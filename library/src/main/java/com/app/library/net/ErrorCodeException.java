package com.app.library.net;

/**
 * Error Code Exception.
 * Created by hexiaohong on 16/9/20.
 */
public class ErrorCodeException extends Exception {

    private int code;

    public ErrorCodeException(int code) {
        super("Error code: " + code);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
