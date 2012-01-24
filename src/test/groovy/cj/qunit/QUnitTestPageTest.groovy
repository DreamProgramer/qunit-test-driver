package cj.qunit

import org.junit.Test

import com.davidron.qunitTestDriver.QUnitJettyTestRunner;
import com.davidron.qunitTestDriver.QUnitTestPage;

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Looking for a demonstration about how to write Javascript Tests?  
 * Check out QUnitDemoIntegration.groovy!
 */
class QUnitTestPageTest {
    final String testPageUrl="src/test/resources/QUnitTestPageTest.html"
    
    @Test
    void pageErrorsAreThrownProperly(){
        boolean exception = false
        try{
            QUnitJettyTestRunner.run(this.class, testPageUrl)
        }catch(AssertionError e){
            assertTrue(e.toString().contains("This is a qunit failure"))
            assertTrue(e.toString().contains("This is another qunit failure"))
            exception=true
        }
        assertTrue("qUnit should have reported back two errors.", exception)
    }
    
    @Test 
    void twoTestsPassAndTwoTestsFail(){
        QUnitJettyTestRunner runner = new QUnitJettyTestRunner(this.class, testPageUrl)
        QUnitTestPage page = runner.getTestPage()
        assertEquals(2,page.passed())
        assertEquals(2,page.failed())
        runner.getServer().stop()
    }
    
    @Test
    void allTestsShouldPass(){
        String noTestPageUrl="src/test/resources/QUnitTestPageWithNoTests.html"
        QUnitJettyTestRunner runner = new QUnitJettyTestRunner(this.class, noTestPageUrl)
        QUnitTestPage page = runner.getTestPage()
        assertEquals(0,page.passed())
        assertEquals(0,page.failed())
        runner.getServer().stop()
        Boolean exception=false
        try{
            page.assertTestsPass()
        }catch(AssertionError e){
            assertTrue(e.toString().contains("no tests found"))
            exception=true
        }
        assertTrue("qUnit should have reported back no tests.", exception)
        
    }

}