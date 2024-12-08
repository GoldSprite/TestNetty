package goldsprite.myUdpNetty.tools;

import java.util.HashMap;

public interface ILogLevel {
    public static final int FORCE = -2;
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int MSG = 5;

    public static HashMap<Integer, String> msgMap = new HashMap<Integer, String>() {{
        put(ILogLevel.MSG, "[ MSG ] ");
        put(ILogLevel.INFO, "[INFO ] ");
        put(ILogLevel.DEBUG, "[DEBUG] ");
        put(ILogLevel.WARNING, "[WARN ] ");
        put(ILogLevel.ERROR, "[ ERR ] ");
        put(ILogLevel.FORCE, "[FORCE] ");
    }};

    public static String getLogMsg(int logLevel) {
        if(!msgMap.containsKey(logLevel)) return "[UNKNOWN] ";
        return msgMap.get(logLevel);
    }
}
