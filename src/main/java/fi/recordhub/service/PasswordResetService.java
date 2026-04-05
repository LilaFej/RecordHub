package fi.recordhub.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.PasswordResetToken;
import fi.recordhub.repository.AppUserRepository;
import fi.recordhub.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {

	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final AppUserRepository appUserRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${app.mail.from}")
	private String fromAddress;

	public PasswordResetService(
		PasswordResetTokenRepository passwordResetTokenRepository,
		AppUserRepository appUserRepository,
		JavaMailSender javaMailSender,
		PasswordEncoder passwordEncoder
	) {
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.appUserRepository = appUserRepository;
		this.javaMailSender = javaMailSender;
		this.passwordEncoder = passwordEncoder;
	}

	public void createResetTokenAndSendMail(String email) {
		// Old reset links are cleaned up before a new one is created.
		passwordResetTokenRepository.deleteByExpirationBefore(LocalDateTime.now());

		appUserRepository.findByEmail(email).ifPresent(user -> {
			passwordResetTokenRepository.deleteByUser(user);
			// Each request replaces the previous token for the same user.
			PasswordResetToken token = passwordResetTokenRepository.save(
				new PasswordResetToken(UUID.randomUUID().toString(), LocalDateTime.now().plusMinutes(15), user)
			);

			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromAddress);
			message.setTo(user.getEmail());
			message.setSubject("RecordHub password reset");
			message.setText("Reset your password using this link within 15 minutes: "
				+ baseUrl + "/reset/confirm?token=" + token.getToken());
			javaMailSender.send(message);
		});
	}

	public PasswordResetToken requireValidToken(String token) {
		PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
			.orElseThrow(() -> new IllegalArgumentException("Invalid reset token."));

		if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
			passwordResetTokenRepository.delete(resetToken);
			throw new IllegalArgumentException("This reset token has expired.");
		}

		return resetToken;
	}

	public void resetPassword(String token, String rawPassword) {
		PasswordResetToken resetToken = requireValidToken(token);
		AppUser user = resetToken.getUser();
		user.setPassword(passwordEncoder.encode(rawPassword));
		appUserRepository.save(user);
		passwordResetTokenRepository.delete(resetToken);
	}
}