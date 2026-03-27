package Entity;

public abstract class Person {
    protected int id;
    protected String phone;
    protected String name;
    private String user;
    private String password;

    public Person() {}

    public Person(int id, String phone, String name, String user, String password) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.user = user;
        this.password = password;
    }

    public Person(String phone, String name, String user, String password) {
        this.phone = phone;
        this.name = name;
        this.user = user;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
