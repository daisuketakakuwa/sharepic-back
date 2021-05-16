package jp.sharepic.sharepicback.domains.tag.response;

import java.util.List;

import jp.sharepic.sharepicback.domains.relation.CardTagRelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {

    private String id;

    private String name;

    private List<CardTagRelEntity> cardTagRelations;

}
