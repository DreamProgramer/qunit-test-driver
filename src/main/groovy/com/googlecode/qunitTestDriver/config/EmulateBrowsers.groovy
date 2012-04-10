package com.googlecode.qunitTestDriver.config

import com.googlecode.qunitTestDriver.QUnitTestDriver
import com.gargoylesoftware.htmlunit.BrowserVersion

/**
 * If passed in, will cause the test runner to STOP executing just after starting the jetty server.
 * This is useful for debugging 404 errors in your tests.
 */
class EmulateBrowsers implements Configuration{
	BrowserVersion browser;
	
	public EmulateBrowsers(BrowserVersion browser){
		this.browser=browser;
	}
	
	void configure(QUnitTestDriver runner){
		runner.browserVersion(browser)
	}
}
