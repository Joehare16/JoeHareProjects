package engine;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.List;

import database.entities.Doctor;
import database.entities.Patient;
import database.entities.Visit;

@FixMethodOrder(MethodSorters.JVM)
public class EngineTest {
    private static Engine engine;

    @BeforeClass
    public static void setUp() {
        var delEngine = new Engine();
        delEngine.emptyDatabase();

        // Initialize the engine
        // This will run migrations fresh
        engine = new Engine();
    }

    @Test
    public void testLoginWithoutUser() {
        assertFalse(engine.login("user", "pass"));
    }

    @Test
    public void testSignup() {
        assertTrue(engine.signupDoctor("admin", "pass", "Admin", "1234567890"));
    }

    @Test
    public void testLoginWithDoctor() {
        assertTrue(engine.login("admin", "pass"));
        assertNotNull(engine.state.user);
    }

    @Test
    public void testGetPersonalPatients() {
        List<Patient> personalPatients = engine.getPersonalPatients();
        assertNotNull(personalPatients);
        assertFalse(personalPatients.isEmpty());
    }

    @Test
    public void testGetAllPatients() {
        List<Patient> allPatients = engine.getPatients();
        assertNotNull(allPatients);
        assertFalse(allPatients.isEmpty());
    }

    @Test
    public void testGetDoctors() {
        List<Doctor> doctors = engine.getDoctors();
        assertNotNull(doctors);
        assertFalse(doctors.isEmpty());
    }

    @Test
    public void testGetVisits() {
        List<Visit> visits = engine.getVisits(Visit.Status.SCHEDULED);
        assertNotNull(visits);
        assertFalse(visits.isEmpty());
    }

    @Test
    public void testGetPatient() {
        Patient patient = engine.getPatient(1);
        assertNotNull(patient);
    }

    @Test
    public void testAddVisit() {
        var visit = engine.addVisit(1, Date.valueOf("2024-01-01"), "Knee");
        assertNotNull(visit);
    }

    @Test
    public void testSendMessage() {
        assertNotNull(engine.sendMessage(1, "Test message"));
    }

    @Test
    public void testGetMessages() {
        List<database.entities.Message> messages = engine.getMessages();
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testLogout() {
        assertNotNull(engine.state.user);
        assertTrue(engine.logout());
        assertNull(engine.state.user);
        assertNull(engine.state.profile);
    }

}
