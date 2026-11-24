package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.Ticket.Ticket;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.repository.HelpdeskOfficerRepository;
import org.example.student_university_helpdesk.repository.TicketRepository;
import org.example.student_university_helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final HelpdeskOfficerRepository officerRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         HelpdeskOfficerRepository officerRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.officerRepository = officerRepository;
    }

    // STUDENT / USER OPERATIONS

    @Transactional
    public Ticket createTicket(Long userId, String subject, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setStatus("OPEN");
        ticket.setSubmittedBy(user);
        ticket.setHandledBy(null);
        ticket.setCreatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findBySubmittedBy_UserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUserIdAndStatus(Long userId, String status) {
        return ticketRepository.findBySubmittedBy_UserIdAndStatus(userId, status.toUpperCase());
    }

    // HELPDESK OFFICER / ADMIN OPERATIONS

    @Transactional
    public Ticket assignOfficer(Long ticketId, Long officerId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        HelpdeskOfficer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + officerId));

        ticket.setHandledBy(officer);
        ticket.setStatus("IN_PROGRESS");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        ticket.setStatus(status.toUpperCase());
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByOfficerId(Long officerId) {
        return ticketRepository.findByHandledBy_UserId(officerId);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status.toUpperCase());
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByOfficerIdAndStatus(Long officerId, String status) {
        return ticketRepository.findByHandledBy_UserIdAndStatus(officerId, status.toUpperCase());
    }
}