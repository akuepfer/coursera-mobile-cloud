package org.aku.sm.smclient.common;

/**
 * Global definitions for session handling
 */
public class Global {
    private static String jSessionId;

    public static synchronized String getJSessionId() {
        return jSessionId;
    }

    public static synchronized  void setJSessionId(String sessionId) {
        jSessionId = sessionId;
    }
}
