import java.util.Objects;

public     class MyObject {

    String name;
    String address;

    public MyObject(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public MyObject() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyObject myObject = (MyObject) o;
        return name.equals(myObject.name) &&
                address.equals(myObject.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }
}
