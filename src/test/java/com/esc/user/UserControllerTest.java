package com.esc.user;

import escintegration.EscIntegrationSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static String username = "";
    private static String password = "";

    public void createRandomUserPass() {
        Random random = new Random();
        username = "";
        password = "";
        for(int i = 0; i < 10; i++) {
            int randomInt = 97 + (int)
                    (random.nextFloat() * (26));
            username += (char)randomInt;
            password += (char)randomInt;
        }
    }

    @Test
    public void testServeHomePage() {
        EscIntegrationSuite testHome = new EscIntegrationSuite(port, restTemplate);
        testHome.createEntity();
        testHome.request("GET", "/");
        assertEquals(200, testHome.responseCodeInt());
        assertTrue(testHome.responseBody().contains("<h2>Login</h2>"));
    }

    @Test
    public void testServeSignUpPage() {
        EscIntegrationSuite testSignup = new EscIntegrationSuite(port, restTemplate);
        testSignup.createEntity();
        testSignup.request("GET", "/signup");
        assertEquals(200, testSignup.responseCodeInt());
        assertTrue(testSignup.responseBody().contains("<h2>Sign Up</h2>"));
    }

    @Test
    public void testCreateNewUser() {
        createRandomUserPass();
        EscIntegrationSuite testNewUser = new EscIntegrationSuite(port, restTemplate);
        testNewUser.setBody("username", username);
        testNewUser.setBody("password", password);
        testNewUser.setBody("phoneNumber", "555-555-5555");
        testNewUser.createEntity();
        testNewUser.request("POST", "/signup");
        assertEquals(302, testNewUser.responseCodeInt());
        assertTrue(testNewUser.responseCookieIndex(0).contains("JSESSIONID"));
        testNewUser.request("GET", "/perform_logout");
    }

    @Test
    public void testLogin() {
        createRandomUserPass();
        EscIntegrationSuite testNewUser = new EscIntegrationSuite(port, restTemplate);
        testNewUser.setBody("username", username);
        testNewUser.setBody("password", password);
        testNewUser.setBody("phoneNumber", "555-555-5555");
        testNewUser.createEntity();
        testNewUser.request("POST", "/signup");
        EscIntegrationSuite testLogin = new EscIntegrationSuite(port, restTemplate);
        testLogin.setBody("username", username);
        testLogin.setBody("password", password);
        testLogin.createEntity();
        testLogin.request("POST", "/perform_login");
        assertEquals(302, testLogin.responseCodeInt());
        assertTrue(testLogin.responseCookieIndex(0).contains("JSESSIONID"));
    }

    @Test
    public void testServeProfilePage() {
        createRandomUserPass();
        EscIntegrationSuite testNewUser = new EscIntegrationSuite(port, restTemplate);
        testNewUser.setBody("username", username);
        testNewUser.setBody("password", password);
        testNewUser.setBody("phoneNumber", "555-555-5555");
        testNewUser.createEntity();
        testNewUser.request("POST", "/signup");
        EscIntegrationSuite testProfile = new EscIntegrationSuite(port, restTemplate);
        testProfile.setHeaders("Cookie", testNewUser.responseCookieIndex(0));
        testProfile.resetBody();
        testProfile.createEntity();
        testProfile.resetResponse();
        testProfile.request("GET", "/profile");
        assertEquals(200, testProfile.responseCodeInt());
        assertTrue(testProfile.responseBody().contains("<h2>Create New Message</h2>"));
    }

    @Test
    public void testServeAboutPage() {
        createRandomUserPass();
        EscIntegrationSuite testTeamPage = new EscIntegrationSuite(port, restTemplate);
        testTeamPage.createEntity();
        testTeamPage.request("GET", "/about");
        assertEquals(200, testTeamPage.responseCodeInt());
        assertTrue(testTeamPage.responseBody().contains("<h2>Who are we?</h2>"));
    }

    @Test
    public void testCreateMessage() {
        createRandomUserPass();
        EscIntegrationSuite testNewUser = new EscIntegrationSuite(port, restTemplate);
        testNewUser.setBody("username", username);
        testNewUser.setBody("password", password);
        testNewUser.setBody("phoneNumber", "555-555-5555");
        testNewUser.createEntity();
        testNewUser.request("POST", "/signup");
        EscIntegrationSuite testMessage = new EscIntegrationSuite(port, restTemplate);
        testMessage.setHeaders("Cookie", testNewUser.responseCookieIndex(0));
        testMessage.setBody("day", "2019-01-05");
        testMessage.setBody("time", "10:15");
        testMessage.setBody("rawMessage", "This Is A Test");
        testMessage.createEntity();
        testMessage.request("POST", "/messages");
        EscIntegrationSuite testProfile = new EscIntegrationSuite(port, restTemplate);
        testProfile.setHeaders("Cookie", testNewUser.responseCookieIndex(0));
        testProfile.resetBody();
        testProfile.createEntity();
        testProfile.resetResponse();
        testProfile.request("GET", "/profile");
        assertEquals(200, testProfile.responseCodeInt());
        System.out.println(testProfile.responseBody());
        assertTrue(testProfile.responseBody().contains("<div class=\"message\">"));
        assertTrue(testProfile.responseBody().contains("This Is A Test"));
    }
}