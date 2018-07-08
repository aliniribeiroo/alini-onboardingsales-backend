package com.aliniribeiro.helpdesk.api.service.impl;

import com.aliniribeiro.helpdesk.api.entitiy.ChangeStatus;
import com.aliniribeiro.helpdesk.api.entitiy.Ticket;
import com.aliniribeiro.helpdesk.api.repository.ChangeStatusRepository;
import com.aliniribeiro.helpdesk.api.repository.TicketRepository;
import com.aliniribeiro.helpdesk.api.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ChangeStatusRepository changeStatusRepository;

    @Override
    public Ticket createOrUpdate(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket findById(String id) {
        return ticketRepository.findOne(id);
    }

    @Override
    public void delete(String id) {
        ticketRepository.delete(id);
    }

    @Override
    public Page<Ticket> listTicket(int page, int count) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findAll(pages);
    }

    @Override
    public ChangeStatus createChangeStatus(ChangeStatus status) {
        return changeStatusRepository.save(status);
    }

    @Override
    public Iterable<ChangeStatus> listChangeStatus(String ticketId) {
        return changeStatusRepository.findByTicketOrderByCreationDateDesc(ticketId);
    }

    @Override
    public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByUserIdOrderByDateDesc(pages, userId);
    }

    @Override
    public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title, status, priority, pages);
    }

    @Override
    public Page<Ticket> findByParametersCurrentUser(int page, int count, String title, String status, String priority, String user) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title, status, priority, pages);
    }

    @Override
    public Page<Ticket> findByNumber(int page, int count, Integer number) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByNumber(number, pages);
    }

    @Override
    public Iterable<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Page<Ticket> findByParametersAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUserId) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(title, status, priority, pages);
    }
}
