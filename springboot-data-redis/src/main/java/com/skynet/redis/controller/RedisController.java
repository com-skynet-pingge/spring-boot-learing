package com.skynet.redis.controller;

import com.alibaba.fastjson.JSONArray;
import com.skynet.redis.entity.User;
import io.micrometer.core.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class RedisController {

    @Resource(name = "myRedisTemplate")
    private RedisTemplate redisTemplate;

    @GetMapping(path = "/string/set")
    public ResponseEntity<String> set(@RequestParam("redisKey") String redisKey) {
        log.info("redisKey:" + redisKey);
        User user = new User();
        user.setUserId(1);
        user.setAge(18);
        user.setUserName("张三");
        user.setAddress("上海市浦东新区唐镇创新中路");
        Object object = JSONArray.toJSON(user);
        String jsonString = object.toString();
        redisTemplate.opsForValue().set(redisKey, jsonString);
        return ResponseEntity.ok("OK");
    }

    @GetMapping(path = "/string/get")
    public ResponseEntity<User> get(@RequestParam("redisKey") String redisKey) {
        log.info("redisKey:" + redisKey);
        String jsonString = (String) redisTemplate.opsForValue().get(redisKey);
        User user = JSONArray.parseObject(jsonString, User.class);
        return ResponseEntity.ok(user);
    }

    @GetMapping(path = "/pipeline/execute")
    public ResponseEntity<List<User>> execute(@RequestParam("redisKey") String redisKey) {

        List<String> redisKeyList = new ArrayList<String>();
        List<User> userList = new ArrayList<User>();

        redisKeyList.add("CONFIG:API");
        redisKeyList.add("CONFIG:APP");

        List<String> stringList = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Nullable
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.openPipeline();

                if(CollectionUtils.isEmpty(redisKeyList)){
                    for (String redisKey : redisKeyList) {
                        System.out.println("redisKey:" + redisKey);
                        connection.get(redisKey.getBytes());
                    }
                }

                List<Object> results = connection.closePipeline();
                System.out.println("results:" + results);
                return null;
            }
        });

        for (String userString : stringList) {
            User user = JSONArray.parseObject(userString, User.class);
            userList.add(user);
        }

        return ResponseEntity.ok(userList);
    }
}
