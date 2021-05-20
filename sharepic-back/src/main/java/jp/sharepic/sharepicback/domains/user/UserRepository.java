package jp.sharepic.sharepicback.domains.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

    public Optional<UserEntity> findByName(String name);

}
