package org.example.csc311hw4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloController {


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


    public void initialize()
    {
        titleColumn.setCellValueFactory(
                new PropertyValueFactory<Movie, String>("title"));
        yearColumn.setCellValueFactory(
                new PropertyValueFactory<Movie, Integer>("year"));
        salesColumn.setCellValueFactory(
                new PropertyValueFactory<Movie, Double>("sales"));

        movieObservList = movieTableView.getItems();
    }

    // Code for MenuItem Open JSON
    public void openFile()
    {
        statusText.setText("Opening file...");

        // Code for Opening Directly to File Window within Project Directory
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        String currentDir = System.getProperty("user.dir");
        File initialDirectory = new File(currentDir);
        fileChooser.setInitialDirectory(initialDirectory);

        Stage stage = (Stage) movieTableView.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try (FileReader fr = new FileReader(selectedFile)) {
            Movie[] ea = gson.fromJson(fr, Movie[].class);

            createDatabase_connection();
            for (int i = 0; i < ea.length; i++) {
                insertDataInDB(ea[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        statusText.setText("Opened File: " + selectedFile.getName() + "!");

    }

    // Code for List Records Button
    public void handleListRecords()
    {
        try
        {
            Gson gson = new Gson();
            FileReader fr = new FileReader(selectedFile);

            // Create a Movie Array that reads from the selected Json File into the array
            Movie[] movies = gson.fromJson(fr, Movie[].class);


            // For each grade in the grade array, add it to the ObservableList.
            for (Movie movie : movies)
            {
                movieObservList.add(movie);
            }

            fr.close();



        }

        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
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

            // Code to write data to the selected File through the gradeList observableList.
            try (PrintWriter writer = new PrintWriter(selectedFile))
            {
                writer.println(gson.toJson(movieObservList));

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        statusText.setText("File Saved as " + selectedFile + "!");
    }


    public void deleteRecord()
    {


        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();

        movieObservList.remove(selectedMovie);

        statusText.setText("Deleted " + selectedMovie.getTitle() + " From TableView.");

    }

    public void aboutButton()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Movie Database");
        alert.setHeaderText("Name and Integrity Statement");
        alert.setContentText("Danish Syed\n\nI certify that this submission is my original work.");
        alert.showAndWait();
    }

    @FXML
    public void handleCreateTable()
    {
        dropTable();
        createDatabase_connection();
        createTableinDB();
//        insertDataInDB();
    }

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


    //  Method for inserting the data created from the game into the database.
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