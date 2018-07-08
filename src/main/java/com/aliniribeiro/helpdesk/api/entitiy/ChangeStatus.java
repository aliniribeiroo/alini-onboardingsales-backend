package com.aliniribeiro.helpdesk.api.entitiy;

import com.aliniribeiro.helpdesk.api.enums.ProfileEnum;
import com.aliniribeiro.helpdesk.api.enums.StatusEnum;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "changestatus")
public class ChangeStatus  implements Serializable {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private String Id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    private User userchange;

    @Column(name = "creationdate", nullable = false)
    private Date creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEnum status;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUserChange() {
        return userchange;
    }

    public void setUserChange(User userChange) {
        this.userchange = userChange;
    }

    public Date getDate() {
        return creationDate;
    }

    public void setDate(Date date) {
        this.creationDate = date;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }



}
