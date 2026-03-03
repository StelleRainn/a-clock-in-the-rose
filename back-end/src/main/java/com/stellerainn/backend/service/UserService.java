package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.User;
import com.stellerainn.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // In a real app, use BCrypt or similar for password hashing check
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false; // User already exists
        }
        userMapper.insert(user);
        return true;
    }
}
