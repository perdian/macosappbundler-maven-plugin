package de.perdian.maven.plugins.macosappbundler.example;

import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class ExampleApplicationPane extends BorderPane {

    ExampleApplicationPane() {

        TextArea mainTextArea = new TextArea();
        mainTextArea.setText(ExampleApplicationPane.createText());
        mainTextArea.setFont(Font.font("Monaco", 12f));

        this.setPadding(new Insets(8, 8, 8, 8));
        this.setCenter(mainTextArea);

    }

    private static String createText() {
        StringBuilder result = new StringBuilder();

        result.append("System Properties\n");
        result.append("-----------------\n");
        System.getProperties().entrySet().stream()
            .map(entry -> Map.entry((String)entry.getKey(), (String)entry.getValue()))
            .sorted((e1, e2) -> String.CASE_INSENSITIVE_ORDER.compare(e1.getKey(), e2.getKey()))
            .forEach(entry -> result.append(entry.getKey()).append(" = ").append(entry.getValue().strip()).append("\n"));

        result.append("\n");
        result.append("System Environment\n");
        result.append("-----------------\n");
        System.getenv().entrySet().stream()
            .sorted((e1, e2) -> String.CASE_INSENSITIVE_ORDER.compare(e1.getKey(), e2.getKey()))
            .forEach(entry -> result.append(entry.getKey()).append(" = ").append(entry.getValue().strip()).append("\n"));

        return result.toString();
    }

}
