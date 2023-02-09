package run.halo.app.service.impl;

import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import run.halo.app.config.MeiliSearchConfiguration;
import run.halo.app.model.entity.Post;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.model.support.MeiliSearchDocument;
import run.halo.app.service.MeiliSearchService;
import run.halo.app.service.PostService;
import run.halo.app.service.assembler.PostAssembler;
import run.halo.app.utils.JsonUtils;


/**
 * MeiliSearch service implementation.
 *
 * @author Rainsheep
 * @date 2023/2/10
 */
@Service
@RequiredArgsConstructor
public class MeiliSearchServiceImpl implements MeiliSearchService {

    private final PostAssembler postAssembler;

    private final MeiliSearchConfiguration meiliSearchConfiguration;

    @Autowired(required = false)
    private Index index;

    @Lazy
    @Resource
    private PostService postService;

    @Override
    public void addOrUpdateDocument(Post post) {
        if (!meiliSearchConfiguration.getEnable()) {
            return;
        }
        // 更新为私密文章时
        if (post.getStatus() == PostStatus.INTIMATE
            && !meiliSearchConfiguration.getIncludeIntimate()) {
            deleteDocument(post.getId());
            return;
        }
        addOrUpdateDocuments(List.of(post));
    }

    @Override
    @SneakyThrows
    public void deleteDocument(Integer postId) {
        if (!meiliSearchConfiguration.getEnable()) {
            return;
        }
        index.deleteDocument(postId.toString());
    }

    @Override
    @SneakyThrows
    public void addOrUpdateDocuments(List<Post> posts) {
        posts = posts.stream().filter(post -> post.getStatus() == PostStatus.PUBLISHED
            || post.getStatus() == PostStatus.INTIMATE).collect(Collectors.toList());

        if (!meiliSearchConfiguration.getIncludeIntimate()) {
            posts.removeIf(post -> post.getStatus() == PostStatus.INTIMATE);
        }
        List<MeiliSearchDocument> documents = postAssembler.convertToMeiliSearchDocuments(posts);
        index.addDocuments(JsonUtils.objectToJson(documents));
    }

    @Override
    @SneakyThrows
    public Page<Post> search(String query, Pageable pageable) {
        if (!meiliSearchConfiguration.getEnable()) {
            throw new UnsupportedOperationException("MeiliSearch is not enabled.");
        }
        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber() * limit;
        SearchRequest searchRequest = new SearchRequest(query, offset, limit);
        SearchResult result = index.search(searchRequest);
        ArrayList<Post> posts = new ArrayList<>(result.getHits().size());
        result.getHits().forEach(hit -> {
            int id = ((Double) hit.getOrDefault("id", -1)).intValue();
            Post post = postService.getByIdOfNullable(id);
            if (post != null) {
                posts.add(post);
            }
        });
        return new PageImpl<>(posts, pageable, result.getEstimatedTotalHits());
    }

    @Override
    @SneakyThrows
    public void deleteAllDocuments() {
        index.deleteAllDocuments();
    }
}
