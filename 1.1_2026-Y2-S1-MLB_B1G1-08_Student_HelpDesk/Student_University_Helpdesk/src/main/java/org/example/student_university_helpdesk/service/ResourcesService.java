package org.example.student_university_helpdesk.service;

import org.example.student_university_helpdesk.model.ResourceManagement.Resources;
import org.example.student_university_helpdesk.repository.ResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourcesService {
    private final ResourcesRepository resourcesRepository;

    @Autowired
    public ResourcesService(ResourcesRepository resourcesRepository) {
        this.resourcesRepository = resourcesRepository;
    }

    //Admin Side
    @Transactional
    public Resources addResource(Resources resources) {
        resources.setAvailability(true);
        return resourcesRepository.save(resources);
    }

    @Transactional
    public Resources updateResource(Long id, Resources Updated) {
        Resources resources = resourcesRepository.findByResourceId(id)
                //throws runtime exception if not found which can trigger rollback of database entries
            .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + id));
        resources.setResourceType(Updated.getResourceType());
        resources.setLocation(Updated.getLocation());
        return resourcesRepository.save(resources);
    }

    @Transactional
    public Resources setResourceAvailability(Long Id, boolean availability) {
        Resources resources = resourcesRepository.findByResourceId(Id)
                .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + Id));
        resources.setAvailability(availability);
        return resourcesRepository.save(resources);
    }

    //User side
    @Transactional(readOnly = true)
    public List<Resources> getAllResources() {
        return resourcesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Resources> getResourceByAvailability(){ return resourcesRepository.findByAvailabilityTrue();}

    @Transactional(readOnly = true)
    public Resources getResourceById(Long id) {
        return resourcesRepository.findById(id).orElse(null);
    }

}
