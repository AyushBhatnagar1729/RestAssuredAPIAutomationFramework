package com.api.base;

import static io.restassured.RestAssured.*;

import com.api.models.request.LoginRequest;
import com.api.filters.LoggingFilter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseService {

	// https://swift.techwithjatin.com
	private static final String baseURL = "http://64.227.160.186:8080";

	private RequestSpecification requestSpecification;
	
	static {
		RestAssured.filters(new LoggingFilter());
	}

	public BaseService() {

		requestSpecification = given().baseUri(baseURL);
	}

	/*
	 * we user protected here for internal reusability and better framework design.
	 */
	protected Response postRequest(Object payload, String endpoint) {

		return requestSpecification.contentType(ContentType.JSON).body(payload).post(endpoint);
	}

	protected Response getRequest(String endpoint) {

		return requestSpecification.get(endpoint);
	}

	protected void setAuthToken(String token) {

		 requestSpecification.header("Authorization", "Bearer "+token);
	}

	protected Response putRequest(Object payload, String endpoint) {

		return requestSpecification.contentType(ContentType.JSON).body(payload).put(endpoint);
	}
}
