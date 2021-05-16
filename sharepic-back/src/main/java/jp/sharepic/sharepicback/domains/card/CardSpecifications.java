package jp.sharepic.sharepicback.domains.card;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CardSpecifications {

    public Specification<CardEntity> containsPoster(String freeword) {
        return StringUtils.isBlank(freeword) ? null
                : (root, query, builder) -> builder.like(root.get("postUser"), "%" + freeword + "%");
    }

    public Specification<CardEntity> containsDescription(String freeword) {
        return StringUtils.isBlank(freeword) ? null
                : (root, query, builder) -> builder.like(root.get("description"), "%" + freeword + "%");
    }

}
