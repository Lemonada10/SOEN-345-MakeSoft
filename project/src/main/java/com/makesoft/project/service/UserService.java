package com.makesoft.project.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.makesoft.project.model.User;
import com.makesoft.project.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(String name, String email, String phone_number, String password, String role) {
        User user = new User(name, email, phone_number, password, role);

        userRepository.save(user);
    }

    public Optional<User> findUser(Long Id) {
        return userRepository.findById(Id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phone_number) {
        return userRepository.findByPhoneNumber(phone_number);
    }

    

}
