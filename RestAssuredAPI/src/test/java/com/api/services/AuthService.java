package com.api.services;

import java.util.HashMap;
import java.util.Map;

import com.api.base.BaseService;
import com.api.models.request.LoginRequest;
import com.api.models.request.ResetPasswordRequest;
import com.api.models.request.SignupRequest;

import io.restassured.response.Response;

public class AuthService extends BaseService{

	private static final String BASE_PATH = "/api/auth/";
	
	public Response login(LoginRequest payload) {
		
		return postRequest(payload, BASE_PATH+"login");
	}
	
	public Response signup(SignupRequest payload) {
		
		return postRequest(payload, BASE_PATH+"signup");
	}
	
	public Response forgotPassword(String emailAddress) {
		Map<String, String> payload = new HashMap<>();
		payload.put("email", emailAddress);
		
		return postRequest(payload, BASE_PATH+"forgot-password");
	}
	
	public Response resetPassword(ResetPasswordRequest payload) {
		return postRequest(payload, BASE_PATH + "reset-password");
	}
}
