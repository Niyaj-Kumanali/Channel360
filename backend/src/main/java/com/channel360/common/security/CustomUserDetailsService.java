package com.channel360.common.security;

import com.channel360.common.exception.ResourceNotFoundException;
import com.channel360.user.domain.User;
import com.channel360.user.infrastructure.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SecurityUserProvider securityUserProvider;

    public CustomUserDetailsService(UserRepository userRepository, SecurityUserProvider securityUserProvider) {
        this.userRepository = userRepository;
        this.securityUserProvider = securityUserProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", emailOrId));

        Set<String> roles = userRepository.findRoleNamesByUserId(user.getId());
        Set<String> permissions = securityUserProvider.findPermissionNamesByUserId(user.getId());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                roles,
                !user.isDeletedFlag(),
                permissions
        );
    }
}
