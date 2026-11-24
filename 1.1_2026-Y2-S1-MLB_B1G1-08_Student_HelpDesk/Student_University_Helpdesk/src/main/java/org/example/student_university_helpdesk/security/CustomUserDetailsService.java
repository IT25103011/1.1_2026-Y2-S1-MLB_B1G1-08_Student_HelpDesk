package org.example.student_university_helpdesk.security;

import org.example.student_university_helpdesk.model.Users.Admin;
import org.example.student_university_helpdesk.model.Users.HelpdeskOfficer;
import org.example.student_university_helpdesk.model.Users.SystemUser;
import org.example.student_university_helpdesk.repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;

    @Autowired
    public CustomUserDetailsService(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        SystemUser user = systemUserRepository.findByEmail(identifier)
                .or(() -> systemUserRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username or email: " + identifier));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user instanceof Admin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (user instanceof HelpdeskOfficer) {
            authorities.add(new SimpleGrantedAuthority("ROLE_HELPDESK_OFFICER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
