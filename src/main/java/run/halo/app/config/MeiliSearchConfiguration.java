package run.halo.app.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.exceptions.MeilisearchException;
import com.meilisearch.sdk.model.Settings;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanglongqi
 * @date 2023/2/10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "meilisearch")
public class MeiliSearchConfiguration {

    private Boolean enable;

    private String host;

    private String masterKey;

    private String indexName;

    private Boolean includeIntimate;

    @Bean
    @ConditionalOnProperty(value = "meilisearch.enable", havingValue = "true")
    public Index getIndex() throws MeilisearchException {
        Client client = new Client(new Config(host, masterKey));
        Index index = client.index(indexName);
        // setting
        Settings settings = new Settings();
        settings.setSearchableAttributes(
            new String[] {"title", "content", "summary", "categories", "tags"});
        index.updateSettings(settings);
        return index;
    }
}
