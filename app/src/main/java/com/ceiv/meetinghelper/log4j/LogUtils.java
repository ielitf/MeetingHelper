package com.ceiv.meetinghelper.log4j;

import android.text.TextUtils;

import org.apache.log4j.Logger;

public class LogUtils {

/** log开关 */
public static final boolean SWITCH_LOG = true;
private static boolean isConfigured = false;

public static void d(String tag, String message) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.debug(message);
        //android.util.LogUtils.d(tag, message);
    }
}

public static void d(String tag, String message, Throwable exception) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.debug(message, exception);
        //android.util.LogUtils.d(tag, message);
        //exception.printStackTrace();
    }
}

public static void i(String tag, String message) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.info(message);
        //android.util.LogUtils.i(tag, message);
    }
}

public static void i(String tag, String message, Throwable exception) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.info(message, exception);
        //android.util.LogUtils.i(tag, message);
        //exception.printStackTrace();
    }
}

public static void w(String tag, String message) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.warn(message);
        //android.util.LogUtils.w(tag, message);
    }
}

public static void w(String tag, String message, Throwable exception) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.warn(message, exception);
        //android.util.LogUtils.w(tag, message);
        //exception.printStackTrace();
    }
}

public static void e(String tag, String message) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.error(message);
        //android.util.LogUtils.e(tag, message);
    }
}

public static void e(String tag, String message, Throwable exception) {
    if (SWITCH_LOG) {
        Logger LOGGER = getLogger(tag);
        LOGGER.error(message, exception);
        //android.util.LogUtils.e(tag, message);
        //exception.printStackTrace();
    }
}

private static Logger getLogger(String tag) {
    if (!isConfigured) {
        Log4jConfigure.configure();
        isConfigured = true;
    }
    Logger logger;
    if (TextUtils.isEmpty(tag)) {
        logger = Logger.getRootLogger();
    } else {
        logger = Logger.getLogger(tag);
    }
    return logger;
    }
}
