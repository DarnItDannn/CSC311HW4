package org.example.csc311hw4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class (HelloController.java) contains the methods to perform JavaFX operations and the bulk of the methods involved in
 * updating the database and visually outputting it to SceneBuilder. Along with all the methods behind
 * the buttons in the GUI, and the updates for the TextFields, ListViews, etc.
 *
 * @author Danish Syed
 */


public class HelloController {

    /*
    *
    * Declarations for all the FXML items and
    * any various variables used throughout the program.
    *
     */


    @FXML
    private Text statusText;

    @FXML
    private TableView<Movie> movieTableView;



    @FXML
    private TableColumn<Movie, String> titleColumn;

    @FXML
    private TableColumn<Movie, Integer> yearColumn;

    @FXML
    private TableColumn<Movie, Double> salesColumn;

    @FXML
    private ObservableList<Movie> movieObservList;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField yearTextField;

    @FXML
    private TextField salesTextField;

    static Connection conn;

    private File selectedFile;

    int tempYear;
    double tempSales;


    /*
    *
    * All the Methods Are Below:
    *
     */


    // Initialize Method which sets up the columns for the TableView and their values.
    // Sets the status to blank.
    // Establishes a database connection.
    // Associates the ObservableList with the TableView.

    public void initialize()
    {
        titleColumn.setCellValueFactory(
                new PropertyValueFactory<Movie, String>("title"));
        yearColumn.setCellValueFactory(
                new PropertyValueFactory<Movie, Integer>("year"));
        salesColumn.setCellValueFactory(
                new PropertyValueFactory<Movie, Double>("sales"));

        statusText.setText("");

        createDatabase_connection();
        movieObservList = movieTableView.getItems();
    }


    // Method to delete all data in the database NOT drop.
    // This is called in openFile to ensure the database is being cleared
    // prior to loading in the new information.

    public void deleteAllDBInfo()
    {
        String sql = "DELETE FROM MovieDB";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            int rowsDeleted = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Code for MenuItem Open JSON
    // Before starting the bulk of the code, it will delete all data from the DB
    // and clear the TableView.
    // Then it DOES populate the ObservableList or TableView.
    // Then it DOES populate the database.

    public void openFile()
    {
        // Start of Method will change status to "opening file..." till the file is open.
        statusText.setText("Opening file...");

        deleteAllDBInfo();
        // deleteAllDBInfo method will cause issue if someone
        // does this as the first action run.
        // Fix: Create db connection in initialize

        movieTableView.getItems().clear(); // Clear the TableView.

        // Code for Opening Directly to File Window within Project Directory
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        String currentDir = System.getProperty("user.dir");
        File initialDirectory = new File(currentDir);
        fileChooser.setInitialDirectory(initialDirectory);

        Stage stage = (Stage) movieTableView.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);


        // Code for reading data from JSON To Database

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try (FileReader fr = new FileReader(selectedFile))
        {
            Movie[] ea = gson.fromJson(fr, Movie[].class);

            createDatabase_connection();
            for (int i = 0; i < ea.length; i++)
            {
                insertDataInDB(ea[i]);
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Call handleListRecords to add data into the TableView.
         handleListRecords();

        // Update Status to Opened File.

        statusText.setText("Opened File: " + selectedFile.getName() + " from " + selectedFile.getAbsolutePath() + "!");

    }

    // Code for List Records Button
    // Queries from Database into ObservableList which then populates the TableView.

    public void handleListRecords()
    {
        try
        {
            String query = "SELECT * FROM MovieDB";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            movieTableView.getItems().clear();
            movieObservList.clear();

            // Adds everything in the query to the items.
            while (resultSet.next())
            {
                String title = resultSet.getString("Title");
                int year = resultSet.getInt("Year");
                double sales = resultSet.getDouble("Sales");

                Movie newMovie = new Movie(title, year, sales);

                movieObservList.add(newMovie);

            }

            movieTableView.setItems(movieObservList);

            resultSet.close();
            statement.close();

        }

        catch (SQLException e)
        {
            e.printStackTrace();
        }

        // Update Status to Listed Records
        statusText.setText("Listed Records from Database into Tableview.");

    }


    // Code for MenuItem Save Json
    public void saveToJSON() throws FileNotFoundException
    {

        statusText.setText("Saving File");

        // Code for Opening Directly to File Window to save within Project Directory
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        String currentDir = System.getProperty("user.dir");
        File initialDirectory = new File(currentDir);
        fileChooser.setInitialDirectory(initialDirectory);

        Stage stage = (Stage) movieTableView.getScene().getWindow();
        selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null)
        {

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            // Code to write data to the selected File through the movieObservList observableList.
            try (PrintWriter writer = new PrintWriter(selectedFile))
            {
                writer.println(gson.toJson(movieObservList));

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        // Update Status to File Saved
        statusText.setText("File Saved as " + selectedFile + "!");
    }

    // Method for checking new addition with validation.
    // If it meets criteria, it will add to the Database and TableView.
    // Validation class will also check if the text field is empty.
    // Otherwise, an alertbox specific to errors appears.
    public void addMovieButton()
    {

        // Read inputs from the TextFields.

        String title = titleTextField.getText();
        String year = yearTextField.getText();
        String sales = salesTextField.getText();

        // Make a new Validation object with the fields given from the user input.
        Validation validation = new Validation(title, year, sales);


        // Regular Expressions for each different TextField.

        validation.checkTitle(title, "[A-Z][\\w*\\d*\\s*[,]*[.]*[-]*[:]*]*");
        validation.checkYear(year,"[0-9]{4}");
        validation.checkSales(sales,"[0-9]*[.]*\\d+");


        // If Statement that checks the results of the checkers.
        // IF it meets all criteria, create a new Movie object and insert it into the database.
        // Else, send an alert with an error message either letting user know
        // The TextField(s) are/is empty or the specifications for that TextField.

        if (validation.getChecker1() == "" && validation.getChecker2() == "" && validation.getChecker3() == "")
        {
            // Parse the year and sales values to their true data types.

            tempYear = Integer.parseInt(year);
            tempSales = Double.parseDouble(sales);

            // Create new movie Object with the checked values.
            Movie newMovie = new Movie(title, tempYear, tempSales);

            // Insert new Movie entry into database and ObservableList/TableView.
            insertDataInDB(newMovie);
            movieObservList.add(newMovie);

            // Update status to reflect new movie addition.
            statusText.setText("A movie has been inserted: " + newMovie.getTitle());
        }

        else
        {
            // Output an alert based off the regular expressions checkers.

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Input");
            alert.setContentText(validation.getChecker1() + "\n" + validation.getChecker2() +
                    "\n" + validation.getChecker3());
            alert.showAndWait();

        }

    }

    // Method for Delete Record button.
    // This method will delete the selected Movie from the TableView and call
    // the deleteDataFromDB method which will delete it from the database.

    public void deleteRecord()
    {

        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        deleteDataFromDB(selectedMovie);

        movieObservList.remove(selectedMovie);

        // Update status to reflect removal of movie.
        statusText.setText("A movie has been deleted: " + selectedMovie.getTitle());

    }

    // Method for deleting the selected item from the database
    public void deleteDataFromDB(Movie movie)
    {
        String sql = "DELETE FROM MovieDB WHERE Title = ? AND Year = ? AND Sales = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, movie.getTitle());
            preparedStatement.setInt(2, movie.getYear());
            preparedStatement.setDouble(3, movie.getSales());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Code for Exit MenuItem
    public void exitProgram()
    {
        System.exit(0);
    }

    // Code for About MenuItem
    public void aboutButton()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Movie Database");
        alert.setHeaderText("Name and Integrity Statement");
        alert.setContentText("Danish Syed\n\nI certify that this submission is my original work.");
        alert.showAndWait();
        statusText.setText("About Button Pressed.");
    }

    // Code for MenuItem CreateTable
    @FXML
    public void handleCreateTable()
    {
        dropTable();
        createDatabase_connection();
        createTableinDB();

        statusText.setText("Database Created.");
    }

    // Method for Dropping the table in the DB
    public void dropTable()
    {
        try {
            String dbFilePath = ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
            Connection conn = DriverManager.getConnection(databaseURL);
            String sql;
            sql = "DROP TABLE MovieDB";
            Statement createTableStatement = conn.createStatement();
            createTableStatement.execute(sql);
            conn.commit();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    // Method for establishing a DB connection.
    public void createDatabase_connection()
    {
        String dbFilePath = ".//MovieDB.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;

        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try (Database db = DatabaseBuilder.create(Database.FileFormat.V2010, new File(dbFilePath))) {
                System.out.println("The database file has been created.");
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }

        }

        try {
            databaseURL = "jdbc:ucanaccess://.//MovieDB.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(HelloController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Method for creating a table within the database.
    public static void createTableinDB()
    {
        try {
            String dbFilePath = ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
            Connection conn = DriverManager.getConnection(databaseURL);
            String sql;
            sql = "CREATE TABLE MovieDB (Title nvarchar(255), Year nvarchar(255), Sales nvarchar(255))";
            Statement createTableStatement = conn.createStatement();
            createTableStatement.execute(sql);
            conn.commit();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }


    //  Method for inserting the data into the Movie Database.
    public void insertDataInDB(Movie movie)
    {
        String sql = "INSERT INTO MovieDB (Title, Year, Sales) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, movie.getTitle());
            preparedStatement.setInt(2, movie.getYear());
            preparedStatement.setDouble(3, movie.getSales());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}