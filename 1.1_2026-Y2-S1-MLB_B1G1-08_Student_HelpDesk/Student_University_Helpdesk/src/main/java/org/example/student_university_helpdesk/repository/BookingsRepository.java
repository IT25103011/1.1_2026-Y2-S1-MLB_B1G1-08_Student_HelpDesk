package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.ResourceManagement.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Long> {

    List<Bookings> findByUser_UserId(Long userId);

    List<Bookings> findByResource_ResourceId(Long resourceId);

    List<Bookings> findByApprovalStatus(String approvalStatus);

    List<Bookings> findByUser_UserIdAndApprovalStatus(Long userId, String approvalStatus);
}
