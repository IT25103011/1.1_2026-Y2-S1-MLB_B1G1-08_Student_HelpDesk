package org.example.student_university_helpdesk.repository;

import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long>
{
    Optional<Resources> findByResourceId(Long id);

    List<Resources> findByResourceType(String resourceType);

    List<Resources> findByAvailability(boolean availability);

    List<Resources> findByAvailabilityTrue();
}
