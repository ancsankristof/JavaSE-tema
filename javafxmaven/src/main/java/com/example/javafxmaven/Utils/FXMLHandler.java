package com.example.javafxmaven.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.IOException;

public class FXMLHandler {

    public static Pair<Stage, FXMLLoader> loadFXML(String fxmlfile, Class<?> cls,String title){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(cls.getResource(fxmlfile));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(root1));

            return new Pair<Stage, FXMLLoader>(stage, fxmlLoader);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}
