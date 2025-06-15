package com.api.utils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;

public class MailosaurEmailUtility {

	private final MailosaurClient mailosaur;
	private final String serverId;
	private final String emailDomain;
	
	
	public MailosaurEmailUtility(String apiKey, String serverId) {
		this.mailosaur = new MailosaurClient(apiKey);
		this.serverId = serverId;
		this.emailDomain = serverId + ".mailosaur.net";
	}
	
	public String generateUniqueEmailAddress(String prefix) {
		return prefix + System.currentTimeMillis() + '@' + this.emailDomain;
	}
	
	public Map<String, String> waitForEmailAndGetResetTokenDetails(
			String recipientEmail,
			String subject,
			String linkPatternRegex,
			String tokenAndExpiryPatternRegex,
			int timeoutSeconds) throws IOException, MailosaurException, InterruptedException {
				
		System.out.println("Polling Mailosaur for email to: " + recipientEmail + " with subject: '" + subject + "'");
		
		Message email = null;
		
		// Use MessageSearchParams for server ID and timeout
        MessageSearchParams searchParams = new MessageSearchParams();
        searchParams.withServer(serverId);
        searchParams.withTimeout(timeoutSeconds * 1000); // Set timeout in milliseconds
		
		try {
			SearchCriteria criteria = new SearchCriteria();
			criteria.withSentTo(recipientEmail);
			criteria.withSubject(subject);
			
			email = mailosaur.messages().get(searchParams, criteria);
			System.out.println("Email found with ID: " + email.id());
        } catch (MailosaurException e) {
            System.err.println("Mailosaur Exception: " + e.getMessage());
            throw new MailosaurException(e);
        }
	
	String resetLink = null;
    String emailBodyHtml = email.html().body(); // Prefer HTML body
    // Fallback to text body if HTML is null or empty
    if (emailBodyHtml == null || emailBodyHtml.isEmpty()) {
        emailBodyHtml = email.text().body();
        System.out.println("Using plain text email body for link extraction as HTML body was empty.");
    }

    if (emailBodyHtml == null || emailBodyHtml.isEmpty()) {
        throw new RuntimeException("Email body (HTML or plain text) is empty. Cannot extract link.");
    }

    Pattern linkPattern = Pattern.compile(linkPatternRegex);
    Matcher linkMatcher = linkPattern.matcher(emailBodyHtml);

    if (linkMatcher.find()) {
        resetLink = linkMatcher.group(1);
        System.out.println("Extracted Reset Link from Email: " + resetLink);
    } else {
        System.err.println("Could not find reset link in the email body using regex: " + linkPatternRegex);
        throw new RuntimeException("Reset link not found in email body.");
    }

    String extractedToken = null;
    String extractedExpiry = null;

    Pattern tokenAndExpiryPattern = Pattern.compile(tokenAndExpiryPatternRegex);
    Matcher tokenAndExpiryMatcher = tokenAndExpiryPattern.matcher(resetLink);

    if (tokenAndExpiryMatcher.find()) {
        extractedToken = URLDecoder.decode(tokenAndExpiryMatcher.group(1), StandardCharsets.UTF_8);
        extractedExpiry = tokenAndExpiryMatcher.group(2); // Keep as String, conversion to long will be in test class
        System.out.println("Extracted Token: " + extractedToken);
        System.out.println("Extracted Token Expiry (raw string): " + extractedExpiry);
    } else {
        System.err.println("Could not extract token and expiry from the reset link: " + resetLink);
        throw new RuntimeException("Token and expiry not found in reset link.");
    }

    // Cleanup: Delete the email after extraction
    mailosaur.messages().delete(email.id()); // Updated: use email.id() to get the message ID for deletion
    System.out.println("Cleaned up email from Mailosaur: " + email.id());
		
		Map<String, String>tokenDetails = new HashMap<>();
		tokenDetails.put("token", extractedToken);
		tokenDetails.put("expiry", extractedExpiry);
		return tokenDetails;
	 }
}
