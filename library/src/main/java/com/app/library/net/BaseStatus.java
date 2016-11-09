package com.app.library.net;

/**
 * Base Status.
 */
public class BaseStatus {

    private int code;
    private String msg;
    private Throwable t;

    public BaseStatus(int code, String msg, Throwable t) {
        this.code = code;
        this.msg = msg;
        this.t = t;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.msg;
    }

    public Throwable getThrowable() {
        return this.t;
    }

    /**
     * Returns true if the code is in [200..300), which means the request was successfully received,
     * understood, and accepted.
     */
    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
}
