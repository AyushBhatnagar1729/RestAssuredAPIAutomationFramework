package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.api.models.request.LoginRequest;
import com.api.models.request.ProfileRequest;
import com.api.models.response.LoginResponse;
import com.api.models.response.UserProfileManagementResponse;
import com.api.services.AuthService;
import com.api.services.UserProfileManagementService;

import io.restassured.response.Response;

public class UpdateProfileTest {

	
	@Test
	public void updateProfile() {
		
		LoginRequest loginRequest = LoginRequest.builder()
									.username("ayush1234")
									.password("ayush1234")
									.build();
		
		AuthService authService = new AuthService();
		Response response = authService.login(loginRequest);
		LoginResponse loginResponse = response.as(LoginResponse.class);
		System.out.println(response.asPrettyString());
		
		System.out.println("==============================================================");
		
		UserProfileManagementService userProfileManagementService = new UserProfileManagementService();
		response = userProfileManagementService.getProfile(loginResponse.getToken());
		System.out.println(response.asPrettyString());
		
		UserProfileManagementResponse userProfileManagementResponse = response.as(UserProfileManagementResponse.class);
		Assert.assertEquals(userProfileManagementResponse.getUsername(), "ayush1234");
		
		System.out.println("==============================================================");
		
		ProfileRequest profileRequest = ProfileRequest.builder()
										.firstName("dia")
										.lastName("bharadwaj")
										.email("dis.bhar@gmail.com")
										.mobileNumber("9938292829")
										.build();
		
		response = userProfileManagementService.updateProfile(loginResponse.getToken(), profileRequest);
		System.out.println(response.asPrettyString());
	
	}
}
