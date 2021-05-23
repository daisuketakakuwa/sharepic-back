package jp.sharepic.sharepicback.domains.card;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, String>, JpaSpecificationExecutor<CardEntity> {

    @Query(value = "SELECT DISTINCT card FROM CardEntity card WHERE card.src = :src")
    public Optional<CardEntity> findBySrc(@Param("src") String src);

    @Query(value = "SELECT DISTINCT card FROM CardEntity card " + "LEFT JOIN card.cardTagRelations relations "
            + "LEFT JOIN relations.tag " + "WHERE card.postUser = :postUser ORDER BY card.postDate DESC ")

    public List<CardEntity> findByPostUser(@Param("postUser") String postUser);

}
