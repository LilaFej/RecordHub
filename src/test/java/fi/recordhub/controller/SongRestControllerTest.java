package fi.recordhub.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import fi.recordhub.model.Song;
import fi.recordhub.repository.AppUserRepository;
import fi.recordhub.repository.PlaylistRepository;
import fi.recordhub.repository.SongRepository;

@SpringBootTest
class SongRestControllerTest {

	@Autowired
	private SongRestController songRestController;

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@BeforeEach
	void setUp() {
		songRepository.deleteAll();
		playlistRepository.deleteAll();
		appUserRepository.deleteAll();
	}

	@Test
	void getAllSongsReturnsSavedSongs() throws Exception {
		songRepository.save(new Song("Numb", "Linkin Park", "Meteora", 2003, "Rock", null));
		songRepository.save(new Song("Halo", "Beyonce", "I Am... Sasha Fierce", 2008, "Pop", null));

		assertThat(songRestController.getAllSongs()).hasSize(2);
		assertThat(songRestController.getAllSongs())
			.extracting(Song::getTitle)
			.containsExactlyInAnyOrder("Numb", "Halo");
	}

	@Test
	void getSongByIdReturnsSavedSong() throws Exception {
		Song savedSong = songRepository.save(new Song("Zombie", "The Cranberries", "No Need to Argue", 1994, "Rock", null));
		ResponseEntity<Song> response = songRestController.getSongById(savedSong.getId());

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getTitle()).isEqualTo("Zombie");
		assertThat(response.getBody().getArtist()).isEqualTo("The Cranberries");
	}
}