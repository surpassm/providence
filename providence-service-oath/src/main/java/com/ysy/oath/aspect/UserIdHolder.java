package com.ysy.oath.aspect;



/**
 * @author Administrator
 */
public class UserIdHolder {

    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Long userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    public static Long get() {
        Long userId = USER_ID_THREAD_LOCAL.get();
        if (userId == null) {
            throw new RuntimeException();
        }
        return userId;
    }

    public static void remove() {
        USER_ID_THREAD_LOCAL.remove();
    }
}
