package com.railway.InRailway.security;

import com.railway.InRailway.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository users;
    public UserDetailsServiceImpl(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) { return users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found")); }
}
