package org.example.student_university_helpdesk.model.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="helpdesk_officers")
@PrimaryKeyJoinColumn(name="user_id")
public class HelpdeskOfficer extends SystemUser{
    @Column(name="department",nullable = false)
    private String department;

    public HelpdeskOfficer() {}

    public HelpdeskOfficer(String department) {
        this.department = department;
    }

    public HelpdeskOfficer(String username, String password, String email, String firstName, String lastName, String role, String department) {
        super(username, password, email, firstName, lastName, role);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
