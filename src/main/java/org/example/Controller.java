package org.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public final Stage primaryStage;
    @FXML
    public Pane base_square;

    @FXML
    public Rectangle game_panel;

    @FXML
    public Label label1;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
//        System.out.println(game_panel);
        label1.setText("Player O's turn");
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public Controller() {
        primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(this);
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
        Pane root;
        try {
            root = fxmlLoader.load();
            primaryStage.setTitle("Tic Tac Toe");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
