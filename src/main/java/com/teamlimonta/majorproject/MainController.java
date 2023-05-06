package com.teamlimonta.majorproject;

import com.teamlimonta.majorproject.datamodel.ProjectData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {

    public ListView<Projects> projectsView = new ListView<>();
    public ListView<String> nameListView = new ListView<>();
    public TextArea projectDetailsTextArea;
    public Label bottomLineLabel;
    public BorderPane mainBorderPane;
    @FXML
    public ListView<String> listOFNamesViewer = new ListView<>();
    private Stack<String> nameList = new Stack<>();
    public Hashtable<Integer, String> table = new Hashtable<>();

    private ContextMenu listContextMenu;
    private final File file = new File("C:\\Users\\Roman\\Desktop\\MajorProject\\userFiles\\ListofNames.txt");


    public void initialize() {

        nameList = loadStackList(file);

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            Projects project = projectsView.getSelectionModel().getSelectedItem();
            deleteProject(project);
        });
        listContextMenu.getItems().addAll(deleteMenuItem);

        projectsView.getSelectionModel().selectedItemProperty().addListener((observableValue, projects, t1) -> {
            if (t1 != null) {
                getFullDetails();
            }
        });
        projectsView.setItems(ProjectData.getInstance().getProjectList());

        projectsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        projectsView.getSelectionModel().selectFirst();

        projectsView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Projects> call(ListView<Projects> projectsListView) {
                ListCell<Projects> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Projects project, boolean b) {
                        super.updateItem(project, b);
                        if (b) {
                            setText(null);
                        } else {
                            setText(project.getName());
                            if (project.getDateCreated().equals(LocalDate.now())) {
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );

                return cell;
            }
        });

    }

    public void showNewProjectDialog(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Concept");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newproject-dialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);


        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            Projects newProject = controller.processResults();
            projectsView.getSelectionModel().select(newProject);
        }
    }

    public void deleteProject(Projects project) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Project?");
        alert.setHeaderText("Delete Project: " + project.getName());
        alert.setContentText("Are you sure you want to delete? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            ProjectData.getInstance().deleteProject(project);
        }
    }

    public static void saveStackList(Stack<String> stack, File file) {
        String tmp = stack.toString();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (pw != null) {
            pw.write(tmp);
            pw.close();
        }
    }

    public static Stack<String> loadStackList(File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Stack<String> tempStack = new Stack<>();

        while (true) {
            assert scanner != null;
            if (!scanner.hasNextLine()) break;
            tempStack.add(scanner.nextLine().replaceAll("[^a-zA-Z0-9 ,]", ""));
        }

        return tempStack;
    }

    public void getFullDetails() {
        Projects project = projectsView.getSelectionModel().getSelectedItem();

        String sb = "The Main Concept: \n" + project.getDetails() + "\n" +
                "Name of Protagonist: \n" + project.getProtagonist() + "\n" +
                "Question Tool: \n" + project.getQuestionTool() + "\n" +
                "Heart of Darkness Scene: \n" + project.getHeartOfDarkness();
        projectDetailsTextArea.setText(sb);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        bottomLineLabel.setText(df.format(project.getDateCreated()));
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        Projects project = projectsView.getSelectionModel().getSelectedItem();
        if (project != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteProject(project);
            }
        }
    }

    public void addCharacterName(ActionEvent event) {
        TextInputDialog popUP = new TextInputDialog();
        popUP.setTitle("Add Name");
        popUP.setHeaderText("Enter Name:");
        popUP.setContentText("Name:");

        Optional<String> result = popUP.showAndWait();

        if (result.isPresent()) {
            if (popUP.getResult().isEmpty()) {
                popUP.close();
                System.out.println("No name entered");
            } else {
                System.out.println(result.get());
                nameList.add(result.get().trim());
                saveStackList(nameList, file);
            }
        } else {
            popUP.close();
        }
        System.out.println(nameList);
    }

    public void viewCharacterNameList(ActionEvent event) throws IOException {

        if (nameList != null) {
            String temp = nameList.toString().trim();
            temp = temp.replaceAll("[^a-zA-Z0-9]", " ");
            String[] list = temp.split("[^a-zA-Z0-9]");

            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(list).subList(0, list.length - 1));

            for (int i = 0; i < arrayList.size() - 1; i++) {
                if (arrayList.get(i).isEmpty() || arrayList.get(i).isBlank()) {
                    arrayList.remove(i);
                    if (i != 0) {i--;}
                }
            }

            System.out.println("this is the array list ");
            System.out.println(arrayList);
            System.out.println("The array length is= " + arrayList.size());

            for (int i = 0; i < arrayList.size() - 1; i++) {
                table.put(i, arrayList.get(i));
            }

            System.out.println(table.toString());

//            for(String table : table.values()){
//                listOFNamesViewer.getItems().add(table);
//            }
            for(String table : table.values()){
                listOFNamesViewer.getItems().add(table);
            }
            listOFNamesViewer.getSelectionModel().selectFirst();



//            Dialog<ButtonType> dialog = new Dialog<>();
//            dialog.initOwner(mainBorderPane.getScene().getWindow());
//            dialog.setTitle("List of Names");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(getClass().getResource("namelist-dialog.fxml"));
//
//            try {
//                dialog.getDialogPane().setContent(fxmlLoader.load());
//
//            } catch (IOException e) {
//                System.out.println("Couldn't load the dialog");
//                e.printStackTrace();
//                return;
//            }
//            //Button button = new Button();
//
//            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//
//
//            Optional<ButtonType> result = dialog.showAndWait();
//            if (result.isPresent() && result.get() == ButtonType.OK) {
//               // DialogController controller = fxmlLoader.getController();
//                //Projects newProject = controller.processResults();
//                //projectsView.getSelectionModel().select(newProject);
//            }


        }

    }

    public void backToLoginWindow(ActionEvent event) throws IOException {
        String fxml = "login-window.fxml";
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
