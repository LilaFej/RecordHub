package fi.recordhub.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.recordhub.model.Song;

@SpringBootTest
class SongRepositoryTest {

	@Autowired
	private SongRepository songRepository;

	@BeforeEach
	void setUp() {
		songRepository.deleteAll();
	}

	@Test
	void createSongSavesIt() {
		Song savedSong = songRepository.save(new Song("Clocks", "Coldplay", "A Rush of Blood to the Head", 2002, "Rock", null));

		assertThat(savedSong.getId()).isNotNull();
		assertThat(songRepository.findById(savedSong.getId())).isPresent();
	}

	@Test
	void deleteSongRemovesIt() {
		Song savedSong = songRepository.save(new Song("Bad Guy", "Billie Eilish", "When We All Fall Asleep, Where Do We Go?", 2019, "Pop", null));

		songRepository.delete(savedSong);

		assertThat(songRepository.findById(savedSong.getId())).isEmpty();
	}

	@Test
	void searchByTitleFindsMatchingSongs() {
		songRepository.save(new Song("Blinding Lights", "The Weeknd", "After Hours", 2020, "Pop", null));
		songRepository.save(new Song("Lights", "Ellie Goulding", "Bright Lights", 2010, "Pop", null));
		songRepository.save(new Song("Everlong", "Foo Fighters", "The Colour and the Shape", 1997, "Rock", null));

		List<Song> results = songRepository.findByTitleContainingIgnoreCase("light");

		assertThat(results).hasSize(2);
		assertThat(results).extracting(Song::getTitle)
			.containsExactlyInAnyOrder("Blinding Lights", "Lights");
	}
}