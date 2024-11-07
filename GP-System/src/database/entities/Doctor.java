package database.entities;

import java.sql.ResultSet;

public class Doctor {
    public Integer did;
    private String phone_number;
    private Integer uid;

    public String name;
    public String background;

    Doctor(Integer did, String phone_number, String name, String background, Integer uid) {
        this.did = did;
        this.phone_number = phone_number;
        this.name = name;
        this.background = background;
        this.uid = uid;
    }

    public static Doctor fromResultSet(ResultSet result) {
        try {
            return new Doctor(
                    result.getInt("did"),
                    result.getString("phone_number"),
                    result.getString("name"),
                    result.getString("background"),
                    result.getInt("uid"));
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
