package com.example.java2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;

    @FXML
    public void checkButtonClick(ActionEvent actionEvent) {
        final String writerText = messageField.getText();
        if (writerText.isEmpty()){
            return;
        }
        messageArea.appendText(writerText);
    }
}