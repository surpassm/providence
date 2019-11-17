package com.ysy.oath.aspect;


import com.ysy.oath.entity.user.OperationsLog;

/**
 * @author Administrator
 */
public class LogHolder {

    private static final ThreadLocal<OperationsLog> LOG_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(OperationsLog log) {
        LOG_THREAD_LOCAL.set(log);
    }

    public static OperationsLog get() {
        return LOG_THREAD_LOCAL.get();
    }

    public static void remove() {
        LOG_THREAD_LOCAL.remove();
    }
}
