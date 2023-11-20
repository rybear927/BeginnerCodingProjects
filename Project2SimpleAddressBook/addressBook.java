/**
 * Address Book Application
 * 
 * This JavaFX application serves as an address book manager. It allows users to add, view, and update
 * addresses stored in a Random Access File. The user interface provides options to navigate through 
 * addresses (first, next, previous, last), update existing addresses, and add new ones.
 * 
 * The program demonstrates the use of JavaFX for UI creation, RandomAccessFile for data storage,
 * and functional interfaces for streamlined file operations.
 * 
 * Author: Ryan Hellmann
 * Date: 10/27/23
 * Version: 1.0
 * 
 * Note: This project is part of my portfolio to demonstrate my coding skills and understanding of JavaFX
 * and file handling in Java.
 */

package Project2SimpleAddressBook;

//import statements
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.RandomAccessFile;

public class exercise09 extends Application {
    private static final String FILE_PATH = "addressBookEx09";
    private long currentAddressPointer = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
      
        //name + street rows
        TextField tfName = new TextField();
        tfName.setPrefColumnCount(24);
        Label labelName = new Label("Name", tfName);
        labelName.setContentDisplay(ContentDisplay.RIGHT);
        TextField tfStreet = new TextField();
        tfStreet.setPrefColumnCount(24);
        Label labelStreet = new Label("Street", tfStreet);
        labelStreet.setContentDisplay(ContentDisplay.RIGHT);

        //city, state, zip row
        TextField tfCity = new TextField();
        tfCity.setPrefColumnCount(10);
        Label labelCity = new Label("City   ", tfCity);
        labelCity.setContentDisplay(ContentDisplay.RIGHT);
        TextField tfState = new TextField();
        tfState.setPrefColumnCount(2);
        Label labelState = new Label("State", tfState);
        labelState.setContentDisplay(ContentDisplay.RIGHT);
        TextField tfZip = new TextField();
        tfZip.setPrefColumnCount(4);
        Label labelZip = new Label("Zip", tfZip);
        labelZip.setContentDisplay(ContentDisplay.RIGHT);
        HBox hBoxCityStateZip = new HBox(labelCity, labelState, labelZip);
        hBoxCityStateZip.setAlignment(Pos.CENTER);
        hBoxCityStateZip.setSpacing(4);

        //button row
        Button btAdd = new Button("Add");
        Button btFirst = new Button("First");
        Button btNext = new Button("Next");
        Button btPrevious = new Button("Previous");
        Button btLast = new Button("Last");
        Button btUpdate = new Button("Update");
        HBox hBoxButtons = new HBox(btAdd, btFirst, btNext, btPrevious, btLast, btUpdate);
        hBoxButtons.setAlignment(Pos.CENTER);
        hBoxButtons.setSpacing(3);

        //Main pane
        VBox vBoxMainPane = new VBox(labelName, labelStreet, hBoxCityStateZip, hBoxButtons);
        vBoxMainPane.setAlignment(Pos.CENTER);
        vBoxMainPane.setPadding(new Insets(5));
        vBoxMainPane.setSpacing(4);

        //button functionality
        btAdd.setOnAction(e -> handleFileOperation(raf -> addAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip, true)));
        btFirst.setOnAction(e -> handleFileOperation(raf -> getFirstAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip)));
        btNext.setOnAction(e -> handleFileOperation(raf -> getNextAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip)));
        btPrevious.setOnAction(e -> handleFileOperation(raf -> getPreviousAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip)));
        btLast.setOnAction(e -> handleFileOperation(raf -> getLastAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip)));
        btUpdate.setOnAction(e -> handleFileOperation(raf -> updateAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip)));

        Scene scene = new Scene(vBoxMainPane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Exercise17_09");
        primaryStage.show();
    }

    //add an address to the file
    public void addAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip, boolean end) throws IOException{
        if (end) {
            //set the pointer to the end of the file if selected
            raf.seek(raf.length());
        }

        //write the textFields to file
        raf.writeBytes(ensureFixedLength(tfName.getText().trim(), 32));
        raf.writeBytes(ensureFixedLength(tfStreet.getText().trim(), 32));
        raf.writeBytes(ensureFixedLength(tfCity.getText().trim(), 20));
        raf.writeBytes(ensureFixedLength(tfState.getText().trim(), 2));
        raf.writeBytes(ensureFixedLength(tfZip.getText().trim(), 5));

        //clear the textFields
        tfName.clear();
        tfStreet.clear();
        tfCity.clear();
        tfState.clear();
        tfZip.clear();
    }

    //present the first address in the file
    public void getFirstAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip) throws IOException{
        currentAddressPointer = 0;
        raf.seek(currentAddressPointer);
        getAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip);
    }

    //get the next address
    public void getNextAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip) throws IOException{
        if (currentAddressPointer >= raf.length()) {
            getFirstAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip);
        } else {
            getAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip);
        }
    }

    //get the previous address in the list
    public void getPreviousAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip) throws IOException{
        if (currentAddressPointer <= 91) { //if it's at the end of the first address or under, go to the last address
            getLastAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip);
        } else {
            currentAddressPointer -= (91 + 91); //since we're at the end of the current address, we have to go 2 addresses back to get to the beginning of the previous one
            getAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip);
        }
    }

    //get the last address in the list
    public void getLastAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip) throws IOException{
        currentAddressPointer = raf.length() - 91;
        raf.seek(currentAddressPointer);
        getAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip);
    }

    //update current address
    public void updateAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip) throws IOException{
        currentAddressPointer -= 91;
        raf.seek(currentAddressPointer);
        addAddress(raf, tfName, tfStreet, tfCity, tfState, tfZip, false);
    }

    //get an address
    public void getAddress(RandomAccessFile raf, TextField tfName, TextField tfStreet, TextField tfCity, TextField tfState, TextField tfZip) throws IOException{
        raf.seek(currentAddressPointer);
        setTextFromRAF(raf, tfName,32);
        setTextFromRAF(raf, tfStreet,32);
        setTextFromRAF(raf, tfCity,20);
        setTextFromRAF(raf, tfState,2);
        setTextFromRAF(raf, tfZip,5);
        currentAddressPointer += 91;
    }

    //get the bytes from the file and put them in the TextField
    private void setTextFromRAF(RandomAccessFile raf, TextField textField, int byteLength) throws IOException{
        byte[] bytes = new byte[byteLength];
        raf.readFully(bytes);
        textField.setText(new String(bytes).trim());
    }

    //make sure each string is set to a fixed length
    public String ensureFixedLength(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            StringBuilder inputBuilder = new StringBuilder(input);
            while (inputBuilder.length() < length) {
                inputBuilder.append(" ");
            }
            return inputBuilder.toString();
        }
    }

    //helper method that opens the file in a try/catch block
    private void handleFileOperation(FileOperation operation) {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "rw")) {
            operation.execute(raf);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    //allows us to pass different behaviors (like adding an address, updating an address, etc.) to the handleFileOperation method 
    //without repeating the code for opening, closing, and handling exceptions of the RandomAccessFile. chatgpt taught me how to do this 
    //part as it's a little above my pay grade currently
    @FunctionalInterface
    interface FileOperation {
        void execute(RandomAccessFile raf) throws IOException;
    }
}
