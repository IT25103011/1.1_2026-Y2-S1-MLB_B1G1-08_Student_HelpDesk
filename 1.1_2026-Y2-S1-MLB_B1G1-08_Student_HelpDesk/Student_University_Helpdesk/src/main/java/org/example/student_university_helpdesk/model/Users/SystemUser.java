package org.example.student_university_helpdesk.model.Users;

import jakarta.persistence.*;

@Entity
@Table(name="system_users")
@Inheritance(strategy = InheritanceType.JOINED)
public class SystemUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;
    @Column(name="username",nullable = false)
    protected String username;
    @Column(name="password",nullable = false)
    protected String password;
    @Column(name="email",nullable = false,unique = true)
    protected String email;
    @Column(name="first_name",nullable = false)
    protected String firstName;
    @Column(name="last_name",nullable = false)
    protected String lastName;

    public SystemUser() {}

    public SystemUser(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public SystemUser(String username, String password, String email, String firstName, String lastName, String role) {
        this(username, password, email, firstName, lastName);
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
