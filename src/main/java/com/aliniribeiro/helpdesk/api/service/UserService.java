package com.aliniribeiro.helpdesk.api.service;

import com.aliniribeiro.helpdesk.api.entitiy.User;
import org.springframework.data.domain.Page;

public interface UserService {

    User findByEmail(String email);

    User createOrUpdate(User user);

    User findById(String id);

    void delete(String id);

    Page<User> findAll(int page, int count);
}
