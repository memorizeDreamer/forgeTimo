package com.tmall.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tmall_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userid")
    private String userId;

    private String username;

    private String password;

    @Column(name = "update_date")
    private Long updateDate;

    @Column(name = "create_date")
    private Long createDate;

    @Column(name = "phone_num")
    private String phoneNum;

    private String email;

    private int role;
}
