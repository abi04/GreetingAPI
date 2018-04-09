package com.example.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pojo.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Abi on 4/7/18.
 */
@RestController
public class JWTAuthorizationController {


    @Value("${secret}")
    private String secret;

    @Value("${headerName}")
    private String headerName;

    @Value("${tokenStartsWith}")
    private String headerStartsWith;

    @Value("${claimfield}")
    private String claimName;



    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    public Token home(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(headerName);

          Token tokenobj = new Token();


            if (authHeader == null || !authHeader.startsWith(headerStartsWith)) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT);
            }
            else {

                final String token = authHeader.substring(7);

                try {
                    Algorithm algorithm = Algorithm.HMAC256(secret);
                    JWTVerifier verifier = JWT.require(algorithm)
                            .acceptExpiresAt(5)
                            .build(); //Reusable verifier instance
                    DecodedJWT jwt = verifier.verify(token);
                    tokenobj.setUserID(jwt.getClaim(claimName).asString());
                    tokenobj.setExpirationTime(jwt.getExpiresAt());

                } catch (UnsupportedEncodingException exception){
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,exception.getMessage());
                    //UTF-8 encoding not supported
                } catch (JWTVerificationException exception){
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,exception.getMessage());
                   }

            }

            return tokenobj;


    }

}
