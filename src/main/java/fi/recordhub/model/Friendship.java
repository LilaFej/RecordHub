package fi.recordhub.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Friendship {
    // This entity represents a friendship between two users. 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "requester_id")
	private AppUser requester;

	@ManyToOne(optional = false)
	@JoinColumn(name = "recipient_id")
	private AppUser recipient;

	@Enumerated(EnumType.STRING)
	private FriendshipStatus status;

	private LocalDateTime createdAt;

	public Friendship() {
	}

	public Friendship(AppUser requester, AppUser recipient, FriendshipStatus status, LocalDateTime createdAt) {
		this.requester = requester;
		this.recipient = recipient;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AppUser getRequester() {
		return requester;
	}

	public void setRequester(AppUser requester) {
		this.requester = requester;
	}

	public AppUser getRecipient() {
		return recipient;
	}

	public void setRecipient(AppUser recipient) {
		this.recipient = recipient;
	}

	public FriendshipStatus getStatus() {
		return status;
	}

	public void setStatus(FriendshipStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}