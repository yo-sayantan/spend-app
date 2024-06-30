package com.finance.sugarmarket.auth.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.finance.sugarmarket.auth.config.UserPrincipal;
import com.finance.sugarmarket.auth.dto.AuthenticationRequest;
import com.finance.sugarmarket.auth.dto.AuthenticationResponse;
import com.finance.sugarmarket.auth.memory.Tokens;
import com.finance.sugarmarket.auth.model.Token;


@Service
public class AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JwtService jwtService;

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
		var jwtToken = jwtService.generateToken(user);
		saveUserToken(user, jwtToken);
		removeUserPassword(user);
		return new AuthenticationResponse(jwtToken, user);
	}

	private void removeUserPassword(UserDetails user) {
		if(user instanceof UserPrincipal) {
			UserPrincipal up = (UserPrincipal) user;
			up.setPassword(null);
			user = up;
		}
	}

	private void saveUserToken(UserDetails user, String jwtToken) {
		Token token = new Token();
		token.setToken(jwtToken);
		token.setUser(user);
		token.setExpired(false);
		Tokens.tokenMap.put(jwtToken, token);
	}
	
	public UserDetails getUserDetailsByToken(String token) throws Exception{
		if(Tokens.tokenMap.get(token) == null) {
			throw new Exception("token not found");
		}
		String userName = jwtService.extractUsername(token);
		UserDetails user = userDetailsService.loadUserByUsername(userName);
		removeUserPassword(user);
		return user;
	}
}