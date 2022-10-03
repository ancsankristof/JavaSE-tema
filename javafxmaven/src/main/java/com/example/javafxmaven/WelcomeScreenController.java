package com.example.javafxmaven;

import com.example.javafxmaven.Utils.FXMLHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class WelcomeScreenController {


    @FXML
    public Pane welcomepanel;


    @FXML
    protected void loginButtonClick() {
        Pair<Stage, FXMLLoader> p = FXMLHandler.loadFXML("fxml/loginpanel.fxml",getClass(),"Login");
        Stage stage = p.getKey();
        stage.show();

        Stage stagewelcome = (Stage) welcomepanel.getScene().getWindow();
        stagewelcome.close();
    }
    @FXML
    protected void registerButtonClick() {
        Pair<Stage, FXMLLoader> p = FXMLHandler.loadFXML("fxml/registerpanel.fxml",getClass(),"Register");
        Stage stage = p.getKey();
        stage.show();
    }


    public void closeButton() {
        Stage stage = (Stage) welcomepanel.getScene().getWindow();
        stage.close();
    }
}