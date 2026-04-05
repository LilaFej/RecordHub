package fi.recordhub.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import fi.recordhub.model.AppUser;
import fi.recordhub.service.CurrentUserService;

@ControllerAdvice
public class GlobalModelAttributes {

	private final CurrentUserService currentUserService;

	public GlobalModelAttributes(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	@ModelAttribute("currentUser")
	public AppUser currentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}

		return currentUserService.findCurrentUser().orElse(null);
	}

	@ModelAttribute("isAdmin")
	public boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return false;
		}

		return currentUserService.isAdmin();
	}
}