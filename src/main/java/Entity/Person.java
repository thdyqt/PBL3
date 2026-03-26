package Entity;

public abstract class Person {
    protected int id;
    protected String phone;
    protected String name;

    public Person() {}

    public Person(int id, String phone, String name) {
        this.id = id;
        this.phone = phone;
        this.name = name;
    }

    public Person(String phone, String name) {
        this.phone = phone;
        this.name = name;
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
}
