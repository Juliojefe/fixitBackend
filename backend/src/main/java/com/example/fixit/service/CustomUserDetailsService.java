package com.example.fixit.service;

import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        List<GrantedAuthority> authorities = new ArrayList<>();
        UserRoles roles = user.getUserRoles();
        if (roles != null) {
            if (Boolean.TRUE.equals(roles.getIsAdmin())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            if (Boolean.TRUE.equals(roles.getIsMechanic())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_MECHANIC"));
            }
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}