package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getAllUser();

    List<User> getAllMainBranchUser();

    User getUserByEmail(String email);

    User getUserById(int userId);

    long getUserRowNo();

    void deleteUser(int userId);

    List<User> getAllUserAndBranch();

}
