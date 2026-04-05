package fi.recordhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fi.recordhub.model.ResetPasswordForm;
import fi.recordhub.model.ResetRequestForm;
import fi.recordhub.service.PasswordResetService;
import fi.recordhub.service.PasswordRules;

@Controller
public class PasswordResetController {

	// This controller handles the password reset flow.
	private final PasswordResetService passwordResetService;

	public PasswordResetController(PasswordResetService passwordResetService) {
		this.passwordResetService = passwordResetService;
	}

	@GetMapping("/reset/request")
	public String showResetRequest(Model model) {
		if (!model.containsAttribute("resetRequestForm")) {
			model.addAttribute("resetRequestForm", new ResetRequestForm());
		}
		return "reset-request";
	}

	@PostMapping("/reset/request")
	public String requestReset(@ModelAttribute ResetRequestForm resetRequestForm, Model model) {
		passwordResetService.createResetTokenAndSendMail(resetRequestForm.getEmail() == null ? "" : resetRequestForm.getEmail().trim().toLowerCase());
		model.addAttribute("message", "If that email exists, a reset link has been sent.");
		model.addAttribute("resetRequestForm", new ResetRequestForm());
		return "reset-request";
	}

	@GetMapping("/reset/confirm")
	public String confirmReset(@RequestParam String token, Model model) {
		try {
			passwordResetService.requireValidToken(token);
			ResetPasswordForm form = new ResetPasswordForm();
			form.setToken(token);
			model.addAttribute("resetPasswordForm", form);
			model.addAttribute("passwordPolicy", PasswordRules.POLICY_MESSAGE);
			return "reset-confirm";
		} catch (IllegalArgumentException exception) {
			model.addAttribute("error", exception.getMessage());
			model.addAttribute("resetRequestForm", new ResetRequestForm());
			return "reset-request";
		}
	}

	@PostMapping("/reset/save")
	public String saveReset(@ModelAttribute ResetPasswordForm resetPasswordForm, Model model) {
		model.addAttribute("passwordPolicy", PasswordRules.POLICY_MESSAGE);

		if (!PasswordRules.isValid(resetPasswordForm.getPassword())) {
			model.addAttribute("error", PasswordRules.POLICY_MESSAGE);
			model.addAttribute("resetPasswordForm", resetPasswordForm);
			return "reset-confirm";
		}

		if (!resetPasswordForm.getPassword().equals(resetPasswordForm.getConfirmPassword())) {
			model.addAttribute("error", "Passwords do not match.");
			model.addAttribute("resetPasswordForm", resetPasswordForm);
			return "reset-confirm";
		}

		try {
			passwordResetService.resetPassword(resetPasswordForm.getToken(), resetPasswordForm.getPassword());
			return "redirect:/login?resetSuccess";
		} catch (IllegalArgumentException exception) {
			model.addAttribute("error", exception.getMessage());
			model.addAttribute("resetRequestForm", new ResetRequestForm());
			return "reset-request";
		}
	}
}