package com.makesoft.project.service;

import java.util.List;
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

    public Optional<User> findById(Long Id) {
        return userRepository.findById(Id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phone_number) {
        return userRepository.findByPhoneNumber(phone_number);
    }

    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    public void deleteByPhoneNumber(String phone_number) {
        userRepository.deleteByPhoneNumber(phone_number);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

}
