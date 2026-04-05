package fi.recordhub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import fi.recordhub.model.Song;

public interface SongRepository extends CrudRepository<Song, Long> {
	List<Song> findAll();

	List<Song> findByTitleContainingIgnoreCase(@Param("title") String title);
}