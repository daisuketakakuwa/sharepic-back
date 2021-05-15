package jp.sharepic.sharepicback.domains.relation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import jp.sharepic.sharepicback.domains.base.BaseEntity;
import jp.sharepic.sharepicback.domains.card.CardEntity;
import jp.sharepic.sharepicback.domains.tag.TagEntity;

@Entity
@Table(name = "card_tag_rel")
public class CardTagRelEntity extends BaseEntity implements java.io.Serializable {

    private String id;

    private CardEntity card;

    private TagEntity tag;

    @Id
    @Column(name = "id", unique = true, nullable = false, length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    public CardEntity getCard() {
        return card;
    }

    public void setCard(CardEntity card) {
        this.card = card;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

}
