package triangle.feeder36.ServerTalk;

public class IPSource {
    public static final String BASE = "http://10.9.65.223:8036/android/";

    public class Login {
        public static final String LOGIN = "student_login/";
    }

    public static String getLoginURL(){
        return BASE + Login.LOGIN;
    }
}
