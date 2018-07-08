package com.aliniribeiro.helpdesk.api.controller;

import com.aliniribeiro.helpdesk.api.common.Response;
import com.aliniribeiro.helpdesk.api.entitiy.User;
import com.aliniribeiro.helpdesk.api.enums.ActionTypes;
import com.aliniribeiro.helpdesk.api.service.UserService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> createUser(HttpServletRequest request, @RequestBody User user, BindingResult result) {

        Response<User> response = new Response<User>();
        try {
            validateUser(user, result, ActionTypes.INSERT);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted = (User) userService.createOrUpdate(user);
            response.setData(userPersisted);

        } catch (DuplicateKeyException de) {
            response.getErrors().add("Email already exists!");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> updateUser(HttpServletRequest request, @RequestBody User user, BindingResult result) {
        Response<User> response = new Response<User>();

        try {
            validateUser(user, result, ActionTypes.UPDATE);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted = (User) userService.createOrUpdate(user);
            response.setData(userPersisted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") String id) {
        Response<User> response = new Response<User>();

        User user = userService.findById(id);
        if (user == null){
            response.getErrors().add("Registered id not found: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        Response<String> response = new Response<String>();
        User user = userService.findById(id);
        if (user == null){
            response.getErrors().add("Registered id not found: " + id);
            return ResponseEntity.badRequest().body(response);
        }
        userService.delete(id);
        return ResponseEntity.ok(new Response<String>());

    }


    private void validateUser(User user, BindingResult result, ActionTypes actionType) {
        if (actionType == ActionTypes.UPDATE && user.getId() == null) {
            result.addError(new ObjectError("User", "Id not found!"));
        }

        if (user.getEmail() == null) {
            result.addError(new ObjectError("User", "Email not found!"));
        }
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public  ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count ){
        Response<Page<User>> response = new Response<Page<User>>();
        Page<User> users = userService.findAll(page, count);
        response.setData(users);
        return ResponseEntity.ok(response);
    }
}
