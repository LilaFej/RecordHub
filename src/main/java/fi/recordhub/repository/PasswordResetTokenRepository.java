package fi.recordhub.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import fi.recordhub.model.AppUser;
import fi.recordhub.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
	Optional<PasswordResetToken> findByToken(String token);

	void deleteByUser(AppUser user);

	void deleteByExpirationBefore(LocalDateTime timestamp);
}