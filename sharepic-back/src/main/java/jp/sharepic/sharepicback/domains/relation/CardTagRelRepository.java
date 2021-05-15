package jp.sharepic.sharepicback.domains.relation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTagRelRepository extends CrudRepository<CardTagRelEntity, String> {

}
