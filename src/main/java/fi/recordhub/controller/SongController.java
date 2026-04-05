package fi.recordhub.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fi.recordhub.model.Playlist;
import fi.recordhub.model.Song;
import fi.recordhub.repository.PlaylistRepository;
import fi.recordhub.repository.SongRepository;
import fi.recordhub.service.CurrentUserService;

@Controller
public class SongController {

	private final SongRepository songRepository;
	private final PlaylistRepository playlistRepository;
	private final CurrentUserService currentUserService;

	public SongController(
		SongRepository songRepository,
		PlaylistRepository playlistRepository,
		CurrentUserService currentUserService
	) {
		this.songRepository = songRepository;
		this.playlistRepository = playlistRepository;
		this.currentUserService = currentUserService;
	}

	@GetMapping("/songlist")
	public String showSongList(
		@RequestParam(required = false) String genre,
		@RequestParam(required = false) Long playlistId,
		Model model
	) {
		List<Song> allSongs = songRepository.findAll();
		// The list page supports simple filtering by genre and playlist from the search form.
		List<Song> filteredSongs = allSongs.stream()
			.filter(song -> genre == null || genre.isBlank() || genre.equals(song.getGenre()))
			.filter(song -> playlistId == null || (song.getPlaylist() != null && playlistId.equals(song.getPlaylist().getId())))
			.collect(Collectors.toList());

		model.addAttribute("songs", filteredSongs);
		model.addAttribute("genres", allSongs.stream()
			.map(Song::getGenre)
			.filter(Objects::nonNull)
			.filter(value -> !value.isBlank())
			.distinct()
			.sorted()
			.collect(Collectors.toList()));
		model.addAttribute("searchPlaylists", playlistRepository.findAll().stream()
			.sorted(Comparator.comparing(Playlist::getName, String.CASE_INSENSITIVE_ORDER))
			.collect(Collectors.toList()));
		model.addAttribute("selectedGenre", genre == null ? "" : genre);
		model.addAttribute("selectedPlaylistId", playlistId);
		return "songlist";
	}

	@GetMapping("/addsong")
	public String showAddSongForm(Model model) {
		model.addAttribute("song", new Song());
		model.addAttribute("playlists", currentUserPlaylists());
		return "addsong";
	}

	@PostMapping("/savesong")
	public String saveSong(@ModelAttribute Song song, @RequestParam(required = false) Long playlistId) {
		song.setPlaylist(resolvePlaylist(playlistId));
		songRepository.save(song);
		return "redirect:/songlist";
	}

	@GetMapping("/deletesong/{id}")
	public String deleteSong(@PathVariable Long id) {
		songRepository.deleteById(id);
		return "redirect:/songlist";
	}

	@GetMapping("/editsong/{id}")
	public String showEditSongForm(@PathVariable Long id, Model model) {
		Song song = songRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Invalid song id: " + id));
		model.addAttribute("song", song);
		model.addAttribute("playlists", currentUserPlaylists());
		return "editsong";
	}

	@PostMapping("/updatesong")
	public String updateSong(@ModelAttribute Song song, @RequestParam(required = false) Long playlistId) {
		song.setPlaylist(resolvePlaylist(playlistId));
		songRepository.save(song);
		return "redirect:/songlist";
	}

	private List<Playlist> currentUserPlaylists() {
		return playlistRepository.findByOwner(currentUserService.getCurrentUser());
	}

	private Playlist resolvePlaylist(Long playlistId) {
		if (playlistId == null) {
			return null;
		}

		// Users can only attach songs to playlists they own.
		return playlistRepository.findById(playlistId)
			.filter(playlist -> playlist.getOwner().getId().equals(currentUserService.getCurrentUser().getId()))
			.orElseThrow(() -> new IllegalArgumentException("Invalid playlist id: " + playlistId));
	}
}