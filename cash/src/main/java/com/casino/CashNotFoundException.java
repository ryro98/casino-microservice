package com.casino;

public class CashNotFoundException extends Exception {
    public CashNotFoundException() {
        super("Cash does not exist.");
    }
}
