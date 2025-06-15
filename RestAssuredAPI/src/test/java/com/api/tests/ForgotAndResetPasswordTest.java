package com.api.tests;

import org.testng.annotations.Test;
import com.api.services.AuthService;
import com.api.models.request.ResetPasswordRequest;
import com.api.models.request.LoginRequest; // For optional login verification
import com.api.models.request.SignupRequest; // For optional user creation
import com.api.utils.MailosaurEmailUtility; // Import the utility

import io.restassured.response.Response;
import com.mailosaur.MailosaurException;

import java.io.IOException;
import java.util.Map;

public class ForgotAndResetPasswordTest {

    private AuthService authService = new AuthService();

    // --- Mailosaur Configuration (from your Mailosaur account) ---
    private final String MAILOUSAUR_API_KEY = "YOUR_MAILOSAUR_API_KEY"; // REPLACE THIS WITH YOUR KEY
    private final String MAILOUSAUR_SERVER_ID = "YOUR_MAILOSAUR_SERVER_ID"; // REPLACE THIS WITH YOUR SERVER ID
    private MailosaurEmailUtility mailosaurUtility = new MailosaurEmailUtility(MAILOUSAUR_API_KEY, MAILOUSAUR_SERVER_ID);

    // --- Test Data ---
    private final String BASE_EMAIL_PREFIX = "automation.password.reset"; // Will be made unique by utility
    private final String INITIAL_PASSWORD = "OldSecurePassword123!"; // Password for initial user setup
    private final String NEW_PASSWORD = "NewSecurePassword456!";
    private final String RESET_PASSWORD_EMAIL_SUBJECT = "Password Reset Request"; // EXACT subject your app sends
    // Regex to find the full link in the email body (example, adjust if needed)
    // This assumes the link is within an HTML href attribute.
    private final String EMAIL_LINK_REGEX = "href=\"([^\"]*reset-password\\?token=[^\"]*)\"";
    // Regex to extract token and expiry from the found link (example, adjust if needed)
    private final String TOKEN_AND_EXPIRY_REGEX = "token=([^&]+)&expiry=(\\d+)";
    private final int EMAIL_FETCH_TIMEOUT_SECONDS = 30; // Max time to wait for email

    @Test
    public void testForgotAndResetPasswordFlow() throws IOException, MailosaurException {
        System.out.println("--- Starting Forgot and Reset Password Flow Test ---");

        String testEmailAddress = null;
        try {
            // Step 0: Ensure the user exists. Generate a unique email and signup.
            // This is crucial for a repeatable test if your app requires a registered user
            // to initiate a password reset.
            testEmailAddress = mailosaurUtility.generateUniqueEmailAddress(BASE_EMAIL_PREFIX);
            System.out.println("Using test email for this run: " + testEmailAddress);

            System.out.println("Attempting to sign up new user for the test: " + testEmailAddress);
            SignupRequest signupRequest = SignupRequest.builder()
											.username("ayush12")
											.password("ayush1212")
											.email("testEmailAddress")
											.firstName("Ayush")
											.lastName("Bhatnagar")
											.mobileNumber("9909900908")
											.build();
            Response signupResponse = authService.signup(signupRequest);
            System.out.println("Signup Status: " + signupResponse.getStatusCode());
            System.out.println("Signup Body: " + signupResponse.asPrettyString());
            signupResponse.then().statusCode(200); // Assuming 200 OK for successful signup
            System.out.println("User signed up successfully with initial password.");

            // Step 1: Initiate the forgot password request
            System.out.println("\nRequesting password reset for email: " + testEmailAddress);
            Response forgotPasswordResponse = authService.forgotPassword(testEmailAddress);
            System.out.println("Forgot Password Request Status: " + forgotPasswordResponse.getStatusCode());
            System.out.println("Forgot Password Request Body: " + forgotPasswordResponse.asPrettyString());
            forgotPasswordResponse.then().statusCode(200); // Assuming 200 OK or 202 Accepted

            // Step 2: Use Mailosaur Utility to fetch email and extract token/expiry
            System.out.println("\nFetching email and extracting reset details via MailosaurUtility...");
            Map<String, String> tokenDetails = mailosaurUtility.waitForEmailAndGetResetTokenDetails(
                    testEmailAddress,
                    RESET_PASSWORD_EMAIL_SUBJECT,
                    EMAIL_LINK_REGEX,
                    TOKEN_AND_EXPIRY_REGEX,
                    EMAIL_FETCH_TIMEOUT_SECONDS
            );

            String extractedToken = tokenDetails.get("token");
            long tokenExpiryMillis = Long.parseLong(tokenDetails.get("expiry")); // Convert to long for comparison

            // Step 3: Handle token expiry (production-grade check)
            long currentTimeMillis = System.currentTimeMillis();
            System.out.println("\nCurrent Time (milliseconds): " + currentTimeMillis);

            if (currentTimeMillis > tokenExpiryMillis) {
                System.err.println("ERROR: The extracted token has already expired. Cannot proceed with password reset.");
                org.testng.Assert.fail("Password reset token expired.");
            } else {
                System.out.println("Token is valid. Proceeding with password reset.");
            }

            // Step 4: Prepare and send the reset password request
            System.out.println("\nPreparing Reset Password Request...");
            ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                    .token(extractedToken)
                    .newPassword(NEW_PASSWORD)
                    .confirmPassword(NEW_PASSWORD) // Confirm password must match new password
                    .build();

            System.out.println("Sending Reset Password Request...");
            Response resetPasswordResponse = authService.resetPassword(resetPasswordRequest);
            System.out.println("Reset Password Response Status: " + resetPasswordResponse.getStatusCode());
            System.out.println("Reset Password Response Body: " + resetPasswordResponse.asPrettyString());

            // Assert that the password reset was successful
            resetPasswordResponse.then().statusCode(200); // Assuming 200 OK or 204 No Content for success
            System.out.println("\nPassword reset successful!");

            // Step 5: (Optional but highly recommended) Verify new password by attempting to login
            System.out.println("\nAttempting to login with the new password to verify reset...");
            LoginRequest loginRequest = LoginRequest.builder()
                    .username(testEmailAddress) // Assuming email is used as username for login
                    .password(NEW_PASSWORD)
                    .build();

            Response loginResponse = authService.login(loginRequest);
            System.out.println("Login after Reset Status: " + loginResponse.getStatusCode());
            System.out.println("Login after Reset Body: " + loginResponse.asPrettyString());

            // Assert that login with the new password is successful
            loginResponse.then().statusCode(200); // Adjust based on your login API success status
            System.out.println("Successfully logged in with the new password. Test completed!");

        } catch (Exception e) {
            System.err.println("Test failed due to an exception: " + e.getMessage());
            e.printStackTrace();
            org.testng.Assert.fail("Test failed: " + e.getMessage());
        } finally {
            // Optional: Cleanup the user created for the test, if applicable
            // For example: authService.deleteUser(testEmailAddress);
            // This would require a deleteUser method in AuthService and an endpoint
            System.out.println("--- Forgot and Reset Password Flow Test Finished ---");
        }
    }
}