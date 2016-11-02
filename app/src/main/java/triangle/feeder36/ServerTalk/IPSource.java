package triangle.feeder36.ServerTalk;

public class IPSource {
    public static final String BASE = "http://10.9.65.223:8036/android/";

    public class Account {
        public static final String LOGIN = "student/login/";
        public static final String CHANGE_PASSWORD = "student/password_change/";
    }

    public class DataBase {
        public static final String SYNC = "student/sync/";
        public static final String FEEDBACK_RESPONSE_SUBMIT = "student/submit/";
    }

    public static String loginURL(){
        return BASE + Account.LOGIN;
    }

    public static String changePasswordURL(){return BASE + Account.CHANGE_PASSWORD;}

    public static String syncURL(){
        return BASE + DataBase.SYNC;
    }

    public static String responseSubmitURL(){return BASE + DataBase.FEEDBACK_RESPONSE_SUBMIT;}
}
