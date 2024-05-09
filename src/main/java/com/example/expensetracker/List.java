package com.example.expensetracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class List implements Initializable {
    String userName = HelloController.userName;
    Stage stage = HelloController.stage;
    @FXML
    ListView<Expense> myListView;
    ArrayList<Expense> myList;
    public void set(){
        myList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = cal.getTime();
        Date today = new Date();
        try (FileInputStream fileIn = new FileInputStream(userName+".txt");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            while (true) {
                try {
                    Expense expense = (Expense) in.readObject();
                    Date expenseDate = expense.getDate();
                    if (!expenseDate.before(firstDayOfMonth) && !expenseDate.after(today)) {
                        myList.add(expense);
                    }
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void backToHome(ActionEvent actionEvent){
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
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        set();
        myListView.getItems().addAll(myList);
    }
}
