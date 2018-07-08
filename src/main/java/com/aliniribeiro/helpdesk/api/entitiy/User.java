package com.aliniribeiro.helpdesk.api.entitiy;

import com.aliniribeiro.helpdesk.api.enums.ProfileEnum;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "useraccount")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String Id;

    @Indexed(unique = true)
    @NotBlank(message = "Email required")
    @Email(message = "Email invalid")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Password required")
    @Size(min = 6)
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile", nullable = false)
    private ProfileEnum profile;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProfileEnum getProfile() {
        return profile;
    }

    public void setProfile(ProfileEnum profile) {
        this.profile = profile;
    }

}
