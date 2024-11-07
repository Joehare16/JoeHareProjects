package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Migrations {
    Core core;
    Database database;

    public abstract class Migration {
        public abstract boolean run(Migrations m);
    }

    public Migrations(Core core, Database database) {
        this.core = core;
        this.database = database;
    }

    public void run() {
        try {
            PreparedStatement statement = core.getPreparedStatement("SHOW TABLES LIKE 'migrations'");

            core.executePreparedStatement(statement);

            var foundMigrationTable = core.resultSet.next();
            if (!foundMigrationTable) {
                System.out.println("[database] migrations not found, initializing");
            }
        } catch (SQLException e) {
            System.err.println("[database] Get Migrations: SQL Exception: " + e.getMessage());
            System.exit(1);
        } finally {
            core.closeResources();
        }

        createMigrationsTable();
        executeMigrations();
    }

    public void createMigrationsTable() {
        core.executeGenericUpdate(
                "CREATE TABLE IF NOT EXISTS migrations ( id INT NOT NULL PRIMARY KEY, timestamp TIMESTAMP NOT NULL )");
    }

    public void executeMigrations() {
        var migrations = new ArrayList<Migration>();
        var foundAlreadyMigrated = new ArrayList<Integer>();

        migrations.add(new Migration() {
            public boolean run(Migrations m) {
                return m.initializeDatabase();
            }
        });

        migrations.add(new Migration() {
            public boolean run(Migrations m) {
                return m.addVisitTable();
            }
        });

        migrations.add(new Migration() {
            public boolean run(Migrations m) {
                return m.addTestingDoctor();
            }
        });

        migrations.add(new Migration() {
            public boolean run(Migrations m) {
                return m.addTestingPatients();
            }
        });

        migrations.add(new Migration() {
            public boolean run(Migrations m) {
                return m.addTestingVisits();
            }
        });

        try {
            PreparedStatement statement = core.getPreparedStatement("SELECT * FROM migrations");

            core.executePreparedStatement(statement);

            while (core.resultSet.next()) {
                foundAlreadyMigrated.add(core.resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception while migrating: " + e.getMessage());
            System.exit(1);
        } finally {
            core.closeResources();
        }

        for (int i = 0; i < migrations.size(); i++) {
            if (!foundAlreadyMigrated.contains(i)) {
                System.out.println("[database] running migration " + (i + 1));
                var success = migrations.get(i).run(this);
                if (success)
                    core.executeGenericUpdate("INSERT INTO migrations (id, timestamp) VALUES (" + i + ", NOW())");
            }
        }

        return;
    }

    public boolean initializeDatabase() {
        var command = new String[] {
                "mysql",
                "--protocol=tcp",
                "-u" + this.core.username,
                "-p" + this.core.password,
                "-h" + this.core.DB_URL.split("mysql://")[1].split("/")[0]
        };

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectInput(new File("database.sql"));

            Process process = processBuilder.start();

            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error running command: " + exitCode);

                var stdoutReader = new BufferedReader(new InputStreamReader(inputStream));
                var stderrReader = new BufferedReader(new InputStreamReader(errorStream));

                stderrReader.lines().forEach(System.err::println);
                stdoutReader.lines().forEach(System.out::println);

                stdoutReader.close();
                stderrReader.close();

                return false;
            }
        } catch (Exception e) {
            System.err.println("Error running command: " + e.getMessage());

            return false;
        }

        return true;
    }

    public boolean addVisitTable() {
        this.core.executeGenericUpdate(
                "CREATE TABLE IF NOT EXISTS visits ( visit_id INT AUTO_INCREMENT PRIMARY KEY, pid INT, did INT, visit_date DATETIME NOT NULL, status ENUM('scheduled', 'cancelled', 'completed') DEFAULT 'scheduled', FOREIGN KEY (pid) REFERENCES patients(pid), FOREIGN KEY (did) REFERENCES doctors(did) )");

        return true;
    }

    public boolean addTestingPatients() {
        String[] users = {
                "john,pass,01234567890,john@gmail.com",
                "jane,pass,01234567891,jane@gmail.com",
                "alice,pass,01234567892,alice@gmail.com",
                "bob,pass,01234567893,bob@gmail.com",
                "charlie,pass,01234567894,charlie@gmail.com"
        };

        var success = true;

        for (String user : users) {
            String[] userParts = user.split(",");
            var added = this.database.addPatientUser(userParts[0], userParts[1], userParts[0], userParts[2],
                    userParts[3]);

            if (added == null) {
                success = false;
                break;
            }
        }

        return success;
    }

    public boolean addTestingDoctor() {
        String[] users = {
                "admin,pass,01234567890"
        };

        var success = true;

        for (String user : users) {
            String[] userParts = user.split(",");
            var added = this.database.addDoctorUser(userParts[0], userParts[1], userParts[0], userParts[2]);

            if (added == null) {
                success = false;
                break;
            }
        }

        return success;
    }

    public boolean addTestingVisits() {

        // get a doctor
        var doctor = this.database.getDoctorList().get(0);
        var patient = this.database.getPatientList().get(0);

        var doctorId = doctor.did;
        var patientId = patient.pid;

        String[] visits = {
                "2024-01-01",
                "2024-01-02",
                "2024-01-03",
                "2024-01-04",
                "2024-01-05"
        };

        var success = true;

        for (String dateString : visits) {
            var date = Date.valueOf(dateString);
            var added = this.database.addVisit(doctorId, patientId,
                    date, "testing");

            if (added == null) {
                success = false;
                break;
            }
        }

        return success;
    }
}
