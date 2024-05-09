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
import java.util.ArrayList;

public class SetBudget {
//    String username = HelloController.userName;
    String username = HelloController.userName;
    protected static int budget;
    Stage stage = HelloController.stage;
    @FXML
    Label currentBudget;
    @FXML
    TextField setBudgetField;
    @FXML
    Label warningLabel;
    public void showCurrentBudgetButton(ActionEvent actionEvent){
        try {
            FileInputStream fileIn = new FileInputStream("admin.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while (true) {
                User user = (User) in.readObject();
                if (user.getUsername().equals(username)) {
                    budget = user.getBudget();
                    break; // Break the loop as soon as the user is found
                }
            }
        } catch (EOFException e) {
            // End of file reached
            System.out.println("User not found");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("admin file not found");
        }
        currentBudget.setText(Integer.toString(budget));
    }
    public void onSetBudgetButton(ActionEvent actionEvent){
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
                if (!user.getUsername().equals(username)) {
                    out.writeObject(user);
                }
                else{
                    User currentUser = new User(username,user.getPassword(),Integer.parseInt(setBudgetField.getText()));
                    out.writeObject(currentUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        budget = Integer.parseInt(setBudgetField.getText());
        currentBudget.setText(setBudgetField.getText());
    }
    public void backtoHome(ActionEvent actionEvent){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
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
