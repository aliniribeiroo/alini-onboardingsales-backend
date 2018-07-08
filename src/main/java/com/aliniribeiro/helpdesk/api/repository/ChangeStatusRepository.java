package com.aliniribeiro.helpdesk.api.repository;

import com.aliniribeiro.helpdesk.api.entitiy.ChangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface ChangeStatusRepository extends JpaRepository<ChangeStatus, String> {

  //  Iterable<ChangeStatus> findByTicketIdOrderByDateDesc(String ticketId);
}
