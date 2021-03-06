package inventoryManager;
/***
 * Copyright (C) 2015  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import inventoryManager.formatters.ItemLog;
import inventoryManager.formatters.PasswordLog;
import inventoryManager.formatters.Person;
import inventoryManager.formatters.ReturnItem;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static inventoryManager.WorkingUser.addItemToDatabase;

public class AdminInterface extends Interface {
    /**
     * Create an interface instance with it's parameters set by the config file
     *
     * @throws IOException
     */
    private AdminInterface() throws IOException {
        super();
    }

    public static void enterAdminMode(Stage lastStage, int privelage) {
        lastStage.hide();
        Stage adminStage = new Stage();
        adminStage.setTitle("Inventory Admin");
        SplitPane split = new SplitPane();
        VBox rightPane = new VBox();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));
        ListView<String> optionList = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        final String[] PersonSettingsList = {"Change a Person", "Save Person Database"};
        final String[] itemSettingsList = {"Return Items", "Add Items", "Remove General Items", "Change a General Item", "Enter General Item Counts", "Save Item Database", "Create Set", "Add Controlled Type"};
        final String[] AdminSettingsList = {"Change Password", "Save Databases To USB", "Close The Program", "Delete Controlled Items"};
        final String[] LogSettingsList = {"Item Logs", "Password Logs"};
        final String[] RootSettingsList = {"Create Admins"};
        items.setAll(PersonSettingsList);
        optionList.setItems(items);
        optionList.maxWidthProperty().bind(split.widthProperty().multiply(0.2));

        grid.add(optionList, 0, 0, 1, 7);
        Button logs = new Button("Logs");
        logs.setOnAction((ActionEvent e) -> {
            items.setAll(LogSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button people = new Button("People");
        people.setOnAction((ActionEvent e) -> {
            items.setAll(PersonSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button products = new Button("Items");
        products.setOnAction((ActionEvent e) -> {
            items.setAll(itemSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button admin = new Button("Admin");
        admin.setOnAction((ActionEvent e) -> {
            items.setAll(AdminSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });
        Button root = new Button("Staff");
        root.setOnAction((ActionEvent e) -> {
            items.setAll(RootSettingsList);
            optionList.setItems(items);
            optionList.getSelectionModel().select(0);
        });

        Button logout = new Button("Logout");
        logout.setOnAction((ActionEvent e) -> {
            adminStage.close();
            lastStage.show();
        });

        ToolBar buttonBar;
        if (privelage == PersonDatabase.ROOT) {
            buttonBar = new ToolBar(people, products, logs, admin, root, logout);
        } else {
            buttonBar = new ToolBar(people, products, logs, admin, logout);
        }
        rightPane.getChildren().addAll(buttonBar, grid);
        split.getItems().addAll(optionList, rightPane);
        split.setDividerPositions(0.2f);
        optionList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String selectedOption) -> {
                    if (selectedOption == null) return;
                    switch (selectedOption) {
                        case "Change a Person":
                            changePerson(grid);
                            break;
                        case "Save Person Database":
                            savePersonDatabase(adminStage, grid);
                            break;
                        case "Return Items":
                            returnItems(grid);
                            break;
                        case "Add Items":
                            addItem(grid);
                            break;
                        case "Remove General Items":
                            removeItem(grid, SQLInterface.TABGENERAL);
                            break;
                        case "Change a General Item":
                            changeItem(grid);
                            break;
                        case "Enter General Item Counts":
                            enterStockCounts(grid);
                            break;
                        case "Save Item Database":
                            saveItemDatabase(adminStage, grid);
                            break;
                        case "Change Password":
                            changeAdminPassword(grid);
                            break;
                        case "Save Databases To USB":
                            SaveDatabases(adminStage, grid);
                            break;
                        case "Close The Program":
                            CloseProgram(grid);
                            break;
                        case "Create Admins":
                            createAdmins(grid);
                            break;
                        case "Delete Controlled Items":
                            removeItem(grid, SQLInterface.TABCONTROLLED);
                            break;
                        case "Item Logs":
                            showItemLog(grid);
                            break;
                        case "Password Logs":
                            showPasswordLog(grid);
                            break;
                        case "Create Set":
                            createSet(grid);
                            break;
                        case "Add Controlled Type":
                            createControlledType(grid);
                            break;
                        default:
                            changePerson(grid);

                    }
                });
        Scene adminScene = new Scene(split, horizontalSize, verticalSize);
        adminStage.setScene(adminScene);
        adminStage.setOnCloseRequest((WindowEvent event) -> lastStage.show());
        adminStage.show();
        adminStage.toFront();

    }


    private static void changePerson(GridPane grid) {
        grid.getChildren().clear();

        TableView<Person> personList = new TableView<>();
        ObservableList<Person> person = FXCollections.observableArrayList();
        person.setAll(WorkingUser.getUserDetails());

        TableColumn IDCol = new TableColumn("ID");
        TableColumn nameCol = new TableColumn("name");
        personList.getColumns().addAll(IDCol, nameCol);
        IDCol.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("ID")
        );
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("name")
        );
        personList.setEditable(true);
        IDCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> t) {
                        WorkingUser.changeDatabasePerson(personList.getSelectionModel().getSelectedItem().getName(),
                                t.getNewValue(), t.getOldValue());
                    }
                }
        );
        nameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Person, String> t) {
                        WorkingUser.changeDatabasePerson(t.getNewValue(),
                                personList.getSelectionModel().getSelectedItem().getID(),
                                personList.getSelectionModel().getSelectedItem().getID());
                    }
                }
        );

        personList.setItems(person);
        grid.add(personList, 0, 0, 1, 4);

        //TODO: Edited cells will be saved, however, cells still cannot be edited.


    }

    private static void savePersonDatabase(Stage adminStage, GridPane grid) {
        DirectoryChooser fc = new DirectoryChooser();


        grid.getChildren().clear();
        Text fileLabel = new Text("Save Directory");
        TextField filePath = new TextField("");
        filePath.setEditable(true);
        Button saveDirBtn = new Button("Choose Save Directory");
        Button saveBtn = new Button("Save Database to Selected Directory");

        grid.add(saveBtn, 1, 5);
        grid.add(fileLabel, 0, 0);
        grid.add(filePath, 0, 1);
        grid.add(saveDirBtn, 1, 1);

        saveDirBtn.setOnAction((ActionEvent e) -> {
            File returnVal = fc.showDialog(adminStage);

            if (returnVal != null) {
                filePath.setText(returnVal.getPath());
                flashColour(1500, Color.AQUAMARINE, saveDirBtn);
            } else {
                flashColour(1500, Color.RED, saveDirBtn);
            }
        });

        saveBtn.setOnAction((ActionEvent e) -> {

                if (filePath.getText() != null || !filePath.getText().isEmpty()) {
                    WorkingUser.adminWriteOutDatabase(SQLInterface.TABPERSON, Compatibility.getFilePath(filePath.getText() + "/adminPersonDatabase.csv"));

                }
        });
    }

    private static void returnItems(GridPane grid) { 
        grid.getChildren().clear();

        TableView<inventoryManager.formatters.ReturnItem> outList = new TableView<>();
        ObservableList<ReturnItem> retlist = FXCollections.observableArrayList();
        retlist.setAll(WorkingUser.getOutItems());

        TableColumn itemIDCol = new TableColumn("Item ID");
        TableColumn nameCol = new TableColumn("Name");
        TableColumn UIDCol = new TableColumn("User ID");
        TableColumn dateCol = new TableColumn("Date");
        TableColumn IDCol = new TableColumn("ID");
        outList.getColumns().addAll(itemIDCol, nameCol, UIDCol, dateCol, IDCol);
        itemIDCol.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("itemID")
        );
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("name")
        );
        UIDCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("userID")
        );
        dateCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("date")
        );
        IDCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("ID")
        );
        outList.setEditable(false);
        outList.setItems(retlist);

        SplitPane inOut = new SplitPane();

        TableView<inventoryManager.formatters.ReturnItem> inItems = new TableView<>();
        ObservableList<ReturnItem> barcodesIn = FXCollections.observableArrayList();
        barcodesIn.setAll(WorkingUser.getReturningItems());

        TableColumn itemIDColIn = new TableColumn("Item ID");
        TableColumn nameColIn = new TableColumn("Name");
        TableColumn UIDColIn = new TableColumn("User ID");
        TableColumn dateColIn = new TableColumn("Date");
        TableColumn IDColIn = new TableColumn("ID");
        inItems.getColumns().addAll(itemIDColIn, nameColIn, UIDColIn, dateColIn, IDColIn);
        itemIDColIn.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("itemID")
        );
        nameColIn.setCellValueFactory(
                new PropertyValueFactory<Person, String>("name")
        );
        UIDColIn.setCellValueFactory(
                new PropertyValueFactory<Person, String>("userID")
        );
        dateColIn.setCellValueFactory(
                new PropertyValueFactory<Person, String>("date")
        );
        IDColIn.setCellValueFactory(
                new PropertyValueFactory<Person, String>("ID")
        );
        inItems.setEditable(false);
        inItems.setItems(barcodesIn);


        inOut.getItems().addAll(outList, inItems);
        inOut.setDividerPositions(0.5f);
        grid.add(inOut, 0, 1, 2, 1);

        TextField barcodeEntry = new TextField();
        barcodeEntry.setPromptText("Barcode");
        barcodeEntry.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                if(barcodeEntry.getText().isEmpty()) {
                    retlist.setAll(WorkingUser.getOutItems());
                }
                else {
                    retlist.setAll(WorkingUser.getOutItems(barcodeEntry.getText()));
                }
            }
        });
         grid.add(barcodeEntry, 0, 0);

        Button checkIn = new Button("Check In"); // This button moves items to the other pane
        checkIn.setOnAction((ActionEvent e) -> {
            WorkingUser.addToReturnCheckout(outList.getSelectionModel().getSelectedItem());
            barcodesIn.setAll(WorkingUser.getReturningItems());
            inItems.setItems(barcodesIn);
            retlist.removeAll(outList.getSelectionModel().getSelectedItems());
            outList.setItems(retlist);

        });
        grid.add(checkIn, 1, 2);

        Button signIn = new Button("Sign In Items"); // This button signs items in
        signIn.setOnAction((ActionEvent e) -> {
            WorkingUser.signItemsIn(new ArrayList<>(barcodesIn));
            barcodesIn.setAll(WorkingUser.getReturningItems());
            inItems.setItems(barcodesIn);
            retlist.removeAll(outList.getSelectionModel().getSelectedItems());
            outList.setItems(retlist);
        });
        grid.add(signIn, 2, 2);
    }

    private static void addItem(GridPane grid) {
        grid.getChildren().clear();
        Text nameLabel = new Text("Name:");
        grid.add(nameLabel, 0, 0);
        TextField nameEntry = new TextField();
        nameEntry.requestFocus();
        grid.add(nameEntry, 1, 0);
        Text BarCodeLabel = new Text("Barcode:");
        grid.add(BarCodeLabel, 0, 1);
        TextField BarCodeEntry = new TextField();
        grid.add(BarCodeEntry, 1, 1);
        ChoiceBox<String> set = new ChoiceBox<>();
        ObservableList<String> sets = FXCollections.observableArrayList();
        sets.setAll(WorkingUser.getSets());
        set.setItems(sets);
        grid.add(set, 1, 2);
        CheckBox cb = new CheckBox("Controlled Item?");
        grid.add(cb, 1, 3);

        // General Item information
        Text descriptionLabel = new Text("Item Description:");
        TextField description = new TextField();
        Text quantityLabel = new Text("Quantity:");
        TextField quantity = new TextField();
        Text locationLabel = new Text("Item Location:");
        TextField location = new TextField();

        // Controlled item information.
        Text type = new Text("Type:");
        ChoiceBox<String> typeBox = new ChoiceBox<>();
        Text tagnoLabel = new Text("Tag/Position number:");
        TextField tagno = new TextField();
        Text stateLabel = new Text("Item State:");
        TextField state = new TextField();

        grid.add(descriptionLabel, 0, 4);
        grid.add(description, 1, 4);

        grid.add(quantityLabel, 0, 5);
        grid.add(quantity, 1, 5);

        grid.add(locationLabel, 0, 6);
        grid.add(location, 1, 6);


        cb.setOnAction((ActionEvent e) -> {
            if (!cb.isSelected()) {
                grid.getChildren().removeAll(type, typeBox, tagno, tagnoLabel, stateLabel, state);
                grid.add(descriptionLabel, 0, 4);
                grid.add(description, 1, 4);

                grid.add(quantityLabel, 0, 5);
                grid.add(quantity, 1, 5);

                grid.add(locationLabel, 0, 6);
                grid.add(location, 1, 6);
            } else {
                grid.getChildren().removeAll(description, descriptionLabel, quantity, quantityLabel, location, locationLabel);
                ObservableList<String> types = FXCollections.observableArrayList();
                types.setAll(WorkingUser.getTypes());
                typeBox.setItems(types);
                grid.add(type, 0, 4);
                grid.add(typeBox, 1, 4);

                grid.add(tagnoLabel, 0, 5);
                grid.add(tagno, 1, 5);

                grid.add(stateLabel, 0, 6);
                grid.add(state, 1, 6);
            }
        });

        nameEntry.setOnAction((ActionEvent e) -> BarCodeEntry.requestFocus());

        location.setOnAction((ActionEvent e) -> {
            boolean valid = true;
            try {
                Long.parseLong(quantity.getText());
            } catch (NumberFormatException e1) {
                flashColour(1500, Color.RED, quantity);
                valid = false;
            }
            if (elementsAreNotEmpty(nameEntry, BarCodeEntry, quantity, location) && valid) { //Description may be empty
                boolean suc = addItemToDatabase(nameEntry.getText(), BarCodeEntry.getText(), description.getText(), Long.parseLong(quantity.getText()), location.getText(), set.getValue());
                if(suc) {
                    nameEntry.clear();
                    BarCodeEntry.clear();
                    description.clear();
                    quantity.clear();
                    location.clear();
                    nameEntry.requestFocus();
                    flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry, description, quantity, location);
                }
                else
                    flashColour(1500, Color.RED, nameEntry, BarCodeEntry, typeBox, set, tagno, state);
            }
        });
        state.setOnAction((ActionEvent e) -> {
            if (nameEntry.getText() != null && !nameEntry.getText().isEmpty() && BarCodeEntry.getText() != null && !BarCodeEntry.getText().isEmpty()) {
                boolean suc = WorkingUser.addItemToDatabase(nameEntry.getText(), BarCodeEntry.getText(), typeBox.getValue(), tagno.getText(), set.getValue(), state.getText());
                if(suc) {
                    nameEntry.clear();
                    BarCodeEntry.clear();
                    typeBox.setValue("");
                    tagno.clear();
                    set.setValue("");
                    state.clear();
                    nameEntry.requestFocus();
                    flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry, typeBox, set, tagno, state);
                }
                else
                    flashColour(1500, Color.RED, nameEntry, BarCodeEntry, typeBox, set, tagno, state);
            }
        });
    }


    private static void createControlledType(GridPane grid) {
        grid.getChildren().clear();
        Text nameLabel = new Text("Name:");
        grid.add(nameLabel, 0, 0);
        TextField nameEntry = new TextField();
        nameEntry.requestFocus();
        grid.add(nameEntry, 1, 0);
        Text BarCodeLabel = new Text("Barcode:");
        grid.add(BarCodeLabel, 0, 1);
        TextField BarCodeEntry = new TextField();
        grid.add(BarCodeEntry, 1, 1);

        nameEntry.setOnAction((ActionEvent e) -> BarCodeEntry.requestFocus());
        BarCodeEntry.setOnAction((ActionEvent e) -> {
            if (elementsAreNotEmpty(nameEntry, BarCodeEntry)) {
                WorkingUser.addControlledType(nameEntry.getText(), BarCodeEntry.getText());
                flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry);
                nameEntry.clear();
                BarCodeEntry.clear();
                nameEntry.requestFocus();
            } else flashColour(1500, Color.RED, nameEntry, BarCodeEntry);
        });
    }
    private static void createSet(GridPane grid) {
        grid.getChildren().clear();
        Text nameLabel = new Text("Name:");
        grid.add(nameLabel, 0, 0);
        TextField nameEntry = new TextField();
        nameEntry.requestFocus();
        grid.add(nameEntry, 1, 0);
        Text BarCodeLabel = new Text("Barcode:");
        grid.add(BarCodeLabel, 0, 1);
        TextField BarCodeEntry = new TextField();
        grid.add(BarCodeEntry, 1, 1);

        nameEntry.setOnAction((ActionEvent e) -> BarCodeEntry.requestFocus());
        BarCodeEntry.setOnAction((ActionEvent e) -> {
            if (elementsAreNotEmpty(nameEntry, BarCodeEntry)) {
                WorkingUser.addSet(nameEntry.getText(), BarCodeEntry.getText());
                flashColour(1500, Color.AQUAMARINE, nameEntry, BarCodeEntry);
                nameEntry.clear();
                BarCodeEntry.clear();
                nameEntry.requestFocus();
            } else flashColour(1500, Color.RED, nameEntry, BarCodeEntry);
        });
    }

    private static void removeItem(GridPane grid, String type) {
        grid.getChildren().clear();

        Button remove = new Button("Remove");
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getProductNames(type));
        productList.setItems(product);
        grid.add(productList, 0, 1);
        product.setAll(WorkingUser.getProductNames(type));
        productList.setItems(product);

        remove.setOnAction((ActionEvent e) -> {
            String index = productList.getSelectionModel().getSelectedItem();
            try {
                WorkingUser.removeItem(index);
                flashColour(1500, Color.AQUAMARINE, remove);
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
                flashColour(1500, Color.RED, remove);
            }
            product.setAll(WorkingUser.getProductNames(type));
        });
        grid.add(remove, 1, 0);
        product.setAll(WorkingUser.getProductNames(type));
    }

    private static void changeItem(GridPane grid) {
        grid.getChildren().clear();
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getProductNames(SQLInterface.TABGENERAL));
        productList.setItems(product);
        grid.add(productList, 0, 0, 1, 4);
        Text nameLabel = new Text("Name:");
        grid.add(nameLabel, 1, 0);
        TextField nameEntry = new TextField();
        nameEntry.requestFocus();
        grid.add(nameEntry, 2, 0);
        Text BarCodeLabel = new Text("Barcode:");
        grid.add(BarCodeLabel, 1, 1);
        TextField barCodeEntry = new TextField();
        grid.add(barCodeEntry, 2, 1);
        productList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> vo, String oldVal, String selectedProduct) -> {
                    nameEntry.setText(selectedProduct);
                    String BC = String.valueOf(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
                    barCodeEntry.setText(BC);

                });
        nameEntry.setOnAction((ActionEvent e) -> barCodeEntry.requestFocus());
        barCodeEntry.setOnAction((ActionEvent e) -> {
            if (WorkingUser.itemExists(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()))) {
                WorkingUser.changeDatabaseProduct(nameEntry.getText(), productList.getSelectionModel().getSelectedItem(),
                        barCodeEntry.getText(), WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
                flashColour(1500, Color.AQUAMARINE, barCodeEntry);
            } else flashColour(1500, Color.RED, barCodeEntry);

            nameEntry.clear();
            barCodeEntry.clear();
            nameEntry.requestFocus();
            flashColour(1500, Color.AQUAMARINE, nameEntry, barCodeEntry);

            //Now need to update the form
            String selectedProduct = productList.getSelectionModel().getSelectedItem();

            nameEntry.setText(selectedProduct);
            String BC = String.valueOf(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()));
            barCodeEntry.setText(BC);
            product.setAll(WorkingUser.getProductNames(SQLInterface.TABGENERAL));
            productList.setItems(product);
        });
    }

    private static void enterStockCounts(GridPane grid) {
        grid.getChildren().clear();
        ListView<String> productList = new ListView<>();
        ObservableList<String> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getProductNames(SQLInterface.TABGENERAL));
        productList.setItems(product);
        grid.add(productList, 0, 0, 1, 4);
        Text numberLabel = new Text("Number:");
        grid.add(numberLabel, 1, 0);
        TextField numberEntry = new TextField();
        grid.add(numberEntry, 2, 0);

        productList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> vo, String oldVal, String selectedProduct) -> {
                    String numberOfProduct = Integer.toString(WorkingUser.getProductNumber(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem())));
                    numberEntry.setText(numberOfProduct);
                    numberEntry.requestFocus();

                });
        numberEntry.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                WorkingUser.setNumberOfProducts(WorkingUser.getProductBarCode(productList.getSelectionModel().getSelectedItem()), Integer.parseInt(numberEntry.getText()));
                productList.getSelectionModel().select(productList.getSelectionModel().getSelectedIndex() + 1);
                numberEntry.requestFocus();
                flashColour(1500, Color.AQUAMARINE, numberEntry);
            }
        });
    }

    private static void saveItemDatabase(Stage adminStage, GridPane grid) {
        DirectoryChooser fc = new DirectoryChooser();


        grid.getChildren().clear();
        Text fileLabel = new Text("Save Directory");
        TextField filePath = new TextField("");
        filePath.setEditable(true);
        Button saveDirBtn = new Button("Choose Save Directory");
        Button saveBtn = new Button("Save Database to Selected Directory");

        grid.add(saveBtn, 1, 5);
        grid.add(fileLabel, 0, 0);
        grid.add(filePath, 0, 1);
        grid.add(saveDirBtn, 1, 1);

        saveDirBtn.setOnAction((ActionEvent e) -> {
            File returnVal = fc.showDialog(adminStage);

            if (returnVal != null) {
                filePath.setText(returnVal.getPath());
                flashColour(1500, Color.AQUAMARINE, saveDirBtn);
            } else {
                flashColour(1500, Color.RED, saveDirBtn);
            }
        });

        saveBtn.setOnAction((ActionEvent e) -> {
            if (filePath.getText() != null || !filePath.getText().isEmpty()) {
                WorkingUser.adminWriteOutDatabase(SQLInterface.TABITEM, Compatibility.getFilePath(filePath.getText() + "/adminItemDatabase.csv")); //adminPersonDatabase.csv

            }
        });
    }

    private static void changeAdminPassword(GridPane grid) {
        grid.getChildren().clear();

        Text current = new Text("Enter your current password: ");
        Text first = new Text("New Password:");
        Text second = new Text("Retype New Password:");
        PasswordField currentInput = new PasswordField();
        PasswordField firstInput = new PasswordField();
        PasswordField secondInput = new PasswordField();
        grid.add(current, 0, 1);
        grid.add(currentInput, 1, 1);
        grid.add(first, 0, 2);
        grid.add(firstInput, 1, 2);
        grid.add(second, 0, 3);
        grid.add(secondInput, 1, 3);


        firstInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                secondInput.requestFocus();
            }
        });
        secondInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                int success = -1;
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (firstInput.getText().equals(secondInput.getText())) {
                        success = WorkingUser.setPassword(WorkingUser.getUserID(), firstInput.getText(), WorkingUser.getUserID(), currentInput.getText());
                    }

                    // Success is an int, 0 = success, 1 = user not found, 2 = admin not found/password issue
                    switch (success) { // In this instance we have checked that the user exists on login. If they have gotten to this point we have a security
                        // risk which flashing a textfield at them will not resolve.
                        case 0:
                            flashColour(1500, Color.AQUAMARINE, firstInput, secondInput, currentInput);
                            break;
                        case 2:
                            flashColour(1500, Color.RED, currentInput);
                            break;
                        default:
                            flashColour(1500, Color.RED, firstInput, secondInput, currentInput);
                    }

                }
            }
        });
    }

    private static void SaveDatabases(Stage adminStage, GridPane grid) {
        DirectoryChooser fc = new DirectoryChooser();


        grid.getChildren().clear();
        Text fileLabel = new Text("Save Directory");
        TextField filePath = new TextField("");
        filePath.setEditable(true);
        Button saveDirBtn = new Button("Choose Save Directory");
        Button saveBtn = new Button("Save Databases to Selected Directories");

        grid.add(saveBtn, 1, 5);
        grid.add(fileLabel, 0, 0);
        grid.add(filePath, 0, 1);
        grid.add(saveDirBtn, 1, 1);

        saveDirBtn.setOnAction((ActionEvent e) -> {
            File returnVal = fc.showDialog(adminStage);

            if (returnVal != null) {
                filePath.setText(returnVal.getPath());
                flashColour(1500, Color.AQUAMARINE, saveDirBtn);
            } else {
                flashColour(1500, Color.RED, saveDirBtn);
            }
        });

        saveBtn.setOnAction((ActionEvent e) -> {
            //try {
                if (filePath.getText() != null || filePath.getText().isEmpty()) {
                    WorkingUser.adminWriteOutDatabase(SQLInterface.TABPERSON, Compatibility.getFilePath(filePath.getText() + "/adminPersonDatabase.csv")); //adminPersonDatabase.csv
                    WorkingUser.adminWriteOutDatabase(SQLInterface.TABITEM, Compatibility.getFilePath(filePath.getText() + "/adminItemDatabase.csv")); //adminPersonDatabase.csv

                }
        });
    }

    private static void CloseProgram(GridPane grid) {
        grid.getChildren().clear();
        Button exit = new Button("Close The Program");
        grid.add(exit, 1, 1);
        exit.setOnAction((ActionEvent e) -> {
            flashColour(1500, Color.AQUAMARINE, exit);
            System.exit(0);
        });
    }

    private static void createAdmins(GridPane grid) {
        grid.getChildren().clear();
        Text IDLabel = new Text("New admin's ID:");
        TextField ID = new TextField();
        ChoiceBox<String> level = new ChoiceBox<>();
        level.getItems().setAll("USER", "ADMIN", "STAFF");
        Button save = new Button("Save");
        save.setOnAction((ActionEvent e) -> {
            if (!WorkingUser.personExists(ID.getText())) {
                flashColour(1500, Color.RED, ID);
            } else {
                int levelInt = PersonDatabase.USER;
                switch (level.getSelectionModel().getSelectedItem()) {
                    case "USER":
                        break;
                    case "ADMIN":
                        levelInt = PersonDatabase.ADMIN;
                        break;
                    case "STAFF":
                        levelInt = PersonDatabase.ROOT;
                        break;
                }
                WorkingUser.updateRole(ID.getText(), levelInt);
                flashColour(1500, Color.AQUAMARINE, save);
            }
        });

        grid.add(IDLabel, 0, 0);
        grid.add(ID, 1, 0);
        grid.add(level, 2, 0);
        grid.add(save, 3, 0);
    }

    public static void showPasswordLog(GridPane grid) {
        grid.getChildren().clear();
        DatePicker dpTo = new DatePicker(LocalDate.now());
        DatePicker dpFrom = new DatePicker(LocalDate.now());
        TableView productTable = new TableView();
        productTable.setEditable(false);
        TableColumn IDCol = new TableColumn("ID");
        TableColumn dateCol = new TableColumn("Date");
        TableColumn authCol = new TableColumn("Auth Name");
        productTable.getColumns().addAll(IDCol, dateCol, authCol);

        ObservableList<PasswordLog> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getPasswordLog());
        productTable.setItems(product);
        dpFrom.setOnAction((ActionEvent e) -> {
            if (dpTo.getValue().equals(dpFrom.getValue())) {
                product.setAll(WorkingUser.getPasswordLog());
                productTable.setItems(product);
            } else {
                product.setAll(WorkingUser.getPasswordLog(dpFrom.getValue(), dpTo.getValue()));
                productTable.setItems(product);
            }
        });
        dpTo.setOnAction((ActionEvent e) -> {
            if (dpTo.getValue().equals(dpFrom.getValue())) {
                product.setAll(WorkingUser.getPasswordLog());
                productTable.setItems(product);
            } else {
                product.setAll(WorkingUser.getPasswordLog(dpFrom.getValue(), dpTo.getValue()));
                productTable.setItems(product);
            }
        });
        IDCol.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("ID")
        );
        dateCol.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("date")
        );
        authCol.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("authID")
        );
        productTable.setMinWidth(grid.getMaxWidth());
        grid.add(dpFrom, 0, 0);
        grid.add(dpTo, 1, 0);
        grid.add(productTable, 0, 1, 5, 10);
    }

    public static void showItemLog(GridPane grid) {
        grid.getChildren().clear();
        DatePicker dpTo = new DatePicker(LocalDate.now());
        DatePicker dpFrom = new DatePicker(LocalDate.now());
        CheckBox cb = new CheckBox("Only out items");
        TableView productTable = new TableView();
        productTable.setEditable(false);
        TableColumn IDCol = new TableColumn("ID");
        TableColumn outDate = new TableColumn("Out Date");
        TableColumn inDate = new TableColumn("In Date");
        TableColumn persID = new TableColumn("Person ID");
        TableColumn controlled = new TableColumn("Controlled");
        TableColumn adminName = new TableColumn("Admin Name");
        TableColumn itemID = new TableColumn("Item ID");
        productTable.getColumns().addAll(IDCol, outDate, inDate, persID, controlled, adminName, itemID);


        ObservableList<ItemLog> product = FXCollections.observableArrayList();
        product.setAll(WorkingUser.getItemLog(cb.isSelected()));
        productTable.setItems(product);
        dpFrom.setOnAction((ActionEvent e) -> {
            if (dpTo.getValue().equals(dpFrom.getValue())) {
                product.setAll(WorkingUser.getItemLog(cb.isSelected()));
                productTable.setItems(product);
            } else {
                product.setAll(WorkingUser.getItemLog(cb.isSelected(), dpFrom.getValue(), dpTo.getValue()));
                productTable.setItems(product);
            }
        });
        dpTo.setOnAction((ActionEvent e) -> {
            if (dpTo.getValue().equals(dpFrom.getValue())) {
                product.setAll(WorkingUser.getItemLog(cb.isSelected()));
                productTable.setItems(product);
            } else {
                product.setAll(WorkingUser.getItemLog(cb.isSelected(), dpFrom.getValue(), dpTo.getValue()));
                productTable.setItems(product);
            }
        });
        cb.setOnAction((ActionEvent e) -> {
            if (dpTo.getValue().equals(dpFrom.getValue())) {
                product.setAll(WorkingUser.getItemLog(cb.isSelected()));
            } else {
                product.setAll(WorkingUser.getItemLog(cb.isSelected()));
                productTable.setItems(product);
            }
        });
        IDCol.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("ID")
        );
        outDate.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("outDate")
        );
        inDate.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("inDate")
        );
        persID.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("persID")
        );
        controlled.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("controlled")
        );
        adminName.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("returnedBy")
        );
        itemID.setCellValueFactory(
                new PropertyValueFactory<PasswordLog, String>("itemID")
        );
        productTable.setMinWidth(grid.getMaxWidth());
        grid.add(dpFrom, 0, 0);
        grid.add(dpTo, 1, 0);
        grid.add(cb, 2, 0);
        grid.add(productTable, 0, 1, 5, 10);
    }
    public static void addUser(String userID) {

        Stage AddStage = new Stage();
        AddStage.setTitle("Add User");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));

        Text ID = new Text("Enter your ID:");
        grid.add(ID, 0, 0);
        Text name = new Text("Enter your name:");
        grid.add(name, 0, 1);
        Text first = new Text("Enter your password:");
        grid.add(first, 0, 2);
        Text second = new Text("Reenter your password:");
        grid.add(second, 0, 3);
        Text error = new Text("Passwords do not match");

        TextField IDInput = new TextField(userID);
        grid.add(IDInput, 1, 0);
        TextField nameInput = new TextField();
        grid.add(nameInput, 1, 1);
        PasswordField firstInput = new PasswordField();
        grid.add(firstInput, 1, 2);
        PasswordField secondInput = new PasswordField();
        grid.add(secondInput, 1, 3);

        IDInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                nameInput.requestFocus();
            }
        });
        nameInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                firstInput.requestFocus();
            }
        });
        firstInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                secondInput.requestFocus();
            }
        });
        secondInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (!WorkingUser.personExists(IDInput.getText())) {
                    if (secondInput.getText().equals(firstInput.getText())) {
                        WorkingUser.addPersonToDatabase(nameInput.getText(), IDInput.getText(), firstInput.getText());
                        flashColour(1500, Color.AQUAMARINE, IDInput, nameInput, firstInput, secondInput);
                    } else {
                        flashColour(1500, Color.RED, firstInput, secondInput);
                        grid.getChildren().remove(error);
                        error.setText("Passwords do not match");
                        grid.add(error, 0, 5, 2, 1);
                    }
                } else {
                    flashColour(1500, Color.RED, IDInput);
                    grid.getChildren().remove(error);
                    error.setText("ID already exists, contact the LOGO to reset your password");
                    grid.add(error, 0, 5, 2, 1);
                }
            }
        });
        Button close = new Button("Close");
        close.setOnAction((ActionEvent e) -> AddStage.close());
        grid.add(close, 1, 4);


        Scene PassScene = new Scene(grid, 500, 500);
        AddStage.setScene(PassScene);
        AddStage.show();
        AddStage.toFront();
    }

    public static void changePassword(String userID) {
        Stage PassStage = new Stage();
        PassStage.setTitle("Change Your Password");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15, 15, 15, 15));

        Text ID = new Text("Enter your ID:");
        Text first = new Text("New Password:");
        Text second = new Text("Retype New Password:");
        TextField IDInput = new TextField();
        IDInput.setText(userID);
        PasswordField firstInput = new PasswordField();
        PasswordField secondInput = new PasswordField();
        grid.add(ID, 0, 0);
        grid.add(IDInput, 1, 0);
        grid.add(first, 0, 1);
        grid.add(firstInput, 1, 1);
        grid.add(second, 0, 2);
        grid.add(secondInput, 1, 2);
        Text admin = new Text("Enter admin ID");
        Text admin2 = new Text("Enter admin password");
        TextField adminID = new TextField();
        PasswordField adminPass = new PasswordField();
        grid.add(admin, 0, 4);
        grid.add(adminID, 1, 4);
        grid.add(admin2, 0, 5);
        grid.add(adminPass, 1, 5);

        IDInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                firstInput.requestFocus();
            }
        });
        firstInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                secondInput.requestFocus();
            }
        });
        secondInput.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (firstInput.getText().equals(secondInput.getText())) {
                    adminID.requestFocus();
                } else {
                    flashColour(1500, Color.RED, firstInput, secondInput);
                }
            }
        });
        adminID.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                adminPass.requestFocus();
            }
        });
        adminPass.setOnKeyPressed((KeyEvent ke) -> {
            int success = 3;
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (firstInput.getText().equals(secondInput.getText()) && IDInput != adminID) {
                    //new Thread(() -> {
                      success = WorkingUser.setPassword(IDInput.getText(), firstInput.getText(), adminID.getText(), adminPass.getText());
                    //}).start();

                }

                if (success == 0) {
                    flashColour(1500, Color.AQUAMARINE, IDInput, firstInput, secondInput, adminID, adminPass);
                } else {
                    // Success is an int, 0 = success, 1 = user not found, 2 = admin not found/password issue
                    switch (success) {
                        case 1:
                            flashColour(1500, Color.RED, IDInput);
                            break;
                        case 2:
                            flashColour(1500, Color.RED, adminID, adminPass);
                            break;
                        default:
                            flashColour(1500, Color.RED, IDInput, firstInput, secondInput, adminID, adminPass);
                    }
                }
            }
        });
        Button close = new Button("Close");
        close.setOnAction((ActionEvent e) -> PassStage.close());
        grid.add(close, 1, 6);
        Scene PassScene = new Scene(grid, 500, 500);
        PassStage.setScene(PassScene);
        PassStage.show();
        PassStage.toFront();
    }

    private static boolean elementsAreNotEmpty(Node... nodes) {
        for (Node node : nodes) {
            if (node == null) return false;
            if (node instanceof TextField && ((TextField) node).getText().equals("")) return false;
        }
        return true;
    }
}
