import java.sql.*;

public class ConfirmationMessages {
    // Database connection details
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/doctor";
    static final String USERNAME = "comp5590";
    static final String PASSWORD = "a2";

    public static void main(String[] args) {
        // Confirmation messages
        String patientMessage = "Dear Patient, we're pleased to confirm your upcoming appointment on 21/06/2024 at 14:00. Please remember to bring any relevant medical records and arrive 15 minutes early to complete any necessary paperwork. If you have any questions or need to reschedule, please don't hesitate to contact us at 01200500800. We look forward to seeing you soon!";
        String doctorMessage = "Dear Doctor, your patient's appointment on 21/06/2024 at 14:00 has been confirmed. Please remember to ensure all necessary documents are ready.";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to the database!");

            // Creating prepared statements to safely insert data
            String patientQuery = "INSERT INTO patient_messages (content) VALUES (?)";
            String doctorQuery = "INSERT INTO doctor_messages (content) VALUES (?)";

            // Executing prepared statements
            try (PreparedStatement patientStatement = connection.prepareStatement(patientQuery);
                 PreparedStatement doctorStatement = connection.prepareStatement(doctorQuery)) {

                // Setting parameters for patient message
                patientStatement.setString(1, patientMessage);
                // Executing patient statement
                patientStatement.executeUpdate();

                // Setting parameters for doctor message
                doctorStatement.setString(1, doctorMessage);
                // Executing doctor statement
                doctorStatement.executeUpdate();

                System.out.println("Confirmation messages inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
}
