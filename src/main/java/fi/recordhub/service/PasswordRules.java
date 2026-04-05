package fi.recordhub.service;

public final class PasswordRules {

	public static final String POLICY_MESSAGE = "Password must be at least 8 letters and include at least one uppercase letter.";

	private PasswordRules() {
	}

	public static boolean isValid(String password) {
		return password != null && password.matches("^(?=.*[A-Z])[A-Za-z]{8,}$");
	}
}