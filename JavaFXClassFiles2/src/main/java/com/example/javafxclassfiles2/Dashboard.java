//  Dashboard.java
//
// Date: May 2023
// Author: Krishan Sapkota
//
// Description: This is a planner application that let your register, login, add and remove
// tasks. Tasks have due dates and when they pass they are marked as overdue.




package com.example.javafxclassfiles2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class Dashboard extends Application {
    // Statement for executing queries
    private Statement statement;

    //Initialize nodes (labels, buttons, text-fields, etc.)
    private TextField tfCourseId = new TextField();
    private Label lblStatus = new Label();

    private Label todayDate = new Label();

    private Label userInfo = new Label();
    private int currentUserID;
    private int countOpenTasks = 0;

    Button btShowGrade = new Button("Show Name");



    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) throws SQLException, ClassNotFoundException, FileNotFoundException {
        // Initialize database connection and create a Statement object
        initializeDB();


        //Login Page
        VBox loginCenterBox = new VBox(20); //center top
        VBox loginTopBox = new VBox(20);
                  //logo
        Image logo = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/progressPal logo.png");
        ImageView  logoView=new ImageView();

        logoView.setImage(logo);
        logoView.setFitHeight(300);
        logoView.setFitWidth(260);
        Label userNotFound = new Label();
               //Login Button
        Button btLogIn= new Button("Log In");
        Button btRegister = new Button("Register");
        loginTopBox.getChildren().addAll(logoView,userNotFound);
               // Registration fields (hidden by default)
        TextField tfFirstName = new TextField();
        TextField tfLastName = new TextField();
        Button btSubmitRegistration = new Button("Submit Registration");
        btSubmitRegistration.setVisible(false);
        Button btRegistrationGoBack  = new Button("Back to Login");
        btRegistrationGoBack.setVisible(false);


        // Login text forms
        HBox logInHBox = new HBox();
        TextField userNameTf = new TextField();
        PasswordField passwordTf = new PasswordField();
        HBox usernameHBox = new HBox(new Label("Username:  "), userNameTf );
        HBox passwordHBox = new HBox(new Label("Password:  "), passwordTf);
        HBox registrationFieldsVBox = new HBox(new Label("Forename:  "), tfFirstName,
                new Label("  "),new Label("Surname:  "), tfLastName);
        registrationFieldsVBox.setVisible(false);
        HBox loginButtonsHbox = new HBox(btRegistrationGoBack,new Label("       "),btLogIn, new Label("     "), btRegister,
                btSubmitRegistration);
        loginCenterBox.getChildren().addAll(usernameHBox, passwordHBox, registrationFieldsVBox,
                loginButtonsHbox);
        usernameHBox.setAlignment(Pos.CENTER);
        passwordHBox.setAlignment(Pos.CENTER);
        registrationFieldsVBox.setAlignment(Pos.CENTER);
        loginButtonsHbox.setAlignment(Pos.CENTER);




        //Initialize the sections of dashboard with Hbox and Vbox
        HBox topInfo = new HBox(50); //top
        VBox taskInfo = new VBox(20); //center top
        HBox HBoxEditTask = new HBox(20); //attach to bottom of task info
        VBox goalInfo = new VBox(20); //center bottom
        VBox performanceInfo = new VBox(30); //right
        HBox bottomButtons = new HBox(10); //bottom


        //DashBoard Top Info nodes
        DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
        Pane popupCalender = (Pane) datePickerSkin.getPopupContent();

        //Dashboard Task Info Nodes
        ScrollPane tasksScrollPane = new ScrollPane();
        Button btEditTask = new Button("Go");
        //Provide combo boxes with list of all short and long term goals for goal nesting
        ObservableList<String> allTasksList =  FXCollections.observableArrayList();
        ComboBox comboBoxTasksEdit = new ComboBox(allTasksList);

        HBoxEditTask.getChildren().addAll(new Label("Edit Task: "),comboBoxTasksEdit, btEditTask);



        //Add Date to Top of Dashboard
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEE, MMMM dd");
        String dateFormatted = dateFormat1.format(cal.getTime());
        todayDate.setText(dateFormatted);
        todayDate.setStyle("-fx-font: 35 Consolas; -fx-font-weight: bold;");


        //Bottom Button HBox
        Image imageAddTask = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/add icon.png");
        ImageView  imageviewAddTask =new ImageView();
        Image imageSearch = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/search icon.png");
        ImageView  imageviewSearch =new ImageView();
        Image imageLogOut = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/log out icon.png");
        ImageView  imageviewLogOut =new ImageView();
        imageviewLogOut.setImage(imageLogOut);

        imageviewAddTask.setImage(imageAddTask);
        imageviewAddTask.setFitHeight(70);
        imageviewAddTask.setFitWidth(70);
        imageviewSearch.setImage(imageSearch);
        imageviewSearch.setFitHeight(70);
        imageviewSearch.setFitWidth(70);
        imageviewLogOut.setFitHeight(70);
        imageviewLogOut.setFitWidth(70);
        bottomButtons.getChildren().addAll(imageviewLogOut,new Label("         "), imageviewAddTask, new Label("         "), imageviewSearch);
        imageviewSearch.setVisible(false);
        //Attach nodes to dashboard sections
        topInfo.getChildren().addAll(todayDate);
        performanceInfo.getChildren().addAll(new Label("                                           "));


        //Goal Info on dashboard section
        ScrollPane goalsScrollPane = new ScrollPane();



        //Task/Goal Page Nodes
        VBox VBoxAddTaskGoal = new VBox();
        ObservableList<String> itemTypesList =  FXCollections.observableArrayList("Task", "Short Term Goal",
                "Long Term Goal");
        ComboBox comboBoxItemType = new ComboBox(itemTypesList);
        //Provide combo boxes with list of all short and long term goals for goal nesting
        ObservableList<String> allShortGoalsList =  FXCollections.observableArrayList();
        ObservableList<String> allLongGoalsList =  FXCollections.observableArrayList();

        ComboBox comboAllShortGoals = new ComboBox(allShortGoalsList);
        ComboBox comboAllLongGoals = new ComboBox(allLongGoalsList);

        TextField tfAddName = new TextField();
        TextField tfAddDescription = new TextField();
        DatePicker datePickerAddDueDate = new DatePicker();
        Label labelAddItemType, labelAddItemName, labelAddItemDesc, labelAddItemDueDate, labelAddItemShortGoal,
                    labelAddItemLongGoal, labelAddErrorMessage;

        VBoxAddTaskGoal.getChildren().addAll(
                labelAddItemType = new Label("\n\n\n\nTask, Short Term Goal, or Long Term Goal: "),
                comboBoxItemType,
                labelAddItemName = new Label("\nTask/Goal Name:"),
                tfAddName,
                labelAddItemDesc=new Label("\nTask/Goal Description:"),
                tfAddDescription,
                labelAddItemDueDate = new Label("\nDue Date:"),
                datePickerAddDueDate,
                labelAddItemShortGoal= new Label("\nIf task, select short term goal it is under:"),
                comboAllShortGoals,
                labelAddItemLongGoal=new Label("\nIf short term goal, select long term goal it is under:"),
                comboAllLongGoals
                );
        labelAddItemType.setStyle("-fx-font: 13 Consolas; ");
        tfAddName.setStyle("-fx-font: 13 Consolas; ");
        labelAddItemDesc.setStyle("-fx-font: 13 Consolas; ");
        labelAddItemDueDate.setStyle("-fx-font: 13 Consolas; ");
        labelAddItemShortGoal.setStyle("-fx-font: 13 Consolas; ");
        labelAddItemLongGoal.setStyle("-fx-font: 13 Consolas; ");

                     //images buttons on task/goal page
        Image imageConfirmAddTask = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/confirm add icon.png");
        ImageView  imageviewConfirmAddTask =new ImageView();
        Image imageBackToDash = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/back to dashboard icon.png");
        ImageView  imageviewBackToDash =new ImageView();
        imageviewConfirmAddTask.setImage(imageAddTask);
        imageviewBackToDash.setImage(imageBackToDash);
        imageviewConfirmAddTask.setFitHeight(70);
        imageviewConfirmAddTask.setFitWidth(70);
        imageviewBackToDash.setFitHeight(50);
        imageviewBackToDash.setFitWidth(50);
        labelAddErrorMessage = new Label("");
        VBoxAddTaskGoal.getChildren().addAll(labelAddErrorMessage, imageviewConfirmAddTask, imageviewBackToDash);

        //Task/Goal Info Page
        Label labelTaskGoalNameInfo = new Label("");
        Label labelTaskGoalDescInfo = new Label("");
        Label labelTaskGoalDueDateInfo = new Label("");
        Label labelTaskGoalCreateDateInfo = new Label("");
        Label labelTaskNestedInfo = new Label("");
        Label labelTaskNested2Info = new Label("");
        Button btTaskGoalComplete = new Button("Complete");
        Button btTaskGoalDismiss = new Button("Dismiss");
        Button btTaskGoalCancel = new Button("Cancel");
        HBox HBoxTaskGoalInfoButtons = new HBox(btTaskGoalCancel, new Label("             "),
                btTaskGoalDismiss, new Label("          "), btTaskGoalComplete);
        VBox VBoxTaskGoalInfo= new VBox(labelTaskGoalNameInfo, labelTaskGoalDescInfo, labelTaskGoalDueDateInfo,
                labelTaskGoalCreateDateInfo, labelTaskNestedInfo, labelTaskNested2Info);
        VBox VBoxTaskGoalInfoPage = new VBox(VBoxTaskGoalInfo, HBoxTaskGoalInfoButtons);
        VBoxTaskGoalInfo.setAlignment(Pos.CENTER);
        HBoxTaskGoalInfoButtons.setAlignment(Pos.CENTER);

        labelTaskGoalNameInfo.setStyle("-fx-font: 15 Consolas; \n");
        labelTaskGoalDescInfo.setStyle("-fx-font: 12 Consolas; \n");
        labelTaskGoalDueDateInfo.setStyle("-fx-font: 12 Consolas;\n");
        labelTaskGoalCreateDateInfo.setStyle("-fx-font: 12 Consolas;\n");
        labelTaskNestedInfo.setStyle("-fx-font: 12 Consolas; \n");
        labelTaskNested2Info.setStyle("-fx-font: 12 Consolas;\n");








        // create a background image

        Image gridBG = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/grid  background.png");
        Image folderBG = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/folder bg.png");
        Image performanceBG = new Image("C:/Users/krish/IdeaProjects/JavaFXClassFiles2/src/main/java/com/example/javafxclassfiles2/performance bg.png");
        BackgroundImage gridBi = new BackgroundImage(gridBG,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        BackgroundImage folderBi = new BackgroundImage(folderBG,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        BackgroundImage performanceBi = new BackgroundImage(performanceBG,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);


    // Background creation
        Background gridBackground = new Background(gridBi);
        Background folderBackground = new Background(gridBi);
        Background performanceBackground = new Background(performanceBi);


        //Initialize borderpanes for each scene  with sections and background attached
        BorderPane dashboardBorderPane = new BorderPane(taskInfo, topInfo,performanceInfo, bottomButtons, goalInfo);
        BorderPane loginBorderPane = new BorderPane(loginCenterBox);
        BorderPane analysisBorderPane = new BorderPane();
        BorderPane taskGoalInfoBorderPane = new BorderPane(VBoxTaskGoalInfoPage);
        BorderPane addTaskGoalPane = new BorderPane(VBoxAddTaskGoal);

        //set background image
        topInfo.setBackground(folderBackground);
        performanceInfo.setBackground(performanceBackground);
        dashboardBorderPane.setBackground(gridBackground);
        loginBorderPane.setBackground(gridBackground);
        analysisBorderPane.setBackground(gridBackground);
        taskGoalInfoBorderPane.setBackground(gridBackground);
        addTaskGoalPane.setBackground(gridBackground);



        //adjust page alignments
        loginBorderPane.setTop(loginTopBox);
        loginTopBox.setAlignment(Pos.CENTER);
        loginCenterBox.setAlignment(Pos.CENTER);
        bottomButtons.setAlignment(Pos.CENTER);
        VBoxAddTaskGoal.setAlignment(Pos.CENTER);
        VBoxTaskGoalInfoPage.setAlignment(Pos.CENTER);

        // Create a scene for each page
        Scene login= new Scene(loginBorderPane, 600, 600);
        Scene dashboard = new Scene(dashboardBorderPane, 600, 600);
        Scene analysisScene = new Scene(analysisBorderPane, 600, 600);
        Scene taskGoalInfo = new Scene(taskGoalInfoBorderPane, 600, 600);
        Scene addTaskGoal = new Scene(addTaskGoalPane, 600, 600);



        //login through enter key
        passwordTf.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!userNameTf.getText().isEmpty() && !passwordTf.getText().isEmpty()) {
                    try {
                        currentUserID = userLogin(userNameTf.getText(), passwordTf.getText());
                        userNotFound.setText("");
                        if (currentUserID!=0) {
                            userNotFound.setText("");

                            //SUCCESSFUL USER LOGIN: rerun all dashboard info:
                            String [] allTaskNames = getUserItem("Task", "TaskName");
                            String [] allTaskDesc = getUserItem("Task", "TaskDescription");
                            String [] allTaskDueDate = getUserItem("Task", "DueDate");
                            String [] allTaskOverdue = getUserItem("Task", "Overdue");
                            String [] allTaskCompletedStatus = getUserItem("Task", "Completed");



                            //call function that returns how many open tasks exist for current user
                            for (int i = 0; i < getUserItemsAmount("Task");i++) {
                                if (allTaskCompletedStatus[i].equals("0")) {
                                    String overdueString = "";
                                    if (allTaskOverdue[i].equals("1")) {overdueString = "Overdue!";}
                                    Label labelTask = new Label("•  " + allTaskNames[i] + "\n" + allTaskDesc[i] +
                                            "\nDue: " + allTaskDueDate[i] + "        " + overdueString);
                                    if (allTaskOverdue[i].equals("1")) {
                                        labelTask.setTextFill(Color.RED);
                                    }

                                    labelTask.setStyle("-fx-font: 12 Consolas;-fx-border-color:green;\n ");
                                    taskInfo.getChildren().add(labelTask);
                                    tasksScrollPane.setContent(taskInfo);
                                }
                            }
                            //Fill combo box with tasks
                            String [] allTaskNamesCombo = new String[0];
                            try {
                                allTaskNamesCombo = getTaskItemCombo("Task", "TaskName");
                            } catch (SQLException ex2) {
                                System.out.println("getUserItem method sql exception for short goal names");
                            }
                            String [] allLongGoalNames = new String[0];
                            allTasksList.addAll(allTaskNamesCombo);

                            taskInfo.getChildren().addAll(HBoxEditTask);
                            primaryStage.setScene(dashboard);
                        }
                        else {
                            userNotFound.setText("User or Password not found. Please try again.");
                            userNotFound.setTextFill(Color.RED);
                        }
                    } catch (SQLException eEnter) {
                        System.out.println("btLogin SQL exception");//test

                        userNotFound.setText("User or Password not found. Please try again.");
                        userNotFound.setTextFill(Color.RED);
                    }
                }
                else {
                    userNotFound.setText("Please fill out both username and password fields.");
                    userNotFound.setTextFill(Color.RED);
                }

            }});

        //login through login  button
        btLogIn.setOnAction((event) -> {    // lambda expression
            if (!userNameTf.getText().isEmpty() && !passwordTf.getText().isEmpty()) {
                try {
                    currentUserID = userLogin(userNameTf.getText(), passwordTf.getText());
                    userNotFound.setText("");
                    if (currentUserID!=0) {
                        userNotFound.setText("");

                        //SUCCESSFUL USER LOGIN: rerun all dashboard info:
                        String [] allTaskNames = getUserItem("Task", "TaskName");
                        String [] allTaskDesc = getUserItem("Task", "TaskDescription");
                        String [] allTaskDueDate = getUserItem("Task", "DueDate");
                        String [] allTaskOverdue = getUserItem("Task", "Overdue");
                        String [] allTaskCompletedStatus = getUserItem("Task", "Completed");



                        //call function that returns how many open tasks exist for current user
                        for (int i = 0; i < getUserItemsAmount("Task");i++) {
                            if (allTaskCompletedStatus[i].equals("0")) {
                                String overdueString = "";
                                if (allTaskOverdue[i].equals("1")) {overdueString = "Overdue!";}
                                Label labelTask = new Label("•  " + allTaskNames[i] + "\n" + allTaskDesc[i] +
                                        "\nDue: " + allTaskDueDate[i] + "        " + overdueString);
                                if (allTaskOverdue[i].equals("1")) {
                                    labelTask.setTextFill(Color.RED);
                                }

                                labelTask.setStyle("-fx-font: 12 Consolas;-fx-border-color:green;\n ");
                                taskInfo.getChildren().add(labelTask);
                                tasksScrollPane.setContent(taskInfo);
                            }
                        }
                        //Fill combo box with tasks
                        String [] allTaskNamesCombo = new String[0];
                        try {
                            allTaskNamesCombo = getTaskItemCombo("Task", "TaskName");
                        } catch (SQLException ex2) {
                            System.out.println("getUserItem method sql exception for short goal names");
                        }
                        String [] allLongGoalNames = new String[0];
                        allTasksList.addAll(allTaskNamesCombo);

                        taskInfo.getChildren().addAll(HBoxEditTask);
                        primaryStage.setScene(dashboard);
                    }
                    else {
                        userNotFound.setText("User or Password not found. Please try again.");
                        userNotFound.setTextFill(Color.RED);
                    }
                } catch (SQLException eEnter) {
                    System.out.println("btLogin SQL exception");//test

                    userNotFound.setText("User or Password not found. Please try again.");
                    userNotFound.setTextFill(Color.RED);
                }
            }
            else {
                userNotFound.setText("Please fill out both username and password fields.");
                userNotFound.setTextFill(Color.RED);
            }
        });

        //register  button handling. Un-hides all register fields and buttons
        btRegister.setOnAction((event) -> {
            userNameTf.setText("");
            passwordTf.setText("");
            tfFirstName.setText("");
            tfLastName.setText("");
            userNotFound.setText("");
            btRegistrationGoBack.setVisible(true);
            registrationFieldsVBox.setVisible(true);
            btSubmitRegistration.setVisible(true);
            btRegister.setVisible(false);
            btLogIn.setVisible(false);

        });

        //Go back from the registration page to login page without registering
        btRegistrationGoBack.setOnAction((event) -> {
            userNotFound.setText("");
            btRegistrationGoBack.setVisible(false);
            registrationFieldsVBox.setVisible(false);
            btSubmitRegistration.setVisible(false);
            btRegister.setVisible(true);
            btLogIn.setVisible(true);

        });



        //submit registration button handling
        btSubmitRegistration.setOnAction((event) -> {
            userNotFound.setText(" ");
            if (userNameTf.getText().length()>4 && passwordTf.getText().length()>4
                    && !tfFirstName.getText().isEmpty() && !tfLastName.getText().isEmpty()) {
                try {
                    if (submitRegistration(userNameTf.getText(), passwordTf.getText(), tfFirstName.getText(),
                            tfLastName.getText())) {
                        userNotFound.setTextFill(Color.GREEN);
                        userNotFound.setText("You have been registered.");
                        btRegistrationGoBack.setVisible(false);
                        registrationFieldsVBox.setVisible(false);
                        btSubmitRegistration.setVisible(false);
                        btRegister.setVisible(true);
                        btLogIn.setVisible(true);
                        userNameTf.setText("");
                        passwordTf.setText("");
                    }
                    else {
                        userNotFound.setTextFill(Color.RED);
                        userNotFound.setText("Username already exists; please pick another.");
                    }
                } catch (SQLException e) {
                    System.out.println("SQL exception on submitRegistration() method"); //test
                }
            }
            else {
                userNotFound.setTextFill(Color.RED);
                userNotFound.setText("Please complete all fields. Username and Password must be at least 5 characters long.");
            }
        });

        //Button to view and edit task specified by combo box selection on dashboard
        btEditTask.setOnAction((event) -> {
            try {
                labelTaskGoalNameInfo.setText(getSpecificWithTaskName((String)
                        comboBoxTasksEdit.getSelectionModel().getSelectedItem(), "TaskName"));
            } catch (SQLException e) {
            }
            try {
                labelTaskGoalDescInfo.setText(getSpecificWithTaskName((String)
                        comboBoxTasksEdit.getSelectionModel().getSelectedItem(), "TaskDescription"));
            } catch (SQLException e) {
            }
            try {
                labelTaskGoalDueDateInfo.setText("Due: " + getSpecificWithTaskName((String)
                        comboBoxTasksEdit.getSelectionModel().getSelectedItem(), "DueDate"));
            } catch (SQLException e) {
            }
            try {
                labelTaskGoalCreateDateInfo.setText("Created: " + getSpecificWithTaskName((String)
                        comboBoxTasksEdit.getSelectionModel().getSelectedItem(), "CreationDate"));
            } catch (SQLException e) {
            }

            primaryStage.setScene(taskGoalInfo);
        });

        btTaskGoalCancel.setOnAction((event) -> {
            comboBoxTasksEdit.setValue(null);
            primaryStage.setScene(dashboard);
        });

        //Dismiss and complete both make goal completed, so change completed status and regenerate dashboard
        btTaskGoalDismiss.setOnAction((event) -> {
            //update to complete
            try {
                statement.executeUpdate("UPDATE Task SET Completed= TRUE WHERE taskName = '" +
                        comboBoxTasksEdit.getSelectionModel().getSelectedItem() + "';");
            } catch (SQLException e) {
            }
            //clear fields
            comboBoxItemType.setValue(null);
            comboAllShortGoals.setValue(null);
            comboAllLongGoals.setValue(null);
            comboBoxTasksEdit.setValue(null);
            tfAddName.setText("");
            tfAddDescription.setText("");
            labelAddErrorMessage.setText("");
            datePickerAddDueDate.setValue(null);

            //empty previous fields in dashboard
            taskInfo.getChildren().clear();
            goalInfo.getChildren().clear();
            //regenerate dashboard

            try {

                //SUCCESSFUL USER LOGIN: rerun all dashboard info:
                String[] allTaskNames = getUserItem("Task", "TaskName");
                String[] allTaskDesc = getUserItem("Task", "TaskDescription");
                String[] allTaskDueDate = getUserItem("Task", "DueDate");
                String[] allTaskOverdue = getUserItem("Task", "Overdue");
                String[] allTaskCompletedStatus = getUserItem("Task", "Completed");
                String[] allTaskNamesCombo = getUserItem("Task", "TaskName");


                //call function that returns how many open tasks exist for current user
                for (int i = 0; i < getUserItemsAmount("Task"); i++) {
                    if (allTaskCompletedStatus[i].equals("0")) {
                        String overdueString = "";
                        if (allTaskOverdue[i].equals("1")) {
                            overdueString = "Overdue!";
                        }
                        Label labelTask = new Label("•  " + allTaskNames[i] + "\n" + allTaskDesc[i] +
                                "\nDue: " + allTaskDueDate[i] + "        " + overdueString);
                        if (allTaskOverdue[i].equals("1")) {
                            labelTask.setTextFill(Color.RED);
                        }

                        labelTask.setStyle("-fx-font: 12 Consolas;-fx-border-color:green;\n ");
                        taskInfo.getChildren().add(labelTask);
                        tasksScrollPane.setContent(taskInfo);
                    }
                }
                //Fill combo box with tasks
                String[] allTaskNamesArray = new String[0];
                try {
                    allTaskNamesArray = getTaskItemCombo("Task", "TaskName");
                } catch (SQLException ex2) {
                    System.out.println("getUserItem method sql exception for short goal names");
                }
                String[] allLongGoalNames = new String[0];
                allTasksList.addAll(allTaskNamesArray);

                taskInfo.getChildren().addAll(HBoxEditTask);
                primaryStage.setScene(dashboard);
            } catch (SQLException eEnter) {
            }

            primaryStage.setScene(dashboard);
        });


        btTaskGoalCancel.setOnAction((event) -> {
            primaryStage.setScene(dashboard);
        });

        //Dismiss and complete both make goal completed, so change completed status and regenerate dashboard
        btTaskGoalComplete.setOnAction((event) -> {
            //update to complete
            try {
                statement.executeUpdate("UPDATE Task SET Completed= TRUE WHERE taskName = '" +
                        comboBoxTasksEdit.getSelectionModel().getSelectedItem() + "';");
            } catch (SQLException e) {
            }
            //clear fields
            comboBoxItemType.setValue(null);
            comboAllShortGoals.setValue(null);
            comboAllLongGoals.setValue(null);
            comboBoxTasksEdit.setValue(null);
            tfAddName.setText("");
            tfAddDescription.setText("");
            labelAddErrorMessage.setText("");
            datePickerAddDueDate.setValue(null);

            //empty previous fields in dashboard
            taskInfo.getChildren().clear();
            goalInfo.getChildren().clear();

            //regenerate dashboard

            try {

                //SUCCESSFUL USER LOGIN: rerun all dashboard info:
                String[] allTaskNames = getUserItem("Task", "TaskName");
                String[] allTaskDesc = getUserItem("Task", "TaskDescription");
                String[] allTaskDueDate = getUserItem("Task", "DueDate");
                String[] allTaskOverdue = getUserItem("Task", "Overdue");
                String[] allTaskCompletedStatus = getUserItem("Task", "Completed");


                //call function that returns how many open tasks exist for current user
                for (int i = 0; i < getUserItemsAmount("Task"); i++) {
                    if (allTaskCompletedStatus[i].equals("0")) {
                        String overdueString = "";
                        if (allTaskOverdue[i].equals("1")) {
                            overdueString = "Overdue!";
                        }
                        Label labelTask = new Label("•  " + allTaskNames[i] + "\n" + allTaskDesc[i] +
                                "\nDue: " + allTaskDueDate[i] + "        " + overdueString);
                        if (allTaskOverdue[i].equals("1")) {
                            labelTask.setTextFill(Color.RED);
                        }

                        labelTask.setStyle("-fx-font: 12 Consolas;-fx-border-color:green;\n ");
                        taskInfo.getChildren().add(labelTask);
                        tasksScrollPane.setContent(taskInfo);
                    }
                }
                //Fill combo box with tasks
                String[] allTaskNamesCombo = new String[0];
                try {
                    allTaskNamesCombo = getUserItem("Task", "TaskName");
                } catch (SQLException ex2) {
                    System.out.println("getUserItem method sql exception for short goal names");
                }

                allTasksList.addAll(allTaskNamesCombo);

                taskInfo.getChildren().addAll(HBoxEditTask);
                primaryStage.setScene(dashboard);
            } catch (SQLException eEnter) {
            }

            primaryStage.setScene(dashboard);
        });




           imageviewAddTask.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                //prepare combo boxes with current user's goals
                String [] allShortGoalNames = new String[0];
                try {
                    allShortGoalNames = getUserItem("ShortTermGoals", "GoalName");
                } catch (SQLException e) {
                    System.out.println("getUserItem method sql exception for short goal names");
                }
                String [] allLongGoalNames = new String[0];
                try {
                    allLongGoalNames = getUserItem("LongTermGoals", "GoalName");
                } catch (SQLException e) {
                    System.out.println("getUserItem method sql exception for long goal names");
                }
                allShortGoalsList.addAll(allShortGoalNames);
                allLongGoalsList.addAll(allLongGoalNames);

                primaryStage.setScene(addTaskGoal);

            }
        });





        imageviewSearch.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                primaryStage.setScene(analysisScene);
            }
        });

        //Back to dash so clear the fields in the add task/goal page
        imageviewBackToDash.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                comboBoxItemType.setValue(null);
                comboAllShortGoals.setValue(null);
                comboAllLongGoals.setValue(null);
                tfAddName.setText("");
                tfAddDescription.setText("");
                primaryStage.setScene(dashboard);
            }
        });

        //Once new task is added, add to database, clear the fields, and re-run the dashboard task/goal generating
        imageviewConfirmAddTask.setOnMouseClicked(new EventHandler() {
            @Override
            public void handle(Event event) {
                //Add new Task/Goal to Database
                if ( !(tfAddName.getText().isEmpty() || comboBoxItemType.getSelectionModel().isEmpty()
                                || (datePickerAddDueDate.getValue()==null))) {
                    String dueDate = datePickerAddDueDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    dueDate = dueDate + " 12:00:00";
                    LocalDateTime ldt = LocalDateTime.now().plusDays(1);
                    DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
                    String creationDate = format2.format(ldt);
                    String type = "";
                    if(comboBoxItemType.getSelectionModel().getSelectedIndex()==0) {type = "Task";}
                    if(comboBoxItemType.getSelectionModel().getSelectedIndex()==1) {type = "ShortTermGoals";}
                    if(comboBoxItemType.getSelectionModel().getSelectedIndex()==2) {type = "LongTermGoals";}
                    String nestedGoal = "none";
                    if (type.equals("Task") && !(comboAllShortGoals.getSelectionModel().isEmpty())) {
                        nestedGoal = (String) comboAllShortGoals.getSelectionModel().getSelectedItem();
                    }
                    else if (type.equals("ShortTermGoals") && !(comboAllLongGoals.getSelectionModel().isEmpty())) {
                        nestedGoal = (String) comboAllLongGoals.getSelectionModel().getSelectedItem();
                    }
                    //run function to actually add it to database
                    try {
                        addTaskGoal(type, tfAddName.getText(), tfAddDescription.getText(), creationDate, dueDate,nestedGoal);

                    } catch (SQLException e) {
                        System.out.println("sql exception on addTaskGoal method"); //test
                    }
                    //clear fields

                    comboBoxItemType.setValue(null);
                    comboAllShortGoals.setValue(null);
                    comboAllLongGoals.setValue(null);
                    comboBoxTasksEdit.setValue(null);
                    tfAddName.setText("");
                    tfAddDescription.setText("");
                    labelAddErrorMessage.setText("");
                    datePickerAddDueDate.setValue(null);

                    //empty previous fields in dashboard
                    taskInfo.getChildren().clear();
                    goalInfo.getChildren().clear();


                    try {

                        //SUCCESSFUL USER LOGIN: rerun all dashboard info:
                        String[] allTaskNames = getUserItem("Task", "TaskName");
                        String[] allTaskDesc = getUserItem("Task", "TaskDescription");
                        String[] allTaskDueDate = getUserItem("Task", "DueDate");
                        String[] allTaskOverdue = getUserItem("Task", "Overdue");
                        String[] allTaskCompletedStatus = getUserItem("Task", "Completed");
                        String[] allTaskNamesCombo = getUserItem("Task", "TaskName");


                        //call function that returns how many open tasks exist for current user
                        for (int i = 0; i < getUserItemsAmount("Task"); i++) {
                            if (allTaskCompletedStatus[i].equals("0")) {
                                String overdueString = "";
                                if (allTaskOverdue[i].equals("1")) {
                                    overdueString = "Overdue!";
                                }
                                Label labelTask = new Label("•  " + allTaskNames[i] + "\n" + allTaskDesc[i] +
                                        "\nDue: " + allTaskDueDate[i] + "        " + overdueString);
                                if (allTaskOverdue[i].equals("1")) {
                                    labelTask.setTextFill(Color.RED);
                                }

                                labelTask.setStyle("-fx-font: 12 Consolas;-fx-border-color:green;\n ");
                                taskInfo.getChildren().add(labelTask);
                                tasksScrollPane.setContent(taskInfo);
                            }
                        }
                        //Fill combo box with tasks
                        String[] allTaskNamesArray = new String[0];
                        try {
                            allTaskNamesArray = getTaskItemCombo("Task", "TaskName");
                        } catch (SQLException ex2) {
                            System.out.println("getUserItem method sql exception for short goal names");
                        }
                        String[] allLongGoalNames = new String[0];
                        allTasksList.addAll(allTaskNamesArray);

                        taskInfo.getChildren().addAll(HBoxEditTask);
                        primaryStage.setScene(dashboard);
                    } catch (SQLException eEnter) {
                    }

                    primaryStage.setScene(dashboard);

                    }
                else {
                    labelAddErrorMessage.setTextFill(Color.RED);
                    labelAddErrorMessage.setStyle("-fx-font: 13 Consolas; ");
                    labelAddErrorMessage.setText("Please enter a name, type, and due date for the task/goal.");
                }
            }
        });




                    //Log out button - bring to homepage
        imageviewLogOut.setOnMouseClicked(new EventHandler() {
           @Override
             public void handle(Event event) {
            primaryStage.setScene(login);
            userNameTf.setText("");
            passwordTf.setText("");
            //empty previous fields in dashboard
            taskInfo.getChildren().clear();
            goalInfo.getChildren().clear();
            currentUserID = 0;  //reset current user
           }
        });



        primaryStage.setTitle("ProgressPal"); // Set the stage title
        primaryStage.getIcons().add(logo);
        primaryStage.setScene(login); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }



    //"start()" ends-------------------------------------------------------------------


    private void initializeDB() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
//      Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver loaded");

            // Establish a connection
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost/javabook", "krishan2", "tiger");

            System.out.println("Database connected");

            // Create a statement
            statement = connection.createStatement();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //LogIn button helper function (runs the SQL part)
    public int userLogin(String u, String p) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT UserID FROM Users WHERE Username = '"
                + u + "' AND UserPassword = '" + p+ "';");
        while (resultSet.next()) {

            return resultSet.getInt("UserID");
        }
        return 0; //if user/password does not match, current user stays at 0
    }

    //LogIn button helper function (runs the SQL part)
    public boolean submitRegistration(String username, String password, String fName, String lName) throws SQLException {
        ResultSet resultSet= statement.executeQuery("SELECT * FROM Users Where Username ='" + username + "';");
        int count = 0;
        while (resultSet.next()) {
            count += 1;
        }
        if (count >0) {
            return false;
        }
        else {
            statement.executeUpdate("INSERT INTO Users(UserID, FirstName, LastName, " +
                    " UserPassword, PartnerMessage, PartnerID, Username) VALUES (NULL, '" +fName+"', '" +lName +
                    "', '" + password + "', NULL, NULL, '" + username + "');");
            return true;

        }
    }

    //Get some specified column data for the current user from any specified table,
    //                     if its 1 piece of data
    public String [] getUserItem (String tableName, String columnName) throws SQLException {
        String []allItems = new String[400];  //max item size is 400

        ResultSet resultSet = statement.executeQuery("SELECT * " +
                " FROM " + tableName + " WHERE UserID= '" + currentUserID + "' " +
                             "ORDER BY DueDate;");
            int count = 0;
            while (resultSet.next()) {
                allItems[count] = resultSet.getString(columnName);
                count += 1;
            }
        return allItems;
    }

    //same as last function but just for use in getting combo box task names without including "completed" tasks
    public String [] getTaskItemCombo (String tableName, String columnName) throws SQLException {
        String []allItems = new String[400];  //max item size is 1000

        ResultSet resultSet = statement.executeQuery("SELECT * " +
                " FROM " + tableName + " WHERE UserID= '" + currentUserID + "' ORDER BY DueDate;");

        int count = 0;

        while (resultSet.next()) {
                allItems[count] = resultSet.getString(columnName);
                count += 1;
            }
        return allItems;
    }

    //Get amount of user items of specific kind (task, short goal, or long goal)
    public int getUserItemsAmount (String tableName) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * " +
                " FROM " + tableName + " WHERE UserID= '" + currentUserID + "' AND Completed =FALSE;");
        int count = 0;
        while (resultSet.next()) {
            count += 1;
        }
        return count;
    }

    //Get some specified column data for the current user from any specified table, if its 1 piece of data
    public String getSpecificWithTaskName (String taskName, String columnNeeded ) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT " + columnNeeded +
                " FROM Task WHERE TaskName= '" + taskName + "';");
        String s = "";
        while (resultSet.next()) {
            s = resultSet.getString(columnNeeded);
        }
        return s;
    }

    //Add a task or goal for the current user
    public void addTaskGoal(String type, String taskName, String taskDesc, String createDate, String dueDate, String nestedGoal) throws SQLException {
        if(type.equals("Task")){
            int nestedGoalID = 0;
            ResultSet rs = statement.executeQuery("SELECT ShortGoalID " +
                    " FROM ShortTermGoals WHERE GoalName= '" + nestedGoal + "';");

            while(rs.next()) {

                nestedGoalID = rs.getInt("ShortGoalID");
            }

            statement.executeUpdate("INSERT INTO Task(TaskID, UserID, CreationDate, DueDate, ShortGoalID, Completed, Overdue, PostTaskNotes,"
                    +"TaskName, TaskDescription) VALUES (NULL, " +currentUserID+
                    ", '" + createDate + "', '" + dueDate + "', NULL, FALSE, FALSE, NULL, '" + taskName + "', '"+taskDesc+"');");

            //if nested goal is present, adjust the entry
            if (nestedGoalID != 0) {

                statement.executeUpdate("UPDATE Task SET ShortGoalID=" +nestedGoalID + " WHERE taskName = '" +
                        taskName + "';");
            }


        }
        else if(type.equals("ShortTermGoals")) {

        }
        else if (type.equals("LongTermGoals")) {

        }
    }


    //Set User's Partner
    private void setPartner(int UserID) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT grade FROM Grades WHERE courseID = " + tfCourseId.getText()
        );

        while (resultSet.next()) {
            System.out.println(resultSet.getString("grade") );
            Label grade = new Label(resultSet.getString("grade"));
        }
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
