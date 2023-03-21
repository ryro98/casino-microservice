package com.casino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getUsers() {
        if (userRepository.count() > 0) {
            List<User> users = userService.getUsers();
            return ResponseEntity.status(OK).body(users);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("Users not found");
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Integer id) throws UserNotFoundException {
        if (userRepository.existsById(id)) {
            User user = userService.getUserById(id);
            return ResponseEntity.status(OK).body(user);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("User with given in does not exist.");
        }
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(value = OK)
    public ResponseEntity<?> getUserByName(@PathVariable(name = "name") String name) throws UserNotFoundException {
        if (userRepository.existsUserByName(name)) {
            User user = userService.getUserByName(name);
            return ResponseEntity.status(OK).body(user);
        } else {
            return ResponseEntity.status(NOT_FOUND).body("User with given name does not exist.");
        }
    }

    @PostMapping
    @ResponseStatus(value = CREATED)
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest request) {
        if (request.getName().isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST).body("Name cannot be empty.");
        }
        User user = userService.createUser(request);
        return ResponseEntity.status(CREATED).body(user);
    }
}
