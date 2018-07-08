package com.aliniribeiro.helpdesk;

import com.aliniribeiro.helpdesk.api.entitiy.User;
import com.aliniribeiro.helpdesk.api.enums.ProfileEnum;
import com.aliniribeiro.helpdesk.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HelpdeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpdeskApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String pass = passwordEncoder.encode("123456");
            System.out.println("Senha da aliniiiiiiiiiiiiiii :   " + pass);
          //  initUsers(userRepository, passwordEncoder);
        };
    }

//    private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        User admin = new User();
//        admin.setEmail("admin@helpdesk.com");
//        admin.setPassword(passwordEncoder.encode("123456"));
//        admin.setProfile(ProfileEnum.ROLE_ADMIN);
//
//
//        User find = userRepository.findByEmail("admin@helpdesk.com");
//        if (find == null) {
//            userRepository.save(admin);
//        }
//    }

}