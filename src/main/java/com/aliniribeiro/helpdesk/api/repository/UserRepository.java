package com.aliniribeiro.helpdesk.api.repository;

import com.aliniribeiro.helpdesk.api.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

}
