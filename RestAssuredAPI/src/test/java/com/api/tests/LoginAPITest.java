package com.api.tests;

import static org.testng.Assert.*;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.services.AuthService;


import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class LoginAPITest {

	
	@Test
	public void loginTest () {
		AuthService authService = new AuthService();
		LoginRequest loginRequest = LoginRequest.builder()
							.username("ayush1234")
							.password("ayush1234")
							.build();
		Response response = authService.login(loginRequest);
		
		/*
		 * here as() method is doing deserialization by internally
		 * using jackson library and for that we should have a pojo
		 * class that exactly mimic the JSON response return by the 
		 * API. SO here the JSON response is deserialized into
		 * LoginResponse pojo object.
		 */
		LoginResponse loginResponse = response.as(LoginResponse.class);
		System.out.println(response.asPrettyString());
		System.out.println(loginResponse.getUsername());
		System.out.println(loginResponse.getRoles());
		
		assertNotNull(loginResponse.getToken());
		assertEquals(loginResponse.getUsername(), "ayush1234");
	}
}
