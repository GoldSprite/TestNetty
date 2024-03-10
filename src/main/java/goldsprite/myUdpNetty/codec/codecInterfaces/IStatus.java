package goldsprite.myUdpNetty.codec.codecInterfaces;

import java.util.HashMap;

/**
 * 四种收发状态分别以1234万开头
 * 每100作为不同指令的信息码id
 */
public interface IStatus {
    int SEND_SUCCESS = 10001;
    int SEND_DEFEAT = 20001;
    int RETURN_SUCCESS = 30001;
    int RETURN_DEFEAT = 40001;

    int RETURN_SUCCESS_LOGIN = 30100;
    String MSG_RETURN_SUCCESS_LOGIN = "登录成功";
    int RETURN_DEFEAT_LOGIN_REPEAT = 40100;
    String MSG_RETURN_DEFEAT_LOGIN_REPEAT = "重复登陆";

    public static HashMap<Integer, String> msgMap = new HashMap<Integer, String>() {{
        put(RETURN_SUCCESS_LOGIN, MSG_RETURN_SUCCESS_LOGIN);
        put(RETURN_DEFEAT_LOGIN_REPEAT, MSG_RETURN_DEFEAT_LOGIN_REPEAT);
    }};

    public static String getStatusMsg(Packet pk) {
        var status = pk.getCode();
        var msg = pk.getClass().getSimpleName()+(isReturnStatus(status)?"响应":"发送")+(isSuccessStatus(status)?"成功.":"失败, reason: "+msgMap.get(status));
        return msg;
    }

    public static boolean isReturnStatus(int status) {
        return status /10000 == 3  || status/10000 == 4;
    }

    public static boolean isSuccessStatus(int status) {
        return status /10000 == 1  || status/10000 == 3;
    }
}
