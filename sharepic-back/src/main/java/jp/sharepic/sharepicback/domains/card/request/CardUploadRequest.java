package jp.sharepic.sharepicback.domains.card.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardUploadRequest {

    private String id;
    private String src;
    private String extension;
    private String[] tags;
    private String description;
    private String postDate;
    private String postUser;

}
