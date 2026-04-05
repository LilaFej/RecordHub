package fi.recordhub.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.Playlist;
import fi.recordhub.model.RegistrationForm;
import fi.recordhub.repository.AppUserRepository;
import fi.recordhub.repository.PlaylistRepository;
import fi.recordhub.service.PasswordRules;

@Controller
public class AuthController {
	// The registration form is very basic and does simple validation. 

	private final AppUserRepository appUserRepository;
	private final PlaylistRepository playlistRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthController(
		AppUserRepository appUserRepository,
		PlaylistRepository playlistRepository,
		PasswordEncoder passwordEncoder
	) {
		this.appUserRepository = appUserRepository;
		this.playlistRepository = playlistRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		if (!model.containsAttribute("registrationForm")) {
			model.addAttribute("registrationForm", new RegistrationForm());
		}
		model.addAttribute("passwordPolicy", PasswordRules.POLICY_MESSAGE);
		return "register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute RegistrationForm registrationForm, Model model) {
		model.addAttribute("passwordPolicy", PasswordRules.POLICY_MESSAGE);

		String username = registrationForm.getUsername() == null ? "" : registrationForm.getUsername().trim();
		String email = registrationForm.getEmail() == null ? "" : registrationForm.getEmail().trim().toLowerCase();
		String password = registrationForm.getPassword() == null ? "" : registrationForm.getPassword().trim();
		String confirmPassword = registrationForm.getConfirmPassword() == null ? "" : registrationForm.getConfirmPassword().trim();

		if (username.isBlank()) {
			model.addAttribute("usernameError", "Choose a username.");
			model.addAttribute("registrationForm", registrationForm);
			return "register";
		}

		if (email.isBlank()) {
			model.addAttribute("emailError", "Enter your email address.");
			model.addAttribute("registrationForm", registrationForm);
			return "register";
		}

		if (appUserRepository.existsByEmail(email)) {
			model.addAttribute("emailError", "That email is already in use.");
			model.addAttribute("registrationForm", registrationForm);
			return "register";
		}

		if (appUserRepository.existsByUsername(username)) {
			model.addAttribute("usernameError", "That username is already in use.");
			model.addAttribute("registrationForm", registrationForm);
			return "register";
		}

		if (!PasswordRules.isValid(password)) {
			model.addAttribute("passwordError", PasswordRules.POLICY_MESSAGE);
			model.addAttribute("registrationForm", registrationForm);
			return "register";
		}

		if (!password.equals(confirmPassword)) {
			model.addAttribute("passwordError", "Passwords do not match.");
			model.addAttribute("registrationForm", registrationForm);
			return "register";
		}

		AppUser user = appUserRepository.save(new AppUser(username, email, passwordEncoder.encode(password), "USER"));
		playlistRepository.save(new Playlist(username + "'s Favorites", user));
		return "redirect:/login?registered";
	}
}