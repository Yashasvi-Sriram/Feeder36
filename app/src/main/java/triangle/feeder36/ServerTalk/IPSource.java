package triangle.feeder36.ServerTalk;

public class IPSource {
    public static final String BASE = "http://10.9.65.223:8036/android/";

    public class Account {
        public static final String LOGIN = "student/login/";
        public static final String CHANGE_PASSWORD = "student/password_change/";
    }

    public static String loginURL(){
        return BASE + Account.LOGIN;
    }

    public static String changePasswordURL(){return BASE + Account.CHANGE_PASSWORD;}
}
