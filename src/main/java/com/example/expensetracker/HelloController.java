package com.example.expensetracker;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
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
    public int getBudget(){ return budget; }
    public void setBudget(int budget){
        this.budget = budget;
    }
}
class Expense implements Serializable{
    private static final long serialVersionUID = 7052176155430362228L;
    private String category;
    private Date date;
    private int amount;
    public Expense(String category,Date date, int amount) {
        this.category = category;
        this.date = date;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "expense{" +
                "category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", amount='" + amount + '\'' +
                "}\n";
    }
    public Date getDate(){
        return this.date;
    }

    public int getAmount() {
        return amount;
    }

    public String getCategory(){
        return category;
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
    protected static String userName;
    private static FileOutputStream userFile;
    private static Expense expense;
    protected static Stage stage;
    @FXML
    Label monthlyExpense;
    @FXML
    Label NumberFormatExceptionLabel;
    @FXML
    TextField amountField;

    @FXML
    TextField OTPField;
    @FXML
    TextField emailFieldSU;
    @FXML
    TextField emailFieldRP;
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
    @FXML
    Label budgetWarning;
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
                        userName = getUsername(email);
                        userFile = new FileOutputStream(userName+".txt",true);
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
//                System.out.println(user);
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
        try{
            if(isUserExist(userName)){
                ArrayList<User> users = new ArrayList<>();

                // Read users from admin.txt
                try (FileInputStream fileIn = new FileInputStream("admin.txt");
                     ObjectInputStream in = new ObjectInputStream(fileIn)) {
                    while (true) {
                        try {
                            User readUser = (User) in.readObject();
                            users.add(readUser);
                        } catch (EOFException e) {
                            // End of file reached
                            break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                // Write users back to admin.txt, excluding the one with the same username as userName
                try (FileOutputStream fileOut = new FileOutputStream("admin.txt");
                     ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                    for (User user : users) {
                        if (!user.getUsername().equals(userName)) {
                            out.writeObject(user);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Parent root = FXMLLoader.load(getClass().getResource("SignUpPage.fxml"));
                stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Expanse Tracker");
                stage.show();
                System.out.println(userName);
            }
            else {
                boolean append = new File("admin.txt").length() > 0;
                FileOutputStream fileOut = new FileOutputStream(new File("admin.txt"), true);
                ObjectOutputStream out = append ? new AppendableObjectOutputStream(fileOut) : new ObjectOutputStream(fileOut);
                out.writeObject(user);
                out.close();
                fileOut.close();

                Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
                stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Expanse Tracker");
                stage.show();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
    public void onForgotPasswordButton(ActionEvent actionEvent){
        try{
            Parent root= FXMLLoader.load(getClass().getResource("ResetPage.fxml"));
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
    public void onContinueButton(ActionEvent actionEvent){
        String email = emailFieldRP.getText();

        try {
            userName = getUsername(email);
            otp = generateOTP();
            new Thread(() -> {
                sendEmail(otp,"OTP for Expanse Tracker",email,"2022btcse002@curaj.ac.in");
            }).start();
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
    }
    public void onRPSignUpButton(ActionEvent actionEvent){
        try{
            Parent root= FXMLLoader.load(getClass().getResource("SignUpPage.fxml"));
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
    public void onLogOutButton(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Press OK to logout, Cancel to stay on the page");
        if(alert.showAndWait().get()== ButtonType.OK){
            try {
                Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
                stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Expanse Tracker");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void onAddExpenseButton(ActionEvent actionEvent){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AddingPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void EntertainmentButton(ActionEvent actionEvent){
        try {
            int amount = Integer.parseInt(amountField.getText());
            expense = new Expense("entertainment",new Date(),amount);
            boolean append = new File("admin.txt").length() > 0;
//            FileOutputStream userFile = new FileOutputStream(new File("abhinavpareek655.txt"), true);
            ObjectOutputStream out = append ? new AppendableObjectOutputStream(userFile) : new ObjectOutputStream(userFile);
            System.out.println("object added");
            System.out.println(expense);
            out.writeObject(expense);
//            out.close();
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (NumberFormatException e){
            NumberFormatExceptionLabel.setText("Enter valid amount");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void EducationButton(ActionEvent actionEvent){
        try {
            int amount = Integer.parseInt(amountField.getText());
            boolean append = new File("admin.txt").length() > 0;
//            FileOutputStream userFile = new FileOutputStream(new File("abhinavpareek655.txt"), true);
            ObjectOutputStream out = append ? new AppendableObjectOutputStream(userFile) : new ObjectOutputStream(userFile);
            expense = new Expense("education",new Date(),amount);
            System.out.println("object added");
            System.out.println(expense);
            out.writeObject(expense);
//            out.close();
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (NumberFormatException e){
            NumberFormatExceptionLabel.setText("Enter valid amount");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void FoodButton(ActionEvent actionEvent){
        try {
            int amount = Integer.parseInt(amountField.getText());
            boolean append = new File("admin.txt").length() > 0;
//            FileOutputStream userFile = new FileOutputStream(new File("abhinavpareek655.txt"), true);
            ObjectOutputStream out = append ? new AppendableObjectOutputStream(userFile) : new ObjectOutputStream(userFile);
            expense = new Expense("food",new Date(),amount);
            System.out.println("object added");
            System.out.println(expense);
            out.writeObject(expense);
//            out.close();
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (NumberFormatException e){
            NumberFormatExceptionLabel.setText("Enter valid amount");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void HealthButton(ActionEvent actionEvent){
        try {
            int amount = Integer.parseInt(amountField.getText());
            boolean append = new File("admin.txt").length() > 0;
//            FileOutputStream userFile = new FileOutputStream(new File("abhinavpareek655.txt"), true);
            ObjectOutputStream out = append ? new AppendableObjectOutputStream(userFile) : new ObjectOutputStream(userFile);
            expense = new Expense("health",new Date(),amount);
            System.out.println("object added");
            System.out.println(expense);
            out.writeObject(expense);
//            out.close();
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (NumberFormatException e){
            NumberFormatExceptionLabel.setText("Enter valid amount");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void TravelButton(ActionEvent actionEvent){
        try {
            int amount = Integer.parseInt(amountField.getText());
            boolean append = new File("admin.txt").length() > 0;
//            FileOutputStream userFile = new FileOutputStream(new File("abhinavpareek655.txt"), true);
            ObjectOutputStream out = append ? new AppendableObjectOutputStream(userFile) : new ObjectOutputStream(userFile);
            expense = new Expense("travel",new Date(),amount);
            System.out.println("object added");
            System.out.println(expense);
            out.writeObject(expense);
//            out.close();
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
            monthlyExpense.setText("₹ "+Integer.toString(calculateTotalExpenses(userName)));
        }
        catch (NumberFormatException e){
            NumberFormatExceptionLabel.setText("Enter valid amount");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int calculateTotalExpenses(String username) {
        int totalExpenses = 0;

        // Get the first day of the current month and today's date
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = cal.getTime();
        Date today = new Date();

        try (FileInputStream fileIn = new FileInputStream(username+".txt");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            while (true) {
                try {
                    Expense expense = (Expense) in.readObject();
                    Date expenseDate = expense.getDate();
                    System.out.println(expense);
                    // Check if the date of the expense is within the desired range
                    if (!expenseDate.before(firstDayOfMonth) && !expenseDate.after(today)) {
                        totalExpenses += expense.getAmount();
                    }
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return totalExpenses;
    }
    public void showMonthlyExpense(ActionEvent actionEvent){
        int budget = SetBudget.budget;
        int currentExpenses = calculateTotalExpenses(userName);
        try {
            monthlyExpense.setText("₹ "+currentExpenses+" of ₹ "+budget);
        }catch (Exception e){
            System.out.println(e);
        }
        if(currentExpenses>=budget){
            budgetWarning.setText("Budget Limit Exceeded!");
        }
    }
    public void onMothlyExpensesButton(ActionEvent actionEvent){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MonthlyExpensesPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setBudgetButton(ActionEvent actionEvent){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("SetBudget.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (Exception e){
            System.out.println("setBudgetButton: "+e);
        }
    }
    public void categorizedExpensesButton(ActionEvent actionEvent){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("CategorizedExpensesPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expanse Tracker");
            stage.show();
        }
        catch (Exception e){
            System.out.println("setBudgetButton: "+e);
        }
    }
}