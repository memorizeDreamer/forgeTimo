package com.tmall.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "application_info")
public class ApplicationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "client_id")
    private String clientId;

    private String secret;

    @Column(name = "create_time")
    private Long createTime;

    private String username;

    public ApplicationInfo(String clientId, String secret,Long createTime, String username){
        this.clientId = clientId;
        this.createTime = createTime;
        this.secret = secret;
        this.username = username;
    }
}
