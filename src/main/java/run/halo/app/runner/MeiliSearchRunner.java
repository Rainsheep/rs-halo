package run.halo.app.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import run.halo.app.task.MeiliSearchTask;

/**
 * sync data to MeiliSearch when halo start.
 *
 * @author Rainsheep
 * @date 2023/2/10
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "meilisearch.enable", havingValue = "true")
public class MeiliSearchRunner implements ApplicationRunner {

    private final MeiliSearchTask meiliSearchTask;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        meiliSearchTask.resetAllDocuments();
    }
}
