package fi.recordhub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.Playlist;

public interface PlaylistRepository extends CrudRepository<Playlist, Long> {
	List<Playlist> findAll();

	List<Playlist> findByOwner(AppUser owner);

	List<Playlist> findByNameContainingIgnoreCase(@Param("name") String name);
}