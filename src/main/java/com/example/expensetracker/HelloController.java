package com.example.expensetracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class User implements Serializable {
    private String username;
    private String password;
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
                "}\n";
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
class AppendableObjectOutputStream extends ObjectOutputStream {
    public AppendableObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        // do not write a header, but reset:
        reset();
    }
}
public class HelloController{
    private static User user;
    private Stage stage;
    @FXML
    TextField OTPField;
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
    private static String otp;
    public String generateOTP(){
        int[][] randomRanges = {{48,57},{65,90},{97,122}};
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i=0;i<5;i++){
            int dice = random.nextInt(0,randomRanges.length);
            otp.append((char)random.nextInt(randomRanges[dice][0],randomRanges[dice][1]));
        }
        return otp.toString();
    }
    public void sendEmail(String message, String subject, String to, String from){
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES: "+properties);
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("2022btcse002@curaj.ac.in","#$1000000");
            }
        });

        session.setDebug(true);

        MimeMessage m = new MimeMessage(session);
        try {
            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
            m.setText(message);
            Transport.send(m);
            System.out.println("Sent!!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private static String getUsername(String email){
        return email.substring(0, email.indexOf('@'));
    }
    private String getPassword(String username){
        String password = null;
        try {
            FileInputStream fileIn = new FileInputStream("admin.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while (true) {
                User user = (User) in.readObject();
//                System.out.println(user);
                if (user.getUsername().equals(username)) {
                    password = user.getPassword();
                    break; // Break the loop as soon as the user is found
                }
            }
        } catch (EOFException e) {
            // End of file reached
            System.out.println("User not found");
        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
            System.out.println("admin file not found");
        }

        return password;
    }
    private boolean login(String email, String password) {
        try{
            String username = getUsername(email);
            String correctPassword = getPassword(username);
            if (password.equals(correctPassword)) {
                System.out.println("Login successful");
                return true;
            } else {
                System.out.println("password incorrect");
                return false;
            }
        }
        catch (Exception e){
            System.out.println("user not found");
            return false;
        }
    }

    public void onLoginButton(ActionEvent actionEvent){
        if(!emailField.getText().isEmpty()) {
            if (!emailField.getText().contains("@"))
                errorLabel.setText("Invalid email!");
            else{
                errorLabel.setText("");
            }
            String email = emailField.getText();
            String password = passwordField.getText();
            try {
                boolean loginStatus = login(email, password);
                if (loginStatus) {
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
            }
            catch (Exception e) {
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
                System.out.println(user);
                if (user.getUsername().equals(username)) {
                    return true;
                }
            }
        } catch (EOFException e) {
            // End of file reached
            System.out.println("user not found");
            return false;
        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
            System.out.println("admin file not found");
            return false;
        }
    }
    public void onSignUpButton(ActionEvent actionEvent){
        String email = emailFieldSU.getText();
        if(!email.contains("@"))
            errorLabelSU.setText("Invalid email!");
        else
            errorLabelSU.setText("");

        String username = getUsername(email);
        if(isUserExist(username)){
            errorLabelSU.setText("User already exists!");
        }
        else{
            if(passwordFieldSU.getText().length()<8){
                errorLabelSU.setText("Password must be at least 8 characters long!");
            }
            else {
                if (passwordFieldSU.getText().equals(confirmPasswordFieldSU.getText())) {
                    username = getUsername(emailFieldSU.getText());
                    user = new User(username, passwordFieldSU.getText(),Integer.MAX_VALUE);
                    try {
                        otp = generateOTP();
                        new Thread(() -> {
                            sendEmail(otp,"OTP for Expanse Tracker",email,"2022btcse002@curaj.ac.in");
                        }).start();
////                        boolean append = new File("admin.txt").length() > 0;
////                        FileOutputStream fileOut = new FileOutputStream(new File("admin.txt"), true);
////                        ObjectOutputStream out = append ? new AppendableObjectOutputStream(fileOut) : new ObjectOutputStream(fileOut);
////                        out.writeObject(user);
////                        out.close();
////                        fileOut.close();
////                        errorLabelSU.setText("User registered successfully!");
                        Parent root= FXMLLoader.load(getClass().getResource("OTPPage.fxml"));
                        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle("Expanse Tracker");
                        stage.show();
                    } catch (IOException e) {
                        System.out.println("admin file not found");
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                } else {
                    errorLabelSU.setText("Passwords do not match!");
                }
            }
        }
    }
    public void onChangeEmailButton(ActionEvent actionEvent){
        try{
            Parent root= FXMLLoader.load(getClass().getResource("SignUpPage.fxml"));
            stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void onVerifyButton(ActionEvent actionEvent){
        if(OTPField.getText().equals(otp)){
//            System.out.println("OTP verified");
            try{
                boolean append = new File("admin.txt").length() > 0;
                FileOutputStream fileOut = new FileOutputStream(new File("admin.txt"), true);
                ObjectOutputStream out = append ? new AppendableObjectOutputStream(fileOut) : new ObjectOutputStream(fileOut);
                out.writeObject(user);
                out.close();
                fileOut.close();
                Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
                stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Expanse Tracker");
                stage.show();
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }
}