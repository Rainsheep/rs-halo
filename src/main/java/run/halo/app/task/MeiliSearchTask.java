package run.halo.app.task;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import run.halo.app.model.entity.Post;
import run.halo.app.service.MeiliSearchService;
import run.halo.app.service.PostService;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "meilisearch.enable", havingValue = "true")
public class MeiliSearchTask {

    private final MeiliSearchService meiliSearchService;

    private final PostService postService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetAllDocuments() {
        List<Post> posts = postService.listAll();
        meiliSearchService.deleteAllDocuments();
        meiliSearchService.addOrUpdateDocuments(posts);
    }
}
