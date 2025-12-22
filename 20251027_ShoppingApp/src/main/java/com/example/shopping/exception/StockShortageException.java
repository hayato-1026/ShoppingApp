package com.example.shopping.exception;

public class StockShortageException extends RuntimeException {
    public StockShortageException(String message) {
        super(message);
    }
}
