package Util;

public class UserSession {
    private static UserSession instance;

    private int id;
    private String username;
    private String position;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(int id, String username, String position) {
        this.id = id;
        this.username = username;
        this.position = position;
    }

    public void clearSession() {
        this.id = -1;
        this.username = null;
        this.position = null;
    }

    public int getStaffId() {
        return id;
    }
    public String getUsername() { return username; }
    public String getPosition() { return position; }
}