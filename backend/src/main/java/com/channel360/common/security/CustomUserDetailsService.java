package com.channel360.common.security;

import com.channel360.user.entity.User;
import com.channel360.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailOrId));

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                roles,
                !user.isDeletedFlag()
        );
    }
}
