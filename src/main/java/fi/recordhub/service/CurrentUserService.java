package fi.recordhub.service;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fi.recordhub.model.AppUser;
import fi.recordhub.repository.AppUserRepository;

@Service
public class CurrentUserService {
    
	private final AppUserRepository appUserRepository;

	public CurrentUserService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	public Optional<AppUser> findCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}

		// The security principal only stores the username, so the full user is loaded from the database.
		return appUserRepository.findByUsername(authentication.getName());
	}

	public AppUser getCurrentUser() {
		return findCurrentUser()
			.orElseThrow(() -> {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
					return new IllegalStateException("No authenticated user available");
				}

				return new IllegalStateException("Authenticated user not found: " + authentication.getName());
			});
	}

	public boolean hasCurrentUser() {
		return findCurrentUser().isPresent();
	}

	public boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return false;
		}

		// Guard against stale sessions that reference a user no longer stored in the database.
		if (!hasCurrentUser()) {
			return false;
		}

		return authentication.getAuthorities().stream()
			.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
	}
}