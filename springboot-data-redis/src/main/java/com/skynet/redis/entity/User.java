package com.skynet.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String userName;
    private String address;
    private Integer age;

    public User(Integer userId, String userName, String address, Integer age) {
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.age = age;
    }

}
