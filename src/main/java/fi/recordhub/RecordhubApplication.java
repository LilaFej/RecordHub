package fi.recordhub;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.Playlist;
import fi.recordhub.model.Song;
import fi.recordhub.repository.AppUserRepository;
import fi.recordhub.repository.PlaylistRepository;
import fi.recordhub.repository.SongRepository;

@SpringBootApplication
public class RecordhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecordhubApplication.class, args);
	}

	@Bean
	CommandLineRunner seedSongs(
		AppUserRepository appUserRepository,
		PlaylistRepository playlistRepository,
		SongRepository songRepository,
		PasswordEncoder passwordEncoder
	) {
		return args -> {
			// Small starter dataset available for a fresh database.
			if (appUserRepository.count() > 0) {
				return;
			}

			AppUser admin = appUserRepository.save(new AppUser("admin", "admin@recordhub.local", passwordEncoder.encode("AdminOnly"), "ADMIN"));

			Playlist adminArchive = playlistRepository.save(new Playlist("Admin Archive", admin));
			Playlist adminFavorites = playlistRepository.save(new Playlist("Admin Favorites", admin));

			songRepository.save(new Song("It's Plenty", "Burna Boy", "Love, Damini", 2022, "Afrobeat", adminFavorites));
			songRepository.save(new Song("Billie Jean", "Michael Jackson", "Thriller", 1982, "Pop", adminArchive));
			songRepository.save(new Song("Hold up", "Beyonce", "Lemonade", 2016, "Raggae", adminFavorites));
		};
	}

}
