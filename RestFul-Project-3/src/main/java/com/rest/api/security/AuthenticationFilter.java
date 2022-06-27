package com.rest.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.SpringApplicationContext;
import com.rest.api.model.Request.UserLoginRequestModel;
import com.rest.api.service.UserService;
import com.rest.api.sharedClass.DTO.UserDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;

	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			UserLoginRequestModel  cred = new ObjectMapper()
					.readValue(request.getInputStream(), UserLoginRequestModel.class);
			
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
					(cred.getEmail(), cred.getPassword(), new ArrayList<>()));
			
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String userName = ((User)authResult.getPrincipal()).getUsername();
		
		String token = Jwts.builder()
				.setSubject(userName)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512,SecurityConstant.getTokenSecret())
				.compact();
		
		UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
		 UserDTO userDTO = userService.getUser(userName);
		response.addHeader(SecurityConstant.HEADER_STRING, SecurityConstant.TOKEN_PREFIX +token);
		response.addHeader("UserId", userDTO.getUserId());
	}
	
	
	
	
	

}
