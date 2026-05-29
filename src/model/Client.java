package model;

import java.io.Serializable;

public class Client extends User implements Serializable {
    private String tel;
    private String address;
    private String email;
    private float totalSpent;

    public Client() { super(); }

    public Client(String username, String password, String fullName,
                  String tel, String address, String email) {
        super(username, password, fullName, "CLIENT", "ACTIVE");
        this.tel = tel;
        this.address = address;
        this.email = email;
        this.totalSpent = 0;
    }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public float getTotalSpent() { return totalSpent; }
    public void setTotalSpent(float totalSpent) { this.totalSpent = totalSpent; }
}
