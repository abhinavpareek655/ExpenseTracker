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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CategorizedExpensesButton implements Initializable {
    String userName = HelloController.userName;
    Stage stage = HelloController.stage;
    int totalEntertainment = 0;
    int totalEducation = 0;
    int totalFood = 0;
    int totalHealth = 0;
    int totalTravel = 0;
    @FXML
    BarChart<?,?> barChart;
    @FXML
    CategoryAxis x;
    @FXML
    NumberAxis y;

    @FXML
     Label entertainment;
    @FXML
     Label education;
    @FXML
     Label food;
    @FXML
     Label health;
    @FXML
     Label travel;
    @FXML
    ListView<Expense> entertainmentList;
    @FXML
    ListView<Expense> educationList;
    @FXML
    ListView<Expense> foodList;
    @FXML
    ListView<Expense> healthList;
    @FXML
    ListView<Expense> travelList;

     ArrayList<Expense> entertainmentData;
     ArrayList<Expense> educationData;
     ArrayList<Expense> foodData;
     ArrayList<Expense> healthData;
     ArrayList<Expense> travelData;
    public void set(){
        entertainmentData = new ArrayList<>();
        educationData = new ArrayList<>();
        foodData = new ArrayList<>();
        travelData = new ArrayList<>();
        healthData = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(userName+".txt");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            while (true) {
                try {
                    Expense expense = (Expense) in.readObject();
                    switch (expense.getCategory()){
                        case "entertainment":
                            entertainmentData.add(expense);
                            break;
                        case "education":
                            educationData.add(expense);
                            break;
                        case "food":
                            foodData.add(expense);
                            break;
                        case "travel":
                            travelData.add(expense);
                            break;
                        case "health":
                            healthData.add(expense);
                            break;
                        default:
                            break;
                    }
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            entertainmentList.getItems().addAll(entertainmentData);
            educationList.getItems().addAll(educationData);
            foodList.getItems().addAll(foodData);
            healthList.getItems().addAll(healthData);
            travelList.getItems().addAll(travelData);
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        set();
        setTotal();
        barChart.setTitle("Categorized Expenses");
        XYChart.Series set1 = new XYChart.Series<>();
        set1.getData().add(new XYChart.Data("Entertainment", totalEntertainment));
        set1.getData().add(new XYChart.Data("Education", totalEducation));
        set1.getData().add(new XYChart.Data("Food", totalFood));
        set1.getData().add(new XYChart.Data("Health", totalHealth));
        set1.getData().add(new XYChart.Data("Travel", totalTravel));

        barChart.getData().add(set1);
    }

    public void backToHome(ActionEvent actionEvent){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
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
    public void setTotal(){
        for(Expense expense: entertainmentData){
            totalEntertainment += expense.getAmount();
        }
        for(Expense expense: educationData){
            totalEducation += expense.getAmount();
        }
        for(Expense expense: foodData){
            totalFood += expense.getAmount();
        }
        for(Expense expense: healthData){
            totalHealth += expense.getAmount();
        }
        for(Expense expense: travelData){
            totalTravel += expense.getAmount();
        }
        entertainment.setText("Total: "+totalEntertainment);
        education.setText("Total: "+totalEducation);
        food.setText("Total: "+totalFood);
        health.setText("Total: "+totalHealth);
        travel.setText("Total: "+totalTravel);
    }
}
