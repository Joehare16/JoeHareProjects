package database.entities;

import java.sql.ResultSet;

public class Patient {
    private Integer uid;

    public String email;
    public String phone_number;
    public Integer pid;
    public String name;

    public Patient(Integer uid, String email, String phone_number, Integer pid, String name) {
        this.uid = uid;
        this.email = email;
        this.phone_number = phone_number;
        this.pid = pid;
        this.name = name;
    }

    public static Patient fromResultSet(ResultSet result) {
        try {
            return new Patient(
                    result.getInt("uid"),
                    result.getString("email"),
                    result.getString("phone_number"),
                    result.getInt("pid"),
                    result.getString("name"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
