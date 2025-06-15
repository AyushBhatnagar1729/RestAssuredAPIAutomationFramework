package com.api.tests;

import org.testng.annotations.Test;

import com.api.models.request.SignupRequest;
import com.api.services.AuthService;

import io.restassured.response.Response;

public class AccountCreationTest {

	@Test
	public void createAccountTest() {
		
		/*
		 * Using Builder pattern in preparing payload increases
		 * readability, flexible i.e. user no needs to remember the 
		 * order of fields and also if the user forgot optional field
		 * then it will not create any issue
		 */
		SignupRequest signupRequest = SignupRequest.builder()
										.username("ayush12")
										.password("ayush1212")
										.email("ayush.bhatnagar1894@gmail.com")
										.firstName("Ayush")
										.lastName("Bhatnagar")
										.mobileNumber("9909900908")
										.build();
		
		AuthService authService = new AuthService();
		Response response = authService.signup(signupRequest);
		System.out.println(response.asPrettyString());
		
	}
}
