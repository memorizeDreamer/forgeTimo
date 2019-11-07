package com.liufeng.repository;

import com.liufeng.entity.UserAccount;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserAccount,Integer> {

    /**
     * 根据用户名查账户信息
     * @param username
     * @return
     */
    UserAccount findUserByUsername(String username);
}
