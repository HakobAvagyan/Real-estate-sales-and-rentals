package org.example.service.security;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.user.UserRegisterMapper;
import org.example.model.User;
import org.example.service.UserService;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserRegisterDto userRegisterDto = userService.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, username));
        User user = userRegisterMapper.toUser(userRegisterDto);
        return new SpringUser(user);
    }
}
