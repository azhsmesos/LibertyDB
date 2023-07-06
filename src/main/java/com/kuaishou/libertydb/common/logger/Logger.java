package com.kuaishou.libertydb.common.logger;

import static com.kuaishou.libertydb.common.verify.VerifyParam.verifyParam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class Logger<T> {

    private static LogLevel logLevel = LogLevel.INFO;
    // 默认日志文件路径为 /tmp/liberty/log/log.txt/tmp/liberty/log/log.txt
    private File logFile;
    private static String logFilePath = "/tmp/liberty/log/log.txt";

    public Logger() {
        logFile = new File(logFilePath);
        File dir = logFile.getParentFile();
        if (!dir.exists()) {
            try {
                verifyParam(dir.mkdirs(), "mkdirs fail, path: " + logFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!logFile.exists()) {
            try {
                verifyParam(logFile.createNewFile(), "createFile fail, path: " + logFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLogLevel(LogLevel level) {
        logLevel = level;
    }

    public void setLogFilePath(String path) {
        logFile = new File(path);
        logFilePath = path;
        if (!logFile.exists()) {
            try {
                verifyParam(logFile.createNewFile(), "set filePath fail, path: " + path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void debug(T message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(T message) {
        log(LogLevel.INFO, message);
    }

    public void warn(T message) {
        log(LogLevel.WARN, message);
    }

    public void error(T message) {
        log(LogLevel.ERROR, message);
    }

    private void log(LogLevel level, T message) {
        if (level.ordinal() >= logLevel.ordinal()) {
            String logMessage = "[" + level.toString() + "] " + new Date() + ": " + message + "\n";
            writeToFile(logMessage);
        }
    }

    private void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
