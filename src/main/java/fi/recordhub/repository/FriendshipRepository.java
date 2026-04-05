package fi.recordhub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.Friendship;
import fi.recordhub.model.FriendshipStatus;

// This repository interface provides methods for managing friendships between users. It allows finding friendships by recipient and status and checking if a friendship exists.
public interface FriendshipRepository extends CrudRepository<Friendship, Long> {
	List<Friendship> findByRecipientAndStatus(AppUser recipient, FriendshipStatus status);

	List<Friendship> findByStatusAndRequesterOrStatusAndRecipient(
		FriendshipStatus requesterStatus,
		AppUser requester,
		FriendshipStatus recipientStatus,
		AppUser recipient
	);

	boolean existsByRequesterAndRecipient(AppUser requester, AppUser recipient);

	boolean existsByRequesterAndRecipientOrRequesterAndRecipient(
		AppUser requester,
		AppUser recipient,
		AppUser reverseRequester,
		AppUser reverseRecipient
	);
}