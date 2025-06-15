package com.api.tests;

import org.testng.annotations.Test;

import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.models.response.UserProfileManagementResponse;
import com.api.services.AuthService;
import com.api.services.UserProfileManagementService;

import io.restassured.response.Response;

public class GetProfileRequestTest {

	
	@Test
	public void getProfileInfo() {
		
		LoginRequest loginRequest = LoginRequest.builder()
									.username("ayush1234")
									.password("ayush1234")
									.build();
		AuthService authService = new AuthService();
		Response response = authService.login(loginRequest);
		LoginResponse loginResponse = response.as(LoginResponse.class);
		
		String token = loginResponse.getToken();
		UserProfileManagementService userProfileManagementService = new UserProfileManagementService();
		response = userProfileManagementService.getProfile(token);
		response.as(UserProfileManagementResponse.class);
		System.out.println(response.asPrettyString());
		
		
		
	}
}
