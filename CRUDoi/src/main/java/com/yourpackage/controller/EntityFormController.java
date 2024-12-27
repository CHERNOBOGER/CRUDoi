package com.yourpackage.controller;

import com.yourpackage.model.Entity;
import com.yourpackage.dao.EntityDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EntityFormController {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    private Entity entity;
    private boolean isEditMode = true;
    private EntityDAO entityDAO;
    private Stage dialogStage;

    public void setEntityDAO(EntityDAO entityDAO) {
        this.entityDAO = entityDAO;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity.getName() != null) {
            nameField.setText(entity.getName());
            descriptionField.setText(entity.getDescription());
            isEditMode = true;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void onSave() {
        if (entity.getName() == null) {
            entity = new Entity();
            isEditMode = false;
        }
        String name = nameField.getText();
        String description = descriptionField.getText();

        if (name == null || name.trim().isEmpty()) {
            showAlert("Ошибка", "Поле 'Название' не может быть пустым.");
            return;
        }
        entity.setName(name);
        entity.setDescription(description);
        if (isEditMode) {
            entityDAO.update(entity);
        } else {
            entityDAO.create(entity);
        }
        onCancel();
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }
}