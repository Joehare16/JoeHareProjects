package database.entities;

import java.sql.ResultSet;

public class User {
    public enum Role {
        ADMIN, DOCTOR, PATIENT
    }

    public Integer uid;
    public String username;
    private String password;
    public Role role;

    public User(Integer uid, String username, String password, Role role) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static User fromResultSet(ResultSet result) {
        try {
            return new User(
                    result.getInt("uid"),
                    result.getString("username"),
                    result.getString("password"),
                    Role.valueOf(result.getString("role").toUpperCase()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
