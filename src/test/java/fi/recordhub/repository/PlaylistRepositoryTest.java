package fi.recordhub.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.Playlist;

@SpringBootTest
class PlaylistRepositoryTest {

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@BeforeEach
	void setUp() {
		playlistRepository.deleteAll();
		appUserRepository.deleteAll();
	}

	@Test
	void createPlaylistSavesIt() {
		AppUser owner = appUserRepository.save(new AppUser("mila", "mila@example.com", "EncodedPass1", "USER"));
		Playlist savedPlaylist = playlistRepository.save(new Playlist("Road Trip", owner));

		assertThat(savedPlaylist.getId()).isNotNull();
		assertThat(playlistRepository.findById(savedPlaylist.getId())).isPresent();
	}

	@Test
	void deletePlaylistRemovesIt() {
		AppUser owner = appUserRepository.save(new AppUser("jane", "jane@example.com", "EncodedPass1", "USER"));
		Playlist savedPlaylist = playlistRepository.save(new Playlist("Study Mix", owner));

		playlistRepository.delete(savedPlaylist);

		assertThat(playlistRepository.findById(savedPlaylist.getId())).isEmpty();
	}

	@Test
	void searchByNameFindsMatchingPlaylists() {
		AppUser owner = appUserRepository.save(new AppUser("otto", "otto@example.com", "EncodedPass1", "USER"));
		playlistRepository.save(new Playlist("Morning Chill", owner));
		playlistRepository.save(new Playlist("Chill Nights", owner));
		playlistRepository.save(new Playlist("Workout Set", owner));

		List<Playlist> results = playlistRepository.findByNameContainingIgnoreCase("chill");

		assertThat(results).hasSize(2);
		assertThat(results).extracting(Playlist::getName)
			.containsExactlyInAnyOrder("Morning Chill", "Chill Nights");
	}
}