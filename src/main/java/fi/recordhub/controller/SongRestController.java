package fi.recordhub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.recordhub.model.Song;
import fi.recordhub.repository.SongRepository;

@RestController
@RequestMapping("/api/songs")
public class SongRestController {

	// This REST controller provides API endpoints for fetching songs.
	private final SongRepository songRepository;

	public SongRestController(SongRepository songRepository) {
		this.songRepository = songRepository;
	}

	@GetMapping
	public List<Song> getAllSongs() {
		return songRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Song> getSongById(@PathVariable Long id) {
		return songRepository.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}