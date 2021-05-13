package jp.sharepic.sharepicback.domains.info;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository extends CrudRepository<InfoEntity, String> {

}
