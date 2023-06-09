package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public User createUser(UserCreateRequest request) throws IOException {
        User user = User.builder()
                .name(request.getName())
                .build();
        userRepository.saveAndFlush(user);

        URL url = new URL(String.format("http://localhost:8081/api/v1/cash?name=%s", user.getName()));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        int responseCode = con.getResponseCode();
        String response = con.getResponseMessage();
        return user;
    }

    public User updateUser(Integer id, UserCreateRequest request) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setName(request.getName());
        userRepository.save(user);
        return user;
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
