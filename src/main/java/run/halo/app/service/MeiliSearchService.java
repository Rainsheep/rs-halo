package run.halo.app.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import run.halo.app.model.entity.Post;

/**
 * MeiliSearch service interface.
 *
 * @author Rainsheep
 * @date 2021/8/2
 */
public interface MeiliSearchService {

    void addOrUpdateDocument(Post post);

    void deleteDocument(Integer postId);

    void addOrUpdateDocuments(List<Post> posts);

    void deleteAllDocuments();

    Page<Post> search(String query, Pageable pageable);
}
