package com.example.pixelsorter2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;

public class InputBox {

    public static Double ThresholdInput(){

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Threshold");
        window.setMinWidth(250);

        //icon
        URL url = InputBox.class.getClassLoader().getResource("icon.png");
        assert url != null;
        window.getIcons().add(new Image(url.toExternalForm()));


        Label label = new Label("Enter threshold between 0.1 and 0.9");
        label.setTextFill(Color.WHITE);//set font color to white

        //create input box
        TextField textBox = new TextField("0.");
        textBox.setMaxWidth(50); // Set the preferred width of the text field

        //window closes when pressed ENTER
        textBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                window.close();
            }
        });

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(10)); // Set the padding for the VBox
        layout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.getChildren().addAll(label, textBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return Double.valueOf(textBox.getText());
    }

}
