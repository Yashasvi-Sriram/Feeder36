package triangle.feeder36.DB.Def;

public class UserInfo {
    private String USER_NAME;
    private String PASSWORD;

    public UserInfo(String user_name,String pwd) {
        USER_NAME = user_name;
        PASSWORD = pwd;
    }

    public String getUserName() {
        return USER_NAME;
    }

    public void setUserName(String userName) {
        this.USER_NAME = userName;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
}