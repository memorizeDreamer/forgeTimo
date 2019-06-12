package com.tmall.repository;

import com.tmall.Entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {

    User findUserByUsernameAndPassword(String username,String password);
}
