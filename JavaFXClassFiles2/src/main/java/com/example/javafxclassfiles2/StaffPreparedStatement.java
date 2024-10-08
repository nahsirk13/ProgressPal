/*
package com.example.javafxclassfiles2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class StaffPreparedStatement extends Application {
    private PreparedStatement preparedStatement;
    Button btInsert = new Button("Show Grade");
    private TextField tfID = new TextField();

    private TextField tfLastName = new TextField();
    private TextField tfFirstName = new TextField();
    private TextField tfMi = new TextField();
    private TextField tfAddress = new TextField();
    private TextField tfCity = new TextField();
    private TextField tfState = new TextField();
    private TextField tfTelephone = new TextField();

    private TextField tfCourseId = new TextField();
    private Label lblStatus = new Label();

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Initialize database connection and create a Statement object
        initializeDB();

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(new Label("SSN"), tfSSN,
                new Label("Course ID"), tfCourseId, (btInsert));

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(hBox, lblStatus);

        tfSSN.setPrefColumnCount(6);
        tfCourseId.setPrefColumnCount(6);
        btInsert.setOnAction(e -> insertRecord());

        // Create a scene and place it in the stage
        Scene scene = new Scene(vBox, 420, 80);
        primaryStage.setTitle("Staff Entry"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }
    private void initializeDB() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");

            // Establish a connection
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost/javabook", "krishan2", "tiger");

            System.out.println("Database connected");

            String queryString = "select firstName, mi, " +
                    "lastName, title, grade from Student, Enrollment, Course "  +
                    "where Student.ssn = ? and Enrollment.courseId = ? " +
                    "and Enrollment.courseId = Course.courseId"; //complete
            // Create a statement
            preparedStatement = connection.prepareStatement(queryString);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertRecord() {
        String statement = "INSERT INTO Staff(ID, LastName, FirstName, mi, Address, " +
                "City, State, Telephone) VALUES ('" +
                tfID.getText().trim() + "', '" +
                tfLastName.getText().trim() + "', '" +
                tfFirstName.getText().trim() + "', '" +
                tfMi.getText().trim() + "', '" +
                tfAddress.getText().trim() + "', '" +
                tfCity.getText().trim() + "', '" +
                tfState.getText().trim() + "', '" +
                tfTelephone.getText().trim() + "', ');"
                ;
        String courseId = tfCourseId.getText();
        try {

            preparedStatement.executeUpdate(statement);
        }
        catch (SQLException ex) {
            lblStatus.setText("Insertion failed: " + ex);
        }
    }


     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.

    public static void main(String[] args) {
        //launch(args);
    }
}


*/
