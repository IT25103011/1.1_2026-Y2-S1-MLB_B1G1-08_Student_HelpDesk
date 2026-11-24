package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.ResourceManagement.Bookings;
import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.model.Users.User;
import org.example.student_university_helpdesk.repository.BookingsRepository;
import org.example.student_university_helpdesk.repository.ResourcesRepository;
import org.example.student_university_helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingsService {

    private final BookingsRepository bookingsRepository;
    private final ResourcesRepository resourcesRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingsService(BookingsRepository bookingsRepository,
                           ResourcesRepository resourcesRepository,
                           UserRepository userRepository) {
        this.bookingsRepository = bookingsRepository;
        this.resourcesRepository = resourcesRepository;
        this.userRepository = userRepository;
    }

    // USER SIDE OPERATIONS

    @Transactional
    public Bookings createBooking(Long userId, Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be strictly before end time.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Resources resource = resourcesRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + resourceId));

        if (!resource.getAvailability()) {
            throw new IllegalStateException("Resource is currently marked as unavailable.");
        }

        Bookings booking = new Bookings();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setApprovalStatus("PENDING");

        return bookingsRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        if(!bookingsRepository.existsById(bookingId) ) {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }

        bookingsRepository.deleteById(bookingId);
    }

    @Transactional(readOnly = true)
    public List<Bookings> getBookingsByUserId(Long userId) {
        return bookingsRepository.findByUser_UserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Bookings> getBookingsByUserIdAndStatus(Long userId, String approvalStatus) {
        return bookingsRepository.findByUser_UserIdAndApprovalStatus(userId, approvalStatus);
    }

    // ADMIN / OFFICER SIDE OPERATIONS

    @Transactional
    public Bookings updateApprovalStatus(Long bookingId, String status) {
        Bookings booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        booking.setApprovalStatus(status.toUpperCase());
        return bookingsRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Bookings> getAllBookings() {
        return bookingsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Bookings> getBookingsByResourceId(Long resourceId) {
        return bookingsRepository.findByResource_ResourceId(resourceId);
    }

    @Transactional(readOnly = true)
    public List<Bookings> getBookingsByStatus(String approvalStatus) {
        return bookingsRepository.findByApprovalStatus(approvalStatus);
    }

    @Transactional
    public Bookings getBookingById(Long id) {
        return bookingsRepository.findById(id).orElse(null);
    }
}