package jp.sharepic.sharepicback.domains.card;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import jp.sharepic.sharepicback.domains.base.BaseEntity;
import jp.sharepic.sharepicback.domains.relation.CardTagRelEntity;

@Entity
@Table(name = "card")
public class CardEntity extends BaseEntity implements java.io.Serializable {

    private String id;

    private String src;

    private String description;

    private LocalDateTime postDate;

    private String postUser;

    private List<CardTagRelEntity> cardTagRelEntities;

    @Id
    @Column(name = "id", unique = true, nullable = false, length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "src")
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "post_date")
    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    @Column(name = "post_user")
    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    @OneToMany(mappedBy = "card")
    public List<CardTagRelEntity> getCardTagRelEntities() {
        return cardTagRelEntities;
    }

    public void setCardTagRelEntities(List<CardTagRelEntity> cardTagRelEntities) {
        this.cardTagRelEntities = cardTagRelEntities;
    }

}
