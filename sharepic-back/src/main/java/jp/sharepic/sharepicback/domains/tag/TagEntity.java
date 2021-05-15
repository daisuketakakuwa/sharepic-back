package jp.sharepic.sharepicback.domains.tag;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import jp.sharepic.sharepicback.domains.base.BaseEntity;
import jp.sharepic.sharepicback.domains.relation.CardTagRelEntity;

@Entity
@Table(name = "tag")
public class TagEntity extends BaseEntity implements java.io.Serializable {

    private String id;

    private String name;

    private List<CardTagRelEntity> cardTagRelEntities;

    @Id
    @Column(name = "id", unique = true, nullable = false, length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "tag")
    public List<CardTagRelEntity> getCardTagRelEntities() {
        return cardTagRelEntities;
    }

    public void setCardTagRelEntities(List<CardTagRelEntity> cardTagRelEntities) {
        this.cardTagRelEntities = cardTagRelEntities;
    }

}
