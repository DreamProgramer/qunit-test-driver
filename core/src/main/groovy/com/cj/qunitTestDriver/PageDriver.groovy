package com.cj.qunitTestDriver

import java.util.logging.Logger
import java.util.logging.Level
import java.util.ArrayList

import com.gargoylesoftware.htmlunit.IncorrectnessListener
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener
import com.gargoylesoftware.htmlunit.html.HTMLParserListener
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import static org.junit.Assert.assertTrue

class PageDriver {

	private final WebClient webClient
	private HtmlPage page
        private logger

	private WebClient silenceHTMLUnit(WebClient client) {
		// silence all html unit related errors
		// this is a qunit runner, not a html/css/js/http validator

		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
		Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF)

		webClient.setIncorrectnessListener([
				notify: {a, b -> null}
		] as IncorrectnessListener)

		webClient.setJavaScriptErrorListener([
				timeoutError: {a, b, c -> null},
				scriptException: {a, b -> null},
				malformedScriptURL: {a, b, c -> null},
				loadScriptError: {a, b, c -> null}
		] as JavaScriptErrorListener)

		webClient.setHTMLParserListener([
				warning: {a, b, c, d, e, f -> null},
				error: {a, b, c, d, e, f -> null}
		] as HTMLParserListener);

		webClient.setCssErrorHandler(new SilentCssErrorHandler())

		webClient.setThrowExceptionOnFailingStatusCode(false)
		webClient.setThrowExceptionOnScriptError(false)

		return webClient

	}

	public PageDriver(String url, BrowserVersion browserVersion) {
                logger = Logger.getLogger("com.cj.qunitTestDriver.PageDriver");
		webClient = new WebClient(browserVersion)

		// turn off chatty htmlunit - TODO: make configurable
		def silence = true
		if (silence) {
			webClient = silenceHTMLUnit(webClient)
		}

		// I don't know a better way to do the configuration below, using deprecated methods
		webClient.setUseInsecureSSL(true)

		navigateTo(url)
	}

	PageDriver navigateTo(String url) {
		page = (HtmlPage) webClient.getPage(url)
		this
	}

	public PageDriver waitForAjax() {
		webClient.waitForBackgroundJavaScript(3000)
		return this;
	}

	public void waitForTextToBePresent(text, Integer timeout){
		String potentialError = "'"+text+"' didn't show up in "+timeout+" milliseconds."

		int millisToWait = 100

		for(int t=timeout; t>0; t+=-millisToWait) {
                    if (containsText(text)) {
                        return; 
                    }

                    Thread.sleep(millisToWait)
		}

		//never saw the text!
		println page.asText()
		throw new AssertionError(potentialError + page.asText())
	}

	public boolean containsText(String text){
                try {
                    return page.asText().contains(text);

                // There appears to be a bug in HTML Unit where it throws an NPE
                // when attempting convert the page to text. As far as we can 
                // determine, this situation is analagous to the text not existing 
                // so, rather than exploding, return false.
                //
                // See: http://sourceforge.net/p/htmlunit/bugs/891/#663c
                } catch (NullPointerException e) {
                    logger.info("NPE while converting the page to text.")
                    return false;
                }
	}

        public boolean containsText(ArrayList<String> list) {
            for( item in list ) {
                if (containsText(item)) {
                    return true;
                }
            }

            return false;
        }

	public List<DomNode> findElementsByXPath(String xPath) {
		return (List<DomNode>) page.getByXPath(xPath)
	}

	public HtmlPage getPage(){
		return page
	}

}
