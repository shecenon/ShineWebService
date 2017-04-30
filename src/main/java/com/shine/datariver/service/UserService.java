package com.shine.datariver.service;

import com.shine.datariver.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
