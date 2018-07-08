package com.aliniribeiro.helpdesk.api.security.service;

import com.aliniribeiro.helpdesk.api.entitiy.User;
import com.aliniribeiro.helpdesk.api.security.jwt.JwtUserFactory;
import com.aliniribeiro.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.findByEmail(email);
		
		if (user != null) {
			return JwtUserFactory.create(user);
		}

		throw new UsernameNotFoundException(String.format("No user found with username '%s';", email));
	}

}
