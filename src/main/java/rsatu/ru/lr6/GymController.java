package rsatu.ru.lr6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

public class GymController {

    // Репозитории (Модель данных)
    private final AssetRepository<Employee> employeeRepo = new AssetRepository<>();
    private final AssetRepository<Equipment> equipmentRepo = new AssetRepository<>();

    // Флаг состояния фильтра для оборудования
    private boolean isFilterActive = false;

    // --- UI Элементы (связаны с FXML) ---

    @FXML private Label lblEmpCount;
    @FXML private Label lblEqCount;
    @FXML private Label lblTotalValue;

    // Таблица сотрудников
    @FXML private TableView<Employee> tableEmployees;
    @FXML private TableColumn<Employee, String> colEmpInv;
    @FXML private TableColumn<Employee, String> colEmpName;
    @FXML private TableColumn<Employee, String> colEmpPos;
    @FXML private TableColumn<Employee, Double> colEmpSal;

    // Таблица оборудования
    @FXML private TableView<Equipment> tableEquipment;
    @FXML private TableColumn<Equipment, String> colEqInv;
    @FXML private TableColumn<Equipment, String> colEqModel;
    @FXML private TableColumn<Equipment, String> colEqManuf;
    @FXML private TableColumn<Equipment, Double> colEqPrice;
    @FXML private TableColumn<Equipment, String> colEqStatus;

    /**
     * Инициализация при загрузке приложения
     */
    @FXML
    public void initialize() {
        setupTables();
        fillWithTestData();
        updateUI();
    }

    /**
     * Настройка привязки данных к колонкам таблиц
     */
    private void setupTables() {
        // Сотрудники
        colEmpInv.setCellValueFactory(new PropertyValueFactory<>("inventoryNumber"));
        colEmpName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmpPos.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmpSal.setCellValueFactory(new PropertyValueFactory<>("salary"));

        // Оборудование
        colEqInv.setCellValueFactory(new PropertyValueFactory<>("inventoryNumber"));
        colEqModel.setCellValueFactory(new PropertyValueFactory<>("modelName"));
        colEqManuf.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        colEqPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Статус: динамическое преобразование boolean в строку
        colEqStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusText())
        );
    }

    /**
     * ГЛАВНЫЙ МЕТОД ОБНОВЛЕНИЯ ИНТЕРФЕЙСА
     * Использует сброс списка (null) и refresh() для принудительной полной перерисовки.
     */
    private void updateUI() {
        // 1. Обновление таблицы сотрудников
        tableEmployees.setItems(null);
        tableEmployees.setItems(FXCollections.observableArrayList(employeeRepo.getAll()));
        tableEmployees.refresh();

        // 2. Обновление таблицы оборудования (с учетом фильтра)
        List<Equipment> dataToShow;
        if (isFilterActive) {
            dataToShow = equipmentRepo.getNonOperational();
        } else {
            dataToShow = equipmentRepo.getAll();
        }

        // СБРОС И УСТАНОВКА НОВОГО СПИСКА
        tableEquipment.setItems(null);
        tableEquipment.setItems(FXCollections.observableArrayList(dataToShow));

        // ВАЖНО: Принудительная перерисовка всех ячеек
        // Это решает проблему "наложения" текста и не обновления первой строки
        tableEquipment.refresh();

        // 3. Обновление статистики
        lblEmpCount.setText("Сотрудников: " + employeeRepo.size());
        lblEqCount.setText("Оборудования: " + equipmentRepo.size());

        double totalVal = employeeRepo.getTotalValue() + equipmentRepo.getTotalValue();
        lblTotalValue.setText(String.format("Общая стоимость: %.2f руб.", totalVal));
    }

    // ================= СОТРУДНИКИ =================

    @FXML
    private void handleAddEmployee() {
        Dialog<Employee> dialog = createEmployeeDialog();
        Optional<Employee> result = dialog.showAndWait();
        result.ifPresent(emp -> {
            employeeRepo.add(emp);
            updateUI();
        });
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selected = tableEmployees.getSelectionModel().getSelectedItem();
        if (selected != null) {
            List<Employee> all = employeeRepo.getAll();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getInventoryNumber().equals(selected.getInventoryNumber())) {
                    employeeRepo.remove(i);
                    break;
                }
            }
            updateUI();
        } else {
            showAlert("Внимание", "Выберите сотрудника для удаления");
        }
    }

    // ================= ОБОРУДОВАНИЕ =================

    @FXML
    private void handleAddEquipment() {
        Dialog<Equipment> dialog = createEquipmentDialog();
        Optional<Equipment> result = dialog.showAndWait();
        result.ifPresent(eq -> {
            equipmentRepo.add(eq);
            updateUI();
        });
    }

    @FXML
    private void handleDeleteEquipment() {
        Equipment selected = tableEquipment.getSelectionModel().getSelectedItem();
        if (selected != null) {
            List<Equipment> all = equipmentRepo.getAll();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getInventoryNumber().equals(selected.getInventoryNumber())) {
                    equipmentRepo.remove(i);
                    break;
                }
            }
            updateUI();
        } else {
            showAlert("Внимание", "Выберите оборудование для удаления");
        }
    }

    @FXML
    private void handleChangeStatus() {
        Equipment selected = tableEquipment.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Меняем данные в модели
            selected.setOperational(!selected.isOperational());

            // Принудительно перерисовываем весь интерфейс
            updateUI();

            // Если после обновления элемент исчез из виду (из-за фильтра), снимаем выделение
            if (isFilterActive && selected.isOperational()) {
                tableEquipment.getSelectionModel().clearSelection();
            }
        } else {
            showAlert("Внимание", "Выберите оборудование для изменения статуса");
        }
    }

    @FXML
    private void handleShowBroken() {
        isFilterActive = true;
        updateUI();
    }

    @FXML
    private void handleShowAllEq() {
        isFilterActive = false;
        updateUI();
    }

    // ================= ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =================

    private void fillWithTestData() {
        employeeRepo.add(new Employee("EMP001", "Иванов Иван Иванович", "Администратор", 50000));
        employeeRepo.add(new Employee("EMP002", "Петров Петр Петрович", "Тренер", 60000));
        employeeRepo.add(new Employee("EMP003", "Сидорова Анна Сергеевна", "Тренер", 65000));
        employeeRepo.add(new Employee("EMP004", "Кузнецов Дмитрий Андреевич", "Инженер", 70000));

        equipmentRepo.add(new Equipment("EQ001", "Беговая дорожка Pro Runner X9", "TechnoGym", 450000));
        equipmentRepo.add(new Equipment("EQ002", "Велотренажер Spin Bike 3000", "LifeFitness", 320000));
        equipmentRepo.add(new Equipment("EQ003", "Многофункциональная рама Power Rack", "Hammer Strength", 680000));
        equipmentRepo.add(new Equipment("EQ004", "Скамья для жима лежа", "Rogue", 120000));
        equipmentRepo.add(new Equipment("EQ005", "Гантельный ряд 2-50 кг", "IronKing", 250000));

        // Одно устройство сразу неисправно для демонстрации
        equipmentRepo.get(2).setOperational(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Создание диалога для сотрудника
    private Dialog<Employee> createEmployeeDialog() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Добавить сотрудника");
        dialog.setHeaderText("Введите данные сотрудника");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField invField = new TextField();
        TextField nameField = new TextField();
        TextField posField = new TextField();
        TextField salaryField = new TextField();

        grid.add(new Label("Инв. номер:"), 0, 0);
        grid.add(invField, 1, 0);
        grid.add(new Label("ФИО:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Должность:"), 0, 2);
        grid.add(posField, 1, 2);
        grid.add(new Label("Зарплата:"), 0, 3);
        grid.add(salaryField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Employee(
                            invField.getText(),
                            nameField.getText(),
                            posField.getText(),
                            Double.parseDouble(salaryField.getText())
                    );
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Зарплата должна быть числом");
                    return null;
                }
            }
            return null;
        });
        return dialog;
    }

    // Создание диалога для оборудования
    private Dialog<Equipment> createEquipmentDialog() {
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Добавить оборудование");
        dialog.setHeaderText("Введите данные оборудования");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField invField = new TextField();
        TextField modelField = new TextField();
        TextField manufField = new TextField();
        TextField priceField = new TextField();

        grid.add(new Label("Инв. номер:"), 0, 0);
        grid.add(invField, 1, 0);
        grid.add(new Label("Модель:"), 0, 1);
        grid.add(modelField, 1, 1);
        grid.add(new Label("Производитель:"), 0, 2);
        grid.add(manufField, 1, 2);
        grid.add(new Label("Цена:"), 0, 3);
        grid.add(priceField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Equipment(
                            invField.getText(),
                            modelField.getText(),
                            manufField.getText(),
                            Double.parseDouble(priceField.getText())
                    );
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Цена должна быть числом");
                    return null;
                }
            }
            return null;
        });
        return dialog;
    }
}