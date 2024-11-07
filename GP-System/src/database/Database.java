package database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import database.entities.User;
import database.entities.Visit;
import database.entities.Visit.Status;
import database.entities.Doctor;
import database.entities.Message;
import database.entities.Patient;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * To-do:
 * 
 * Better layout for running an update/query
 * Validation
 */
public class Database {
	private Core core = new Core();
	private Migrations migrations = new Migrations(core, this);
	private Argon2 argon2 = Argon2Factory.create();

	/**
	 * Initializes the Database by running migrations.
	 */
	public Database() {
		System.out.println("[database] initialized");

		migrations.run();
	}

	/**
	 * Empties the database by dropping all tables and constraints.
	 */
	public void emptyDatabase() {
		core.executeGenericUpdate("EXEC sp_msforeachtable \"ALTER TABLE ? NOCHECK CONSTRAINT all\"");
		core.executeGenericUpdate("EXEC sp_MSforeachtable @command1 = \"DROP TABLE ?\"");
	}

	/**
	 * Retrieves a User from the database based on username and password.
	 * 
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return The User object if found, otherwise null.
	 */
	public User getUser(String username, String password) {
		try {
			PreparedStatement statement = this.core.getPreparedStatement("SELECT * FROM users WHERE username = ?");

			statement.setString(1, username);

			this.core.executePreparedStatement(statement);

			if (this.core.resultSet.next()) {
				if (argon2.verify(this.core.resultSet.getString("password"), password.getBytes())) {
					return User.fromResultSet(this.core.resultSet);
				}
			}
		} catch (SQLException e) {
			System.err.println("[database] Get User: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return null;
	}

	/**
	 * Retrieves a Doctor from the database based on uid.
	 * 
	 * @param uid The unique identifier of the doctor.
	 * @return The Doctor object if found, otherwise null.
	 */
	public Doctor getDoctor(int uid) {
		try {
			PreparedStatement statement = this.core.getPreparedStatement("SELECT * FROM doctors WHERE uid = ?");

			statement.setInt(1, uid);

			this.core.executePreparedStatement(statement);

			if (this.core.resultSet.next()) {
				return Doctor.fromResultSet(this.core.resultSet);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Doctor: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return null;
	}

	/**
	 * Retrieves a list of all doctors from the database.
	 * 
	 * @return An ArrayList of Doctor objects.
	 */
	public ArrayList<Doctor> getDoctorList() {
		ArrayList<Doctor> doctors = new ArrayList<>();

		try {
			PreparedStatement statement = this.core.getPreparedStatement("SELECT * FROM doctors");

			this.core.executePreparedStatement(statement);

			while (this.core.resultSet.next()) {
				Doctor doctor = Doctor.fromResultSet(this.core.resultSet);
				doctors.add(doctor);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Doctor List: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return doctors;
	}

	/**
	 * Retrieves a list of all patients from the database.
	 * 
	 * @return An ArrayList of Patient objects.
	 */
	public ArrayList<Patient> getPatientList() {
		ArrayList<Patient> patients = new ArrayList<>();

		try {
			PreparedStatement statement = this.core.getPreparedStatement("SELECT * FROM patients");

			this.core.executePreparedStatement(statement);

			while (this.core.resultSet.next()) {
				Patient patient = Patient.fromResultSet(this.core.resultSet);
				patients.add(patient);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Patient List: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return patients;
	}

	/**
	 * Adds a new user with the role of doctor to the database.
	 * 
	 * @param username     The username of the new doctor user.
	 * @param password     The password of the new doctor user.
	 * @param name         The name of the new doctor.
	 * @param phone_number The phone number of the new doctor.
	 * @return The User object representing the newly added doctor user if
	 *         successful, otherwise null.
	 */
	public User addDoctorUser(String username, String password, String name, String phone_number) {
		String hashedPassword = argon2.hash(10, 65536, 1, password.getBytes());

		try {
			PreparedStatement statement = this.core
					.getPreparedStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");

			statement.setString(1, username);
			statement.setString(2, hashedPassword);
			statement.setString(3, "doctor");

			this.core.executePreparedStatement(statement);
		} catch (SQLException e) {
			System.err.println("[database] Add Doctor User: SQL Exception: " + e.getMessage());
		}

		User user = getUser(username, password);

		if (user == null) {
			return null;
		}

		try {
			PreparedStatement statement = this.core
					.getPreparedStatement("INSERT INTO doctors (uid, name, phone_number) VALUES (?, ?, ?)");

			statement.setInt(1, user.uid);
			statement.setString(2, name);
			statement.setString(3, phone_number);

			this.core.executePreparedStatement(statement);
		} catch (SQLException e) {
			System.err.println("[database] Add Doctor: SQL Exception: " + e.getMessage());
		}

		return user;
	}

	/**
	 * Adds a new user with the role of patient to the database.
	 * 
	 * @param username     The username of the new patient user.
	 * @param password     The password of the new patient user.
	 * @param name         The name of the new patient.
	 * @param phone_number The phone number of the new patient.
	 * @param email        The email of the new patient.
	 * 
	 * @return The User object representing the newly added patient user if
	 *         successful, otherwise null.
	 */
	public User addPatientUser(String username, String password, String name, String phone_number, String email) {
		String hashedPassword = argon2.hash(10, 65536, 1, password.getBytes());

		try {
			PreparedStatement statement = this.core
					.getPreparedStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");

			statement.setString(1, username);
			statement.setString(2, hashedPassword);
			statement.setString(3, "patient");

			this.core.executePreparedStatement(statement);
		} catch (SQLException e) {
			System.err.println("[database] Add Patient User: SQL Exception: " + e.getMessage());
		}

		User user = getUser(username, password);

		if (user == null) {
			return null;
		}

		try {
			PreparedStatement statement = this.core
					.getPreparedStatement(
							"INSERT INTO patients (uid, name, phone_number, email) VALUES (?, ?, ?, ?)");

			statement.setInt(1, user.uid);
			statement.setString(2, name);
			statement.setString(3, phone_number);
			statement.setString(4, email);

			this.core.executePreparedStatement(statement);
		} catch (SQLException e) {
			System.err.println("[database] Add Patient: SQL Exception: " + e.getMessage());
		}

		return user;
	}

	/**
	 * Adds a new visit to the database.
	 * 
	 * @param did    The unique identifier of the doctor for the visit.
	 * @param pid    The unique identifier of the patient for the visit.
	 * @param date   The date of the visit.
	 * @param reason The reason for the visit.
	 * 
	 * @return The Visit object representing the newly added visit.
	 */
	public Visit addVisit(int did, int pid, Date date, String reason) {
		var visit_id = 0;

		try {
			PreparedStatement statement = this.core.getPreparedStatement(
					"INSERT INTO visits (did, pid, visit_date, status) VALUES (?, ?, ?, ?)");

			statement.setInt(1, did);
			statement.setInt(2, pid);
			statement.setDate(3, date);
			statement.setString(4, Status.SCHEDULED.toString().toLowerCase());

			this.core.executePreparedStatement(statement);

			var keys = statement.getGeneratedKeys();
			if (keys.next()) {
				visit_id = keys.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("[database] Add Visit: SQL Exception: " + e.getMessage());
		}

		return new Visit(visit_id, pid, did, date, Status.SCHEDULED);
	}

	/**
	 * Retrieves a list of visits from the database.
	 * 
	 * @param status The status of the visits to retrieve. If null, retrieves all
	 *               visits.
	 * 
	 * @return An ArrayList of Visit objects representing the visits.
	 */
	public ArrayList<Visit> getVisits(Status status) {
		ArrayList<Visit> visits = new ArrayList<>();

		try {
			PreparedStatement statement = this.core.getPreparedStatement(
					"SELECT * FROM visits JOIN patients ON visits.pid = patients.pid WHERE status = ?");

			if (status == null) {
				statement = this.core
						.getPreparedStatement("SELECT * FROM visits JOIN patients ON visits.pid = patients.pid");
			} else {
				statement.setString(1, status.toString().toLowerCase());
			}

			this.core.executePreparedStatement(statement);

			while (this.core.resultSet.next()) {
				Visit visit = Visit.fromResultSet(this.core.resultSet);
				visits.add(visit);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Visits: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return visits;
	}

	/**
	 * Retrieves a patient from the database based on their unique identifier.
	 * 
	 * @param pid The unique identifier of the patient.
	 * 
	 * @return The Patient object representing the patient if found, otherwise null.
	 */
	public Patient getPatient(int pid) {
		try {
			PreparedStatement statement = this.core.getPreparedStatement("SELECT * FROM patients WHERE pid = ?");

			statement.setInt(1, pid);

			this.core.executePreparedStatement(statement);

			if (this.core.resultSet.next()) {
				return Patient.fromResultSet(this.core.resultSet);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Patient: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return null;
	}

	/**
	 * Retrieves a list of patients associated with a specific doctor from the
	 * database.
	 * 
	 * @param did The unique identifier of the doctor.
	 * 
	 * @return An ArrayList of Patient objects representing the patients associated
	 *         with the doctor.
	 */
	public ArrayList<Patient> getPersonalPatients(int did) {
		ArrayList<Patient> patients = new ArrayList<>();

		try {
			PreparedStatement statement = this.core
					.getPreparedStatement("SELECT * FROM patients WHERE pid IN (SELECT pid FROM visits WHERE did = ?)");

			statement.setInt(1, did);

			this.core.executePreparedStatement(statement);

			while (this.core.resultSet.next()) {
				Patient patient = Patient.fromResultSet(this.core.resultSet);
				patients.add(patient);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Personal Patients: SQL Exception: " + e.getMessage());
		} finally {
			this.core.closeResources();
		}

		return patients;
	}

	/**
	 * Logs an access action in the database.
	 * 
	 * @param uid    The unique identifier of the user performing the action.
	 * @param action The action being performed.
	 */
	public void logAccess(int uid, String action) {
		try {
			PreparedStatement statement = this.core
					.getPreparedStatement("INSERT INTO access_logs (uid, action) VALUES (?, ?)");

			statement.setInt(1, uid);
			statement.setString(2, action);

			this.core.executePreparedStatement(statement);
		} catch (SQLException e) {
			System.err.println("[database] Log Access: SQL Exception: " + e.getMessage());
		}
	}

	/**
	 * Updates a visit in the database.
	 * 
	 * @param visit_id The unique identifier of the visit to update.
	 * @param pid      The unique identifier of the patient for the visit.
	 * @param date     The updated date of the visit.
	 * @return True if the visit was successfully updated, otherwise false.
	 */
	public boolean updateVisit(int visit_id, int pid, Date date) {
		try {
			PreparedStatement statement = this.core.getPreparedStatement(
					"UPDATE visits SET pid = ?, visit_date = ? WHERE visit_id = ?");

			statement.setInt(1, pid);
			statement.setDate(2, date);
			statement.setInt(3, visit_id);

			this.core.executePreparedStatement(statement);

			return true;
		} catch (SQLException e) {
			System.err.println("[database] Update Visit: SQL Exception: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the doctor associated with a patient in the database.
	 * 
	 * @param pid The unique identifier of the patient.
	 * @param did The unique identifier of the new doctor for the patient.
	 * @return True if the update was successful, otherwise false.
	 */
	public boolean updatePatientsDoctor(int pid, int did) {
		try {
			System.out.println("siu");
			PreparedStatement statement = this.core
					.getPreparedStatement("UPDATE visits SET did = ? WHERE pid = ?");

			statement.setInt(1, did);
			statement.setInt(2, pid);

			this.core.executePreparedStatement(statement);

			return true;
		} catch (SQLException e) {
			System.err.println("[database] Update Patients Doctor: SQL Exception: " + e.getMessage());
		}

		return false;
	}

	/**
	 * Sends a message from one user to another in the database.
	 * 
	 * @param sender   The unique identifier of the sender.
	 * @param receiver The unique identifier of the receiver.
	 * @param message  The message content.
	 * @return The Message object representing the newly sent message.
	 */
	public Message sendMessage(int sender, int receiver, String message) {
		var message_id = 0;

		try {
			PreparedStatement statement = this.core
					.getPreparedStatement("INSERT INTO messages (sender_id, receiver_id, message) VALUES (?, ?, ?)");

			statement.setInt(1, sender);
			statement.setInt(2, receiver);
			statement.setString(3, message);

			this.core.executePreparedStatement(statement);

			var keys = statement.getGeneratedKeys();

			if (keys.next()) {
				message_id = keys.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("[database] Send Message: SQL Exception: " + e.getMessage());
		}

		return new Message(message_id, sender, receiver, message, new Date(System.currentTimeMillis()));
	}

	/**
	 * Retrieves a list of messages for a user from the database.
	 * 
	 * @param uid The unique identifier of the user.
	 * @return An ArrayList of Message objects representing the messages for the
	 *         user.
	 */
	public ArrayList<Message> getMessages(int uid) {
		var messages = new ArrayList<Message>();

		try {
			PreparedStatement statement = this.core.getPreparedStatement("SELECT * FROM messages WHERE receiver = ?");

			statement.setInt(1, uid);

			this.core.executePreparedStatement(statement);

			if (this.core.resultSet == null) {
				return messages;
			}

			while (this.core.resultSet.next()) {
				Message message = Message.fromResultSet(this.core.resultSet);
				messages.add(message);
			}
		} catch (SQLException e) {
			System.err.println("[database] Get Messages: SQL Exception: " + e.getMessage());
		}

		return messages;
	}
}
