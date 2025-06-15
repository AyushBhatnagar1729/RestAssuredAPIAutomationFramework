package com.api.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

	private static final Logger logger = LogManager.getLogger();
	
	public  void onTestStart(ITestResult result) {
	    
	  }

	  
	  public  void onTestSuccess(ITestResult result) {
	    // not implemented
	  }

	  
	  public  void onTestFailure(ITestResult result) {
	    // not implemented
	  }

	  
	  public  void onTestSkipped(ITestResult result) {
	    logger.info("Test is skipped!!", result.getMethod().getMethodName());
	  }

	  
	  
	  public  void onStart(ITestContext context) {
	    // not implemented
	  }

	  
	  public  void onFinish(ITestContext context) {
	    // not implemented
	  }
	
}
