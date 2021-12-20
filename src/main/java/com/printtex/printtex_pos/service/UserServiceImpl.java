package com.printtex.printtex_pos.service;

import com.printtex.printtex_pos.model.User;
import com.printtex.printtex_pos.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllMainBranchUser() {
        return userRepository.findAllByIsNeedPermissionTrueAndActive(1);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findByIdAndActive(userId, 1);
    }

    @Override
    public long getUserRowNo() {
        return userRepository.count();
    }

    @Override
    public void deleteUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(value -> userRepository.delete(value));
    }

    @Override
    public List<User> getAllUserAndBranch() {
        return userRepository.findAllUserWithBranch();
    }
}
