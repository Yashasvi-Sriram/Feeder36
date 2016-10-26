package triangle.feeder36.ServerTalk;

public class IPSource {
    public static final String BASE = "http://192.168.0.10:8036/android/student_login/";

    public class Login {
        public static final String LOGIN = "";
    }

    public static String getLoginURL(){
        return BASE + Login.LOGIN;
    }
}
