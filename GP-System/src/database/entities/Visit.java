package database.entities;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Visit {
    public enum Status {
        SCHEDULED, CANCELLED, COMPLETED
    }

    public Integer did;
    public Integer pid;
    public Patient patient;
    public Integer visit_id;
    public Date visit_date;

    public Status status;

    public Visit(Integer visit_id, Integer pid, Integer did, Date visit_date, Status status) {
        this.visit_id = visit_id;
        this.pid = pid;
        this.did = did;
        this.visit_date = visit_date;
        this.status = status;
    }

    public static Visit fromResultSet(ResultSet result) {
        Visit visit = null;

        try {
            if (result.findColumn("visit_id") != 0) {
                visit = new Visit(result.getInt("visit_id"),
                        result.getInt("pid"),
                        result.getInt("did"),
                        result.getDate("visit_date"),
                        Status.valueOf(result.getString("status").toUpperCase()));
            }
        } catch (SQLException e) {
            System.out.println("[database] Visit: SQL Exception: " + e.getMessage());
        }

        Patient patient = null;
        try {
            if (result.findColumn("email") != 0) {
                patient = Patient.fromResultSet(result);
            }
        } catch (SQLException e) {
            System.out.println("[database] Visit: SQL Exception: " + e.getMessage());
        }

        if (visit != null && patient != null) {
            visit.patient = patient;
        }

        return visit;
    }

    @Override
    public String toString() {
        return this.patient.name + " (" + this.visit_date + ")";
    }
}
