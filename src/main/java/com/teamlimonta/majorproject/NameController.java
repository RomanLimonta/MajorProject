package com.teamlimonta.majorproject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class NameController {
    public ListView<String> listOFNamesViewer;
    public Hashtable<Integer, String> table = new Hashtable<>();
    public Button okButton;
    public Button cancelButton;
    private final File file = new File("C:\\Users\\Roman\\Desktop\\MajorProject\\userFiles\\ListofNames.txt");


    public void initialize() throws IOException {

//************************************************************************************************************************
//************************************************************************************************************************
        Runnable task = () -> {
            String s = Platform.isFxApplicationThread() ? "UI Thread" : "Background Thread";

            Stack<String> nameList = loadStackList(file);

            String temp = nameList.toString().trim();
            temp = temp.replaceAll("[^a-zA-Z0-9]", " ");
            String[] list = temp.split("[^a-zA-Z0-9]");

            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(list).subList(0, list.length - 1));

            for (int i = 0; i < arrayList.size() - 1; i++) {
                if (arrayList.get(i).isEmpty() || arrayList.get(i).isBlank()) {
                    arrayList.remove(i);
                    if (i != 0) {
                        i--;
                    }
                }
            }

            for (int i = 0; i < arrayList.size() - 1; i++) {
                table.put(i, arrayList.get(i));
            }

            arrayList.sort(String::compareToIgnoreCase);
            System.out.println(arrayList);

            System.out.println("Finished Running on the : " + s);

            Platform.runLater(() -> {
                String s1 = Platform.isFxApplicationThread() ? "UI Thread" : "Background Thread";

//                for(String table : table.values()){
//                    listOFNamesViewer.getItems().add(table);
//                }

                for (Iterator<String> it = arrayList.iterator(); it.hasNext(); ) {
                    String table = it.next();
                    listOFNamesViewer.getItems().add(table);
                }

                System.out.println("Finished Running on the : " + s1);
            });
        };

        new Thread(task).start();


    }
    public void onOkClicked(ActionEvent event) {
        System.out.println("Ok button clicked");

    }

    public void onCancelClicked(ActionEvent event) throws IOException {
        stageSwitcher("main-window.fxml",event);
    }

    public void stageSwitcher(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
}

