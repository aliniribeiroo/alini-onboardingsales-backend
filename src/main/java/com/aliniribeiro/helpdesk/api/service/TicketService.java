package com.aliniribeiro.helpdesk.api.service;

import com.aliniribeiro.helpdesk.api.entitiy.ChangeStatus;
import com.aliniribeiro.helpdesk.api.entitiy.Ticket;
import org.springframework.data.domain.Page;

public interface TicketService {

    Ticket createOrUpdate(Ticket ticket);

    Ticket findById(String id);

    void delete(String id);

    Page<Ticket> listTicket(int page, int count);

    ChangeStatus createChangeStatus(ChangeStatus status);

    Iterable<ChangeStatus> listChangeStatus(String ticketId);

    Page<Ticket> findByCurrentUser(int page, int count, String userId);

    Page<Ticket> findByParameters(int page, int count, String title, String status, String priority);

    Page<Ticket> findByParametersCurrentUser(int page, int count, String title, String status, String priority, String user);

    Page<Ticket> findByNumber(int page, int count, Integer number);

    Iterable<Ticket> findAll();

    Page<Ticket> findByParametersAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUserId);
}
