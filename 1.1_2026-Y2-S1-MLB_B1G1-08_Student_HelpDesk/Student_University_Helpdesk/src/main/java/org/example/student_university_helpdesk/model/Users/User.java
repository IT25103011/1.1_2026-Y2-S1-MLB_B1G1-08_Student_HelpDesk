package org.example.student_university_helpdesk.model.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
@PrimaryKeyJoinColumn(name="user_id")
public class User extends SystemUser{
    @Column(name="course",nullable = false)
    private String course;
    @Column(name="department",nullable = false)
    private String department;

    public User(){
        super();
    }

    public User(String department, String course) {
        this.department = department;
        this.course = course;
    }

    public User(String username, String password, String email, String firstName, String lastName, String role, String department, String course) {
        super(username, password, email, firstName, lastName, role);
        this.department = department;
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
