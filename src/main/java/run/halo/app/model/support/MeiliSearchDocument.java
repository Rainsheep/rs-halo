package run.halo.app.model.support;

import java.util.List;
import lombok.Data;

/**
 * meiliSearch Document
 *
 * @author Rainsheep
 * @date 2023/2/10
 */
@Data
public class MeiliSearchDocument {
    private Integer id;
    private String title;
    private String fullPath;
    private List<String> categories;
    private List<String> tags;
    private String summary;
    private String content;
}
