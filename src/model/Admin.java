package model;

import java.io.Serializable;

public class Admin extends User implements Serializable {

    public Admin() { super(); }

    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, "ADMIN", "ACTIVE");
    }
}
