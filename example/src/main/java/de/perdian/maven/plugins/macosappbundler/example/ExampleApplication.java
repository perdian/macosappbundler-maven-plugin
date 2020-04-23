package de.perdian.maven.plugins.macosappbundler.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExampleApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene primaryScene = new Scene(new ExampleApplicationPane());
        primaryScene.getStylesheets().add("META-INF/example.css");

        primaryStage.setTitle("Example application");
        primaryStage.setScene(primaryScene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.show();
        primaryStage.centerOnScreen();

    }

}
