package fi.recordhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fi.recordhub.model.AppUser;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
	List<AppUser> findAll();

	Optional<AppUser> findByUsername(String username);

	Optional<AppUser> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}