package com.tmall.resultVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private String userId;

    private String username;

    private String password;

    private String phoneNum;

    private String email;

    private int role;
}
