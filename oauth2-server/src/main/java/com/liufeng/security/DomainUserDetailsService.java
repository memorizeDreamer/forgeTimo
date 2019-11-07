package com.liufeng.security;

import com.liufeng.entity.UserAccount;
import com.liufeng.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户信息服务
 * 实现 Spring Security的UserDetailsService接口方法，用于身份认证
 */
@Service
public class DomainUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userRepository.findUserByUsername(username);
        if (userAccount != null){
            return new User(userAccount.getUsername(),"{noop}"+userAccount.getPassword(),
                    AuthorityUtils.createAuthorityList(String.valueOf(userAccount.getRole()).split(",")));
        }else {
            throw  new UsernameNotFoundException("用户["+username+"]不存在");
        }
    }
}
