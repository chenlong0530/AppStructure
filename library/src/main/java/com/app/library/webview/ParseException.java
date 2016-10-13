package com.app.library.webview;

/**
 * Thrown when parsing a URL fails.
 */
public class ParseException extends RuntimeException {
    private static final long serialVersionUID = -1463297754240794743L;
    public String response;

    ParseException(String response) {
        this.response = response;
    }
}
