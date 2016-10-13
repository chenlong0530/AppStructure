package com.app.library.lg;

import android.text.format.DateFormat;
import android.util.Log;

/**
 * 日志记录
 * @version 1.1.0
 */
public class Lg {
    // private static final boolean DEBUG = true;
    private static String TAG = "lg";
    private static WriteLogger logger;
    private static boolean debug;

    enum Level {
        V,
        D,
        I,
        W,
        E,
    }

    public static final void setDebug(boolean debug) {
        Lg.debug = debug;
    }

    public static final void setLogger(WriteLogger writeLogger) {
        logger = writeLogger;
    }

    private static final void post(String msg) {
        String timestamp = DateFormat.format("yyyy-MM-dd kk-mm-ss", System.currentTimeMillis()).toString();
        post(timestamp + " " + msg, null);
    }

    private static final void post(String msg, Throwable e) {
        if (logger != null) {
            logger.post(msg, e);
        }
    }

    public static final boolean isDebug() {
        return debug || Log.isLoggable(TAG, Log.DEBUG);
    }

    public static void v(String msg) {
        doLog(Level.V, msg);
    }



    public static void d(String msg) {
        doLog(Level.D, msg);
    }

    public static void i(String msg) {
        doLog(Level.I, msg);
    }

    public static void w(Throwable e) {
        w("", e);
    }

    public static void w(String msg, Throwable e) {
        if (!isDebug() || msg == null) return;

        Log.w(TAG, msg, e);
        post(TAG + "\t" + msg, e);
    }

    public static void e(Throwable e) {
        e("", e);
    }

    public static void e(String msg, Throwable e) {
        if (!isDebug() || msg == null) return;

        Log.e(TAG, msg, e);
        post(TAG + "\t" + msg, e);
    }

    public static void print(String msg) {
        v(msg);
    }

    private static void doLog(Level level, String msg) {
        if (!isDebug() || msg == null) return;

        switch (level) {
            case V:
                Log.v(TAG, msg);
                break;
            case D:
                Log.d(TAG, msg);
                break;
            case I:
                Log.i(TAG, msg);
                break;
            case W:
                break;
            case E:
                break;
        }
        post(TAG + "\t" + msg);
    }
}
