package com.manjith.portfolio.security;

import com.manjith.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user by username/email={}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(UserPrincipal::new)
                .orElseThrow(() -> {
                    log.warn("No user found for username/email={}", usernameOrEmail);
                    return new UsernameNotFoundException("No user found for: " + usernameOrEmail);
                });
    }
}
