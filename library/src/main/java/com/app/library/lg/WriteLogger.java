package com.app.library.lg;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志保存文件
 * 
 * @version 1.1.0
 */
public class WriteLogger {

    private static final String TAG = "Structure";

    private static String MYLOG_PATH_SDCARD_DIR = "/StructureLog/";// 日志文件在sdcard中的路径
    private static String LOG_NAME = "StructureLog.txt";// 本类输出的日志文件名称

    private static WriteLogger instance;
    private HandlerThread looperThread;
    private Handler handler;
    private File file;
    private BufferedWriter out;

    public static final WriteLogger getInstance(Context context) {
        if (instance == null) {
            instance = new WriteLogger(context);
        }
        return instance;
    }

    private WriteLogger(Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-");// 日志文件格式
        Date now = new Date();
        String needWriteFile = dateFormat.format(now);
        looperThread = new HandlerThread("BackgroundHandler", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        looperThread.start();
        handler = new Handler(looperThread.getLooper());
        File file1 = Environment.getExternalStorageDirectory();
        File path = new File(file1 + MYLOG_PATH_SDCARD_DIR);
        try {
            if (!path.exists()) {
                path.mkdir();
            }
            File filetxt = new File(path, needWriteFile + LOG_NAME);
            if (!filetxt.exists()) {
                filetxt.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(filetxt, true));
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private class WriteLoggerRunnable implements Runnable {

        private String msg;
        private Throwable throwable;

        public WriteLoggerRunnable(String msg, Throwable throwable) {

            this.msg = msg;
            this.throwable = throwable;
        }

        @Override
        public void run() {
            writeLog(msg, throwable);
        }

        private void writeLog(String log, Throwable throwable) {
            try {
                out.write(log);
                out.newLine();
                if (throwable != null) {
                    Writer result = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(result);
                    throwable.printStackTrace(printWriter);
                    String stacktrace = result.toString();
                    printWriter.close();
                    out.write(stacktrace);
                    out.newLine();
                }
                out.flush();
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            } finally {
            }
        }
    }

    public void post(String msg, Throwable e) {
        handler.post(new WriteLoggerRunnable(msg, e));
    }
}
