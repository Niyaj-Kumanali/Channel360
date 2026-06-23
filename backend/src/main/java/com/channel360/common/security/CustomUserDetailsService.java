package com.channel360.common.security;

import com.channel360.auth.api.AuthFacade;
import com.channel360.auth.api.AuthUserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthFacade authFacade;

    public CustomUserDetailsService(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        AuthUserDto user = authFacade.findByEmail(emailOrId);

        Set<String> roles = user.getRoleNames();
        Set<String> permissions = user.getPermissionNames();

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
