package com.casino;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("User does not exist.");
    }
}
