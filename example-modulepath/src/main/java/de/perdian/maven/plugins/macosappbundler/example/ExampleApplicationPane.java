package de.perdian.maven.plugins.macosappbundler.example;

import java.util.Map;

import javafx.application.Application.Parameters;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

public class ExampleApplicationPane extends BorderPane {

    ExampleApplicationPane(Parameters parameters) {

        TextArea mainTextArea = new TextArea();
        mainTextArea.setText(ExampleApplicationPane.createText(parameters));
        mainTextArea.setFont(Font.font("Monaco", 12f));

        this.setPadding(new Insets(8, 8, 8, 8));
        this.setCenter(mainTextArea);

    }

    private static String createText(Parameters parameters) {
        StringBuilder result = new StringBuilder();

        result.append("Parameters\n");
        result.append("----------\n");
        parameters.getRaw().forEach(parameter -> result.append(" - ").append(parameter.strip()).append("\n"));

        result.append("\n");
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
