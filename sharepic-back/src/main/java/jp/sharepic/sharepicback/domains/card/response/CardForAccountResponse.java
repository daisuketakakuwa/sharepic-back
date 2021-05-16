package jp.sharepic.sharepicback.domains.card.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardForAccountResponse {

    private List<CardResponse> favoriteCards;
    private List<CardResponse> yourCards;

}
