package com.example.javafxmaven;

import com.example.javafxmaven.Utils.DatabaseHandler;
import com.example.javafxmaven.Utils.FXMLHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Pair;
public class WelcomeScreen extends Application {

    @Override
    public void start(Stage stage){
        DatabaseHandler.createDatabaseTable();
        Pair<Stage, FXMLLoader> p = FXMLHandler.loadFXML("fxml/welcomescreen.fxml",getClass(),"Welcome");
        stage = p.getKey();
        stage.show();


    }

    public static void main(String[] args){
        launch();

    }


}