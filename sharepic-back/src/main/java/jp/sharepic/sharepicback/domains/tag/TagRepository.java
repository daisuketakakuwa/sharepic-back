package jp.sharepic.sharepicback.domains.tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, String> {

    @Query(value = "SELECT DISTINCT tag FROM TagEntity tag " + "LEFT JOIN tag.cardTagRelations relations "
            + "LEFT JOIN relations.card card " + "WHERE tag.name = :name ")
    public Optional<TagEntity> findByName(@Param("name") String name);

}