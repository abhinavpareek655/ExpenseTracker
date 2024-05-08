package com.example.expensetracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;

class User implements Serializable {
    private String username;
    private transient String password;
    private int budget;
    public User(String username, String password, int budget) {
        this.username = username;
        this.password = password;
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "user{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", budget=" + budget +
                '}';
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
public class HelloController {
    private Stage stage;
    @FXML
    TextField emailFieldSU;
    @FXML
    TextField emailField;
    @FXML
    TextField passwordField;
    @FXML
    Label errorLabel;
    @FXML
    Label errorLabelSU;
    @FXML
    TextField passwordFieldSU;
    @FXML
    TextField confirmPasswordFieldSU;
    private String getUsername(String email){
        return email.substring(0, email.indexOf('@'));
    }
    private String getPassword(String username){
        String password = null;
        try {
            FileInputStream fileIn = new FileInputStream("admin.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while (true) {
                User user = (User) in.readObject();
                if (user.getUsername().equals(username)) {
                    password = user.getPassword();
                    break;
                }
            }
        } catch (EOFException e) {
            // End of file reached
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return password;
    }
    private FileInputStream login(String email, String password) throws FileNotFoundException {
        String username = getUsername(email);
        String correctPassword = getPassword(username);
        if (password.equals(correctPassword)) {
            System.out.println("Login successful");
            return new FileInputStream(username + ".txt");
        } else {
            System.out.println("Login failed");
            return null;
        }
    }

    public void onLoginButton(ActionEvent actionEvent){
        if(!emailField.getText().isEmpty()) {
            String email = emailField.getText();
            if (!email.contains("@"))
                errorLabel.setText("Invalid email!");
            String password = passwordField.getText();
            try {
                FileInputStream userFile = login(email, password);
                if (userFile != null) {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
                        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle("Expanse Tracker");
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errorLabel.setText("Invalid email or password!");
                }
            } catch (FileNotFoundException e) {
                errorLabel.setText("Invalid email or password!");
                System.out.println(e);
            } catch (Exception e) {
                errorLabel.setText("Invalid email or password!");
                System.out.println(e);
            }
        }
    }
    public void onRegisterButton(ActionEvent actionEvent){
        try {
            Parent root= FXMLLoader.load(getClass().getResource("SignUpPage.fxml"));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onRegisterToLogin(ActionEvent actionEvent){
        try {
            Parent root= FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean isUserExist(String username){
        try {
            FileInputStream fileIn = new FileInputStream("admin.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while (true) {
                User user = (User) in.readObject();
                if (user.getUsername().equals(username)) {
                    return true;
                }
            }
        } catch (EOFException e) {
            // End of file reached
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void onSignUpButton(ActionEvent actionEvent){
        String email = emailFieldSU.getText();
        if(!email.contains("@"))
            errorLabelSU.setText("Invalid email!");
        String username = getUsername(email);
        if(isUserExist(username)){
            errorLabelSU.setText("User already exists!");
        }else{
            if(passwordFieldSU.getText().length()<8){
                errorLabelSU.setText("Password must be at least 8 characters long!");
            }
            else {
                if (passwordFieldSU.getText().equals(confirmPasswordFieldSU.getText())) {
                    User user = new User(username, passwordFieldSU.getText(), 0);
                    try {
                        FileOutputStream fileOut = new FileOutputStream("admin.txt");
                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
                        out.writeObject(user);
                        out.close();
                        fileOut.close();
                        errorLabel.setText("User registered successfully!");

                        Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
                        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle("Expanse Tracker");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    errorLabelSU.setText("Passwords do not match!");
                }
            }
        }
    }
}