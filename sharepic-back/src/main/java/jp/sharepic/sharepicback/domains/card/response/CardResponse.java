package jp.sharepic.sharepicback.domains.card.response;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.sharepic.sharepicback.domains.relation.response.CardTagRelResponse;
import jp.sharepic.sharepicback.domains.tag.response.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {

    private String id;

    private String src;

    private String description;

    private String postDate;

    private String postUser;

    @JsonIgnore
    List<CardTagRelResponse> cardTagRelations;

    public List<String> getTags() {
        return cardTagRelations.stream().map(CardTagRelResponse::getTag).map(TagResponse::getName)
                .collect(Collectors.toList());
    }

}
