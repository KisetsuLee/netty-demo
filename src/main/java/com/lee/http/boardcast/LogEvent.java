package com.lee.http.boardcast;

/**
 * @Author Lee
 * @Date 2020/12/28
 */
public class LogEvent {
    private long sendTime;
    private String filename;
    private String logString;
    public static byte SEPARATOR = ':';

    public LogEvent(String filename, String logString) {
        this.filename = filename;
        this.logString = logString;
        this.sendTime = System.currentTimeMillis();
    }

    public LogEvent(String filename, String logString, long timestamp) {
        this.filename = filename;
        this.logString = logString;
        this.sendTime = timestamp;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLogString() {
        return logString;
    }

    public void setLogString(String logString) {
        this.logString = logString;
    }
}
