package jp.sharepic.sharepicback.domains.base;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class BaseEntityListener {

    private static final String DEFAULT_USER = "system";

    @PrePersist
    public void prePresist(BaseEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        entity.setUpdatedDate(now);

        entity.setCreatedBy(DEFAULT_USER);
        entity.setUpdatedBy(DEFAULT_USER);
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedBy(DEFAULT_USER);
    }

}
