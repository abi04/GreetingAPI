package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Value("${secret}")
	private String secret;

	@Value("${claimfield}")
	private String claimfield;

	@Value("${host}")
	private String host;


	TestRestTemplate restTemplate = new TestRestTemplate();

	private String createURLWithPort(String uri) {
		return host + uri;
	}


	private String generateToken(String tokenSecret,String user,Date exp){

		String token = null;

		try {
			Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
			 token = JWT.create()
					.withClaim(claimfield,user)
					.withExpiresAt(exp)
					.sign(algorithm);

		} catch (UnsupportedEncodingException exception){
			//UTF-8 encoding not supported
		} catch (JWTCreationException exception){
			//Invalid Signing configuration / Couldn't convert Claims.
		}

		return token;
	}


	@Test
	public void ApiRouteValidate() throws JSONException {

		HttpHeaders headers = new HttpHeaders();

		String username = "xyz";

		Date current = new Date(System.currentTimeMillis()+ 5*60*1000);
		String token = "Bearer "+ generateToken(secret,username,current);

		headers.add("authorization",token);

		HttpEntity<String> entity = new HttpEntity<String>(null,headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/validate"),
				HttpMethod.GET, entity, String.class);

		JsonObject actualObj =  new JsonParser().parse(response.getBody()).getAsJsonObject();
		String actual = actualObj.get("name").getAsString();


		assertEquals(username, actual);
	}

	@Test
	public void ApiRouteValidateExpiration() throws JSONException, InterruptedException {

		HttpHeaders headers = new HttpHeaders();

		String username = "xyz";

		Date current = new Date(System.currentTimeMillis());
		String token = "Bearer "+ generateToken(secret,username,current);

		headers.add("authorization",token);

		HttpEntity<String> entity = new HttpEntity<String>(null,headers);


		Thread.sleep(10000);


		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/validate"),
				HttpMethod.GET, entity, String.class);


		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	public void ApiRouteValidateInValidateHeader() throws JSONException, InterruptedException {

		HttpHeaders headers = new HttpHeaders();

		String username = "xyz";

		Date current = new Date(System.currentTimeMillis());
		String token = "Bearer "+ generateToken(secret,username,current);


		HttpEntity<String> entity = new HttpEntity<String>(null,headers);


		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/validate"),
				HttpMethod.GET, entity, String.class);


		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	public void ApiRoutecheckInvalidateSecret() throws JSONException, InterruptedException {

		HttpHeaders headers = new HttpHeaders();

		String username = "xyz";

		Date current = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
		String token = "Bearer "+ generateToken("secret",username,current);

		headers.add("authorization",token);

		HttpEntity<String> entity = new HttpEntity<String>(null,headers);


		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/validate"),
				HttpMethod.GET, entity, String.class);


		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

}
