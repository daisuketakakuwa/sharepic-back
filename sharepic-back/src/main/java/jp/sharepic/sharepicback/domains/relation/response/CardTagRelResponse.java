package jp.sharepic.sharepicback.domains.relation.response;

import jp.sharepic.sharepicback.domains.card.response.CardResponse;
import jp.sharepic.sharepicback.domains.tag.response.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardTagRelResponse {

    private String id;

    private CardResponse card;

    private TagResponse tag;

}
