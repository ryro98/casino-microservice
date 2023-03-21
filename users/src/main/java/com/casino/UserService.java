package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }


    public User getUserById(Integer id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByName(String name) throws UserNotFoundException {
        return userRepository.findUserByName(name).orElseThrow(UserNotFoundException::new);
    }

    public User createUser(UserCreateRequest request) {
        User user = User.builder()
                .name(request.getName())
                .build();
        userRepository.saveAndFlush(user);

        return user;
    }
}
