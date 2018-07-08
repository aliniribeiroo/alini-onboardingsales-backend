package com.aliniribeiro.helpdesk.api.security.jwt;

import com.aliniribeiro.helpdesk.api.entitiy.User;
import com.aliniribeiro.helpdesk.api.enums.ProfileEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class JwtUserFactory {

	private JwtUserFactory() {

	}

	/**
	 * Converte e gera um Usuário com base nos dados do usuário
	 * 
	 * @param user
	 * @return
	 */
	public static JwtUser create(User user) {
		return new JwtUser(user.getId() , user.getEmail(), user.getPassword(),
				mapToGrantedAuthorities(user.getProfile()));
	}

	/**
	 * Converte o perfil do usuário para o formato utilizado no Spring Security
	 * 
	 * @return uma lista de GrantedAuthority
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profile) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(profile.toString()));
		return authorities;
	}

}
