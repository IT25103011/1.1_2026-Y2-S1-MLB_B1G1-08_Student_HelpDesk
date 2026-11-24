package org.example.student_university_helpdesk.model.ResourceManagement;

import jakarta.persistence.*;

@Entity
@Table(name="Resources")
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="resource_Id")
    private Long resourceId;
    @Column(name="Resource_Type",nullable = false)
    private String resourceType;
    @Column(name="Location",nullable = false)
    private String location;
    @Column(name = "availability",nullable = false)
    private Boolean availability;

    public Resources() {
    }

    public Resources(String resourceType, String location, Boolean availability) {
        this.resourceType = resourceType;
        this.location = location;
        this.availability = availability;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}


