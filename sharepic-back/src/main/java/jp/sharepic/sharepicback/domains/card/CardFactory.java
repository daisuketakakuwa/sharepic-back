package jp.sharepic.sharepicback.domains.card;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.sharepic.sharepicback.domains.card.response.CardResponse;
import jp.sharepic.sharepicback.infra.factory.ResponseFactory;

@Component
public class CardFactory {

    @Autowired
    ResponseFactory responseFactory;

    public List<CardResponse> createCardResponses(List<CardEntity> entities) {

        return responseFactory.map(entities, CardResponse.class);

    }

}
