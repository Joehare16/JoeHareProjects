package engine;

import database.Database;
import database.entities.User;
import database.entities.Visit;
import database.entities.Doctor;
import database.entities.Message;
import database.entities.Patient;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * The Engine class is the main class of the application. It is responsible for
 * managing the state of the application and for handling the business logic.
 */
public class Engine {
    /**
     * The State class represents the current state of the application.
     */
    public class State {
        public User user; // The currently logged-in user
        public Object profile; // The profile of the currently logged-in user (Doctor or Patient)
    }

    private Database db; // Database instance
    public State state; // Current state of the application

    /**
     * Constructor for Engine class.
     */
    public Engine() {
        db = new Database(); // Initialize database
        state = new State(); // Initialize application state
    }

    /**
     * Drops the database.
     * 
     * NOTE: This method is for testing purposes only.
     */
    public void emptyDatabase() {
        db.emptyDatabase();
    }

    /**
     * Logs in the user with the given username and password.
     * 
     * @param username The username of the user
     * @param password The password of the user
     * @return true if the login was successful, false otherwise
     */
    public boolean login(String username, String password) {
        // Get user from the database
        User user = db.getUser(username, password);

        // If the user is null, the login was unsuccessful
        if (user == null) {
            return false;
        }

        // Set the logged-in user
        this.state.user = user;

        // Determine the role of the user and set the profile accordingly
        if (user.role == User.Role.DOCTOR) {
            this.state.profile = db.getDoctor(user.uid);
        } else if (user.role == User.Role.PATIENT) {
            this.state.profile = db.getPatient(user.uid);
        }

        // Log the user's activity
        this.db.logAccess(user.uid, "login");

        return true;
    }

    /**
     * Logs out the current user.
     * 
     * @return true
     */
    public boolean logout() {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "logout");

        // This effectively logs out the user
        this.state.user = null;
        this.state.profile = null;

        return true;
    }

    /**
     * Signs up a new doctor with the given username, password, name, and phone
     * number.
     * 
     * @param username    The username of the new doctor
     * @param password    The password of the new doctor
     * @param name        The name of the new doctor
     * @param phoneNumber The phone number of the new doctor
     * @return true if the signup was successful, false otherwise
     */
    public boolean signupDoctor(String username, String password, String name, String phoneNumber) {
        // Add a new doctor user to the database
        User user = db.addDoctorUser(username, password, name, phoneNumber);

        // If the user is null, the signup was unsuccessful
        if (user == null) {
            return false;
        }

        // Set the logged-in user
        this.state.user = user;
        this.state.profile = db.getDoctor(user.uid);

        // Log the user's activity
        this.db.logAccess(state.user.uid, "signup");

        return true;
    }

    /**
     * Retrieves a list of all patients.
     * 
     * @return List of patients
     */
    public List<Patient> getPatients() {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "getPatients");

        // Retrieve the patients
        return db.getPatientList();
    }

    /**
     * Retrieves a list of all doctors.
     * 
     * @return List of doctors
     *
     */
    public List<Doctor> getDoctors() {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "getDoctors");

        // Retrieve the patients
        return db.getDoctorList();
    }

    /**
     * Retrieves a list of visits with the specified status.
     * 
     * @param status The status of the visits to retrieve
     * @return List of visits
     */
    public ArrayList<Visit> getVisits(Visit.Status status) {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "getVisits");

        // Retrieve the visits
        return db.getVisits(status);
    }

    /**
     * Retrieves the patient with the specified ID.
     * 
     * @param pid The ID of the patient to retrieve
     * @return The patient with the specified ID
     */
    public Patient getPatient(int pid) {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "getPatient");

        // Retrieve and return the patient with the specified ID from the database
        return db.getPatient(pid);
    }

    /**
     * Retrieves a list of patients assigned to the currently logged-in doctor.
     * 
     * @return List of patients
     */
    public List<Patient> getPersonalPatients() {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "getPersonalPatients");

        // Get the doctor profile from the application state
        Doctor profile = (Doctor) state.profile;

        // Retrieve from the database
        return db.getPersonalPatients(profile.did);
    }

    /**
     * Add a visit to the database.
     * 
     * @param patientId The ID of the patient
     * @param gender    The gender of the patient
     * @param date      The date of the visit
     * @param reason    The reason for the visit
     * 
     * @return The newly created visit
     */
    public Visit addVisit(int pid, Date date, String reason) {
        // Get the doctor ID from the application state
        Doctor doctor = (Doctor) state.profile;

        // Log the user's activity
        this.db.logAccess(state.user.uid, "addVisit");
        this.db.sendMessage(state.user.uid, pid, "You have a new visit scheduled for " + date.toString());

        // Add the visit to the database
        return db.addVisit(doctor.did, pid, date, reason);
    }

    /**
     * Update a visit in the database.
     * 
     * @param vid     The ID of the visit
     * @param patient The patient assigned to the visit
     * @param date    The date of the visit
     * 
     * @return true if the visit was updated successfully, false otherwise
     */
    public boolean updateVisit(Integer vid, Patient patient, Date date) {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "updateVisit");
        this.db.sendMessage(state.user.uid, patient.pid, "Your visit has been updated to " + date.toString());

        // Update the visit in the database
        return db.updateVisit(vid, patient.pid, date);
    }

    /**
     * Update the doctor assigned to a patient.
     * 
     * @param pid The ID of the patient
     * @param did The ID of the new doctor
     */
    public boolean updateDoctor(int pid, int did) {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "updateDoctor");
        this.db.sendMessage(state.user.uid, pid, "You have been assigned a new doctor");

        // Update the doctor assigned to the patient in the database
        return db.updatePatientsDoctor(pid, did);
    }

    /**
     * Send a message to a user
     * 
     * @param receiver The ID of the receiver
     * @param message  The message to send
     * 
     * @return The message that was sent
     */
    public Message sendMessage(int receiver, String message) {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "sendMessage");

        // Send the message to the receiver
        return db.sendMessage(state.user.uid, receiver, message);
    }

    /**
     * Retrieve a list of messages for the currently logged-in user.
     * 
     * @return List of messages
     */
    public List<Message> getMessages() {
        // Log the user's activity
        this.db.logAccess(state.user.uid, "getMessages");

        // Retrieve the messages for the currently logged-in user
        return db.getMessages(state.user.uid);
    }
}
