package com.example.javafxmaven;

import com.example.javafxmaven.Utils.DatabaseHandler;
import com.example.javafxmaven.Utils.FXMLHandler;
import com.example.javafxmaven.Utils.PasswordHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

public class RegisterPanelController {
    @FXML
    private Label welcomeText;
    @FXML
    private Pane registerpanel;
    @FXML
    private TextField inputUsername;
    @FXML
    private TextField inputFullName;
    @FXML
    private TextField inputPhoneNumber;
    @FXML
    private TextField inputAddress;
    @FXML
    private PasswordField inputPassword;
    @FXML
    private PasswordField inputPasswordConfirm;

    @FXML
    private ChoiceBox worktypeSelect;
    ObservableList<String> st = FXCollections.observableArrayList("Alkalmazott","Vendeg");

    @FXML
    private void initialize()  {
        worktypeSelect.setItems(st);
    }
    @FXML
    protected void registerButtonClick() {
        if (!Objects.equals(inputUsername.getText(), "") && !Objects.equals(inputPassword.getText(), "") && Objects.equals(inputPassword.getText(), inputPasswordConfirm.getText())){

            welcomeText.setText("Sikeres regisztráció: " +inputUsername.getText());


            try{
                String tablename = "LoginInfo";

                String password = inputPassword.getText();

                password = PasswordHandler.createSHAHash(password);

                Connection c = DatabaseHandler.connectToDatabase();
                DatabaseHandler.registerUser(c,inputUsername.getText(),password,inputFullName.getText(),inputPhoneNumber.getText(),inputAddress.getText(),worktypeSelect.getValue().toString());
                RegisterSuccess();
                c.close();
            }
         catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        }
        else{
            welcomeText.setText("Egyik mező sem lehet üres!");
        }

    }
    public void RegisterSuccess() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/loginpanel.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Login Panel");
        stage.setScene(new Scene(root1));
        stage.show();

        closeStage();
    }

    public void closeStage() {
        Stage stagelogin = (Stage) registerpanel.getScene().getWindow();
        stagelogin.close();
    }

    public void closeButton() {
        Stage stage= (Stage) registerpanel.getScene().getWindow();
        stage.close();

        Pair<Stage, FXMLLoader> p = FXMLHandler.loadFXML("fxml/welcomescreen.fxml",getClass(),"Welcome");
        stage = p.getKey();
        stage.show();
    }
}