package com.yourpackage.controller;

import com.yourpackage.dao.EntityDAO;
import com.yourpackage.dao.EntityDAOImpl;
import com.yourpackage.model.Entity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML
    private TableView<Entity> entityTable;

    @FXML
    private TableColumn<Entity, Integer> idColumn;

    @FXML
    private TableColumn<Entity, String> nameColumn;

    @FXML
    private TableColumn<Entity, String> descriptionColumn;

    @FXML
    private TableColumn<Entity, LocalDateTime> createdAtColumn;

    @FXML
    private TableColumn<Entity, LocalDateTime> updatedAtColumn;

    @FXML
    private TextField searchField;

    private EntityDAO entityDAO;
    private FilteredList<Entity> filteredData;
    private ObservableList<Entity> data;

    public MainController() {
        entityDAO = new EntityDAOImpl();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        setupDateTimeColumn(createdAtColumn, "createdAt");
        setupDateTimeColumn(updatedAtColumn, "updatedAt");
        loadEntities();
        setupSearch();
//        entityTable.setSortPolicy(param -> {
//            // Сортировка по умолчанию
//            return true;
//        });
        entityTable.setSortPolicy(tableView -> {
            if (data == null || data.isEmpty()) {
                return true; // Если данных нет, сортировка не требуется
            }

            Comparator<Entity> comparator = tableView.getComparator();
            if (comparator != null) {
                FXCollections.sort(data, comparator); // Сортируем данные
            }
            return true;
        });

        idColumn.setSortable(true);
        nameColumn.setSortable(true);
        descriptionColumn.setSortable(true);
        createdAtColumn.setSortable(true);
        updatedAtColumn.setSortable(true);

    }



    private void setupDateTimeColumn(TableColumn<Entity, LocalDateTime> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TableCell<Entity, LocalDateTime>() {
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                }
            }
        });
    }

    private void loadEntities() {
        List<Entity> entities = entityDAO.readAll();
        data = FXCollections.observableArrayList(entities);
        filteredData = new FilteredList<>(data, p -> true);
        entityTable.setItems(filteredData);
    }


    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(entity -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                String entityName = entity.getName().toLowerCase();
                String entityDescription = entity.getDescription().toLowerCase();


                if (entityName.contains(lowerCaseFilter)||entityDescription.contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    @FXML
    private void onCreate() {
        Entity newEntity = new Entity();
        openEntityForm(newEntity);
        loadEntities();
    }

    @FXML
    private void onUpdate() {
        Entity selectedEntity = entityTable.getSelectionModel().getSelectedItem();
        if (selectedEntity != null) {
            openEntityForm(selectedEntity);
            loadEntities();
        } else {
            showAlert("Ошибка", "Сущность не выбрана!");
        }
    }

    @FXML
    private void onDelete() {
        Entity selectedEntity = entityTable.getSelectionModel().getSelectedItem();
        if (selectedEntity != null) {
            entityDAO.delete(selectedEntity.getId());
            loadEntities();
        } else {
            showAlert("Ошибка", "Сущность не выбрана!");
        }
    }

    @FXML
    private void onRefresh() {
        refreshTable();
    }

    private void refreshTable() {
        List<Entity> entities = entityDAO.readAll();
        data.setAll(entities);
    }

    private void openEntityForm(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EntityForm.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(entity == null ? "Создать сущность" : "Редактировать сущность");
            dialogStage.setScene(new Scene(loader.load()));

            EntityFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);
            controller.setEntityDAO(entityDAO);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть форму.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}