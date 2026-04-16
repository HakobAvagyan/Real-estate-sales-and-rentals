package org.example.service.security;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.mapper.user.UserRegisterMapper;
import org.example.model.User;
import org.example.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final UserRegisterMapper userRegisterMapper;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        UserRegisterDto userRegisterDto = userService.findByEmail(username);
        if (userRegisterDto == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        User user = userRegisterMapper.toUser(userRegisterDto);
        return new SpringUser(user);
    }
}
