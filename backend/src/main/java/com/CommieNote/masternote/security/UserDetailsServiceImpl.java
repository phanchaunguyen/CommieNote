package com.CommieNote.masternote.security;

import com.CommieNote.masternote.model.User;
import com.CommieNote.masternote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username:" + username + "not found"));

        return UserDetailsImpl.build(user);
    }
}
