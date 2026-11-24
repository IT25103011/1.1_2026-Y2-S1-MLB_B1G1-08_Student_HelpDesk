package org.example.student_university_helpdesk.model.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="Admin")
@PrimaryKeyJoinColumn(name="user_id")
public class Admin extends SystemUser{
    @Column(name= "access_level",nullable = false)
    private String accessLevel;

    public Admin() {}

    public Admin(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Admin(String username, String password, String email, String firstName, String lastName, String role, String accessLevel) {
        super(username, password, email, firstName, lastName, role);
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}
