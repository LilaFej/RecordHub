package fi.recordhub.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.Friendship;
import fi.recordhub.model.FriendshipStatus;
import fi.recordhub.repository.AppUserRepository;
import fi.recordhub.repository.FriendshipRepository;
import fi.recordhub.repository.PlaylistRepository;
import fi.recordhub.service.CurrentUserService;

@Controller
public class FriendController {

	// This controller handles friend requests and viewing friends' lists.
	private final FriendshipRepository friendshipRepository;
	private final AppUserRepository appUserRepository;
	private final PlaylistRepository playlistRepository;
	private final CurrentUserService currentUserService;

	public FriendController(
		FriendshipRepository friendshipRepository,
		AppUserRepository appUserRepository,
		PlaylistRepository playlistRepository,
		CurrentUserService currentUserService
	) {
		this.friendshipRepository = friendshipRepository;
		this.appUserRepository = appUserRepository;
		this.playlistRepository = playlistRepository;
		this.currentUserService = currentUserService;
	}

	@GetMapping("/friends/list")
	public String showFriends(Model model) {
		AppUser currentUser = currentUserService.getCurrentUser();
		List<Friendship> pendingRequests = friendshipRepository.findByRecipientAndStatus(currentUser, FriendshipStatus.PENDING);
		List<Friendship> acceptedFriendships = friendshipRepository.findByStatusAndRequesterOrStatusAndRecipient(
			FriendshipStatus.ACCEPTED,
			currentUser,
			FriendshipStatus.ACCEPTED,
			currentUser
		);

		List<AppUser> candidateUsers = appUserRepository.findAll().stream()
			.filter(user -> !user.getId().equals(currentUser.getId()))
			.filter(user -> "USER".equals(user.getRole()))
			.filter(user -> !friendshipRepository.existsByRequesterAndRecipientOrRequesterAndRecipient(currentUser, user, user, currentUser))
			.collect(Collectors.toList());

		model.addAttribute("pendingRequests", pendingRequests);
		model.addAttribute("acceptedFriendships", acceptedFriendships);
		model.addAttribute("candidateUsers", candidateUsers);
		return "friends";
	}

	@PostMapping("/friends/request/{userId}")
	public String requestFriend(@PathVariable Long userId) {
		AppUser currentUser = currentUserService.getCurrentUser();
		AppUser targetUser = appUserRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + userId));

		if (!currentUser.getId().equals(targetUser.getId())
			&& !friendshipRepository.existsByRequesterAndRecipientOrRequesterAndRecipient(currentUser, targetUser, targetUser, currentUser)) {
			friendshipRepository.save(new Friendship(currentUser, targetUser, FriendshipStatus.PENDING, java.time.LocalDateTime.now()));
		}

		return "redirect:/friends/list";
	}

	@PostMapping("/friends/accept/{requestId}")
	public String acceptFriendRequest(@PathVariable Long requestId) {
		AppUser currentUser = currentUserService.getCurrentUser();
		Friendship friendship = friendshipRepository.findById(requestId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid friendship request id: " + requestId));

		if (!friendship.getRecipient().getId().equals(currentUser.getId())) {
			throw new IllegalArgumentException("Request does not belong to current user");
		}

		friendship.setStatus(FriendshipStatus.ACCEPTED);
		friendshipRepository.save(friendship);
		return "redirect:/friends/list";
	}

	@GetMapping("/friends/{friendId}/playlists")
	public String showFriendPlaylists(@PathVariable Long friendId, Model model) {
		AppUser currentUser = currentUserService.getCurrentUser();
		AppUser friend = appUserRepository.findById(friendId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid friend id: " + friendId));

		boolean canView = friendshipRepository.existsByRequesterAndRecipientOrRequesterAndRecipient(currentUser, friend, friend, currentUser)
			&& friendshipRepository.findByStatusAndRequesterOrStatusAndRecipient(
				FriendshipStatus.ACCEPTED,
				currentUser,
				FriendshipStatus.ACCEPTED,
				currentUser
			).stream()
			.anyMatch(friendship -> friendship.getRequester().getId().equals(friend.getId()) || friendship.getRecipient().getId().equals(friend.getId()));

		if (!canView) {
			throw new IllegalArgumentException("Friend playlists are not available");
		}

		model.addAttribute("friend", friend);
		model.addAttribute("playlists", playlistRepository.findByOwner(friend));
		return "friendplaylists";
	}
}