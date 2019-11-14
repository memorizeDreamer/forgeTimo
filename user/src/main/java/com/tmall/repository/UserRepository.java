package com.tmall.repository;

import com.tmall.Entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface UserRepository extends CrudRepository<User,Integer> {
    User findUserByUsernameAndPassword(String username,String password);

    User findUserByMobileAndPassword(String mobileNum, String password);

    User findUserByUsername(String username);

    User findUserByMobile(String mobileNum);

    User findUserById(int id);

    @Transactional
    @Modifying
    @Query("update tmall_user set update_date = ?1 where username = ?2")
    int updateTime(Long updateTime, String username);

    @Transactional
    @Modifying
    @Query("update tmall_user set update_date = ?1 , password =?2 where username = ?3")
    int updatePassword(Long updateTime, String password, String username);

    @Transactional
    @Modifying
    @Query("update tmall_user set user_image = ?1 , update_date =?2 where username = ?3")
    int updateUserImage(String userImage, Long updateTime, String username);

    @Transactional
    @Modifying
    @Query("update tmall_user set username=?1 ,email=?2, update_date=?3 where id = ?4")
    int updateUserInfo(String username,String email, Long updateTime, int id);
}
