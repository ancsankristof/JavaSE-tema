package com.example.javafxmaven;

import com.example.javafxmaven.Utils.DatabaseHandler;
import com.example.javafxmaven.Utils.FXMLHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.*;
import java.util.Objects;



public class LoginPanelController {
    @FXML
    private Label welcomeText;

    @FXML
    private Pane loginpanel;

    @FXML
    private TextField inputUsername;

    @FXML
    private PasswordField inputPassword;

    @FXML
    protected void loginButtonClick(){

        if (!Objects.equals(inputUsername.getText(), "") && !Objects.equals(inputPassword.getText(), "")){

            welcomeText.setText("Login Successful: Usr: " +inputUsername.getText() + " ,pass: "+inputPassword.getText());

            try{
                Connection c = DatabaseHandler.connectToDatabase();

                String tablename = "LoginInfo";
                Boolean loggedin = DatabaseHandler.checkLogin(c,inputUsername.getText(),inputPassword.getText());


                if (Boolean.TRUE.equals(loggedin)){
                    try{

                        ResultSet loggedinresult  = DatabaseHandler.getUserInfo(c,inputUsername.getText());
                        String loginname = loggedinresult.getString("Name");
                        String fullname = loggedinresult.getString("FullName");
                        String emptype = loggedinresult.getString("EmpType");

                        LoginSuccess(loginname,fullname,emptype);

                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    welcomeText.setText("Rossz felhasználónév/jelszó!");
                }

                c.close();

            }
         catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

            inputUsername.clear();
            inputPassword.clear();

            inputUsername.setPromptText("Felhasználónév");
            inputPassword.setPromptText("Jelszó");
        }
        else{
            welcomeText.setText("Hiba: felhasználónév/jelszó üres");
        }


    }



    public void LoginSuccess(String username,String fullname,String emptype){

        VendegLoginController.currentuser = username;

        if (emptype.equals("Alkalmazott")){
            Pair<Stage, FXMLLoader> p =  FXMLHandler.loadFXML("fxml/alkalmazottloggedin.fxml",getClass(),"Alkalmazott");



            Stage stage = p.getKey();
            FXMLLoader fxmlLoader = p.getValue();
            stage.show();

            AlkalmazottLoginController Afterlogin = fxmlLoader.getController();
            Afterlogin.welcomeText.setText("Üdv: "+ fullname + "!");
            Afterlogin.usernameLabel.setText(username);
            Afterlogin.EmpTypeLabel.setText(emptype);
            Stage stagelogin = (Stage) loginpanel.getScene().getWindow();
            stagelogin.close();
        }
        else{
            Pair<Stage, FXMLLoader> p =  FXMLHandler.loadFXML("fxml/vendegloggedin.fxml",getClass(),"Vendeg");




            Stage stage = p.getKey();
            FXMLLoader fxmlLoader = p.getValue();

            VendegLoginController vendeglogin = fxmlLoader.getController();
            vendeglogin.welcomeText.setText("Üdv: "+ fullname + "!");
            vendeglogin.usernameLabel.setText(username);
            vendeglogin.EmpTypeLabel.setText(emptype);

            stage.show();
            Stage stagelogin = (Stage) loginpanel.getScene().getWindow();
            stagelogin.close();
        }

    }


    public void closeButton() {
        Stage stage = (Stage) loginpanel.getScene().getWindow();
        stage.close();

        Pair<Stage, FXMLLoader> p = FXMLHandler.loadFXML("fxml/welcomescreen.fxml",getClass(),"Welcome");
        stage = p.getKey();
        stage.show();
    }
}