package com.api.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class LoggingFilter implements Filter {

	private static final Logger logger = LogManager.getLogger(LoggingFilter.class);
	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext ctx) {
		
		logRequest(requestSpec);
		Response response = ctx.next(requestSpec, responseSpec);
		logResponse(response);
		
		return response;
	}
	
	public void logRequest(FilterableRequestSpecification requestSpec) {
		logger.info("Base URI: " +requestSpec.getBaseUri());
		logger.info("Header: " +requestSpec.getHeaders());
		logger.info("Payload: " +requestSpec.getBody());
	}
	
	public void logResponse(Response response) {
		logger.info("Status Code: "+response.getStatusCode());
	
		
	}

}
