package com.aliniribeiro.helpdesk.api.controller;

import com.aliniribeiro.helpdesk.api.common.Response;
import com.aliniribeiro.helpdesk.api.dto.Summary;
import com.aliniribeiro.helpdesk.api.entitiy.ChangeStatus;
import com.aliniribeiro.helpdesk.api.entitiy.Ticket;
import com.aliniribeiro.helpdesk.api.entitiy.User;
import com.aliniribeiro.helpdesk.api.enums.ProfileEnum;
import com.aliniribeiro.helpdesk.api.enums.StatusEnum;
import com.aliniribeiro.helpdesk.api.security.jwt.JwtTokenUtil;
import com.aliniribeiro.helpdesk.api.service.TicketService;
import com.aliniribeiro.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {


    @Autowired
    private TicketService ticketService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;


    @PostMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
        Response<Ticket> response = new Response<Ticket>();
        try {
            validateCreateTicket(ticket, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            ticket.setStatus(StatusEnum.getStatus("New"));
            ticket.setUser(userFromRequest(request));
            ticket.setDate(new Date());
            ticket.setNumber(generateNumber());
            Ticket ticketPersisted = (Ticket) ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket,
                                                   BindingResult result) {
        Response<Ticket> response = new Response<Ticket>();
        try {
            validateUpdateTicket(ticket, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            Ticket ticketCurrent = ticketService.findById(ticket.getId());
            ticket.setStatus(ticketCurrent.getStatus());
            ticket.setUser(ticketCurrent.getUser());
            ticket.setDate(ticketCurrent.getDate());
            ticket.setNumber(ticketCurrent.getNumber());
            if(ticketCurrent.getAssignedUser() != null) {
                ticket.setAssignedUser(ticketCurrent.getAssignedUser());
            }
            Ticket ticketPersisted = (Ticket) ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        Response<String> response = new Response<String>();
        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            response.getErrors().add("Register not found id:" + id);
            return ResponseEntity.badRequest().body(response);
        }
        ticketService.delete(id);
        return ResponseEntity.ok(new Response<String>());
    }


    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public  ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable int page, @PathVariable int count) {

        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        User userRequest = userFromRequest(request);
        if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICAL)) {
            tickets = ticketService.listTicket(page, count);
        } else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
            tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public  ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request,
                                                                @PathVariable int page,
                                                                @PathVariable int count,
                                                                @PathVariable Integer number,
                                                                @PathVariable String title,
                                                                @PathVariable String status,
                                                                @PathVariable String priority,
                                                                @PathVariable boolean assigned) {

        title = title.equals("uninformed") ? "" : title;
        status = status.equals("uninformed") ? "" : status;
        priority = priority.equals("uninformed") ? "" : priority;

        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        if(number > 0) {
            tickets = ticketService.findByNumber(page, count, number);
        } else {
            User userRequest = userFromRequest(request);
            if(userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICAL)) {
                if(assigned) {
                    tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority, userRequest.getId());
                } else {
                    tickets = ticketService.findByParameters(page, count, title, status, priority);
                }
            } else if(userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
                tickets = ticketService.findByParametersCurrentUser(page, count, title, status, priority, userRequest.getId());
            }
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> changeStatus(
            @PathVariable("id") String id,
            @PathVariable("status") String status,
            HttpServletRequest request,
            @RequestBody Ticket ticket,
            BindingResult result) {

        Response<Ticket> response = new Response<Ticket>();
        try {
            validateChangeStatus(id, status, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            Ticket ticketCurrent = ticketService.findById(id);
            ticketCurrent.setStatus(StatusEnum.getStatus(status));
            if(status.equals("Assigned")) {
                ticketCurrent.setAssignedUser(userFromRequest(request));
            }
            Ticket ticketPersisted = (Ticket) ticketService.createOrUpdate(ticketCurrent);
            ChangeStatus changeStatus = new ChangeStatus();
            changeStatus.setUserChange(userFromRequest(request));
            changeStatus.setDate(new Date());
            changeStatus.setStatus(StatusEnum.getStatus(status));
            changeStatus.setTicket(ticketPersisted);
            ticketService.createChangeStatus(changeStatus);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/summary")
    public ResponseEntity<Response<Summary>> findChart() {
        Response<Summary> response = new Response<Summary>();
        Summary chart = new Summary();
        int amountNew = 0;
        int amountResolved = 0;
        int amountApproved = 0;
        int amountDisapproved = 0;
        int amountAssigned = 0;
        int amountClosed = 0;
        Iterable<Ticket> tickets = ticketService.findAll();
        if (tickets != null) {
            for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
                Ticket ticket = iterator.next();
                if(ticket.getStatus().equals(StatusEnum.NEW)){
                    amountNew ++;
                }
                if(ticket.getStatus().equals(StatusEnum.RESOLVED)){
                    amountResolved ++;
                }
                if(ticket.getStatus().equals(StatusEnum.APPROVED)){
                    amountApproved ++;
                }
                if(ticket.getStatus().equals(StatusEnum.DISAPPROVED)){
                    amountDisapproved ++;
                }
                if(ticket.getStatus().equals(StatusEnum.ASSIGNED)){
                    amountAssigned ++;
                }
                if(ticket.getStatus().equals(StatusEnum.CLOSED)){
                    amountClosed ++;
                }
            }
        }
        chart.setAmountNew(amountNew);
        chart.setAmountResolved(amountResolved);
        chart.setAmountApproved(amountApproved);
        chart.setAmountDisapproved(amountDisapproved);
        chart.setAmountAssigned(amountAssigned);
        chart.setAmountClosed(amountClosed);
        response.setData(chart);
        return ResponseEntity.ok(response);
    }


    private void validateCreateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getTitle() == null) {
            result.addError(new ObjectError("Ticket", "Title no information"));
            return;
        }
    }

    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }

    private Integer generateNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    private void validateChangeStatus(String id, String status, BindingResult result) {
        if (id == null || id.equals("")) {
            result.addError(new ObjectError("Ticket", "Id no information"));
            return;
        }
        if (status == null || status.equals("")) {
            result.addError(new ObjectError("Ticket", "Status no information"));
            return;
        }
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getId() == null) {
            result.addError(new ObjectError("Ticket", "Id no information"));
            return;
        }
        if (ticket.getTitle() == null) {
            result.addError(new ObjectError("Ticket", "Title no information"));
            return;
        }
    }
}
