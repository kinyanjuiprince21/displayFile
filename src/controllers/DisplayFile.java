package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class DisplayFile implements Initializable {

    @FXML
    private Button btnView;

    @FXML
    private Button btnChooseFile;

    @FXML
    private TextArea txtFile;

    List<File> files = new ArrayList<>();
    @FXML
    private TextField txtFilename;

    private Future<List<String>> listFuture;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public File filePath() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);

        if(file == null) {
            return null;
        }

        txtFilename.setText(file.getName());
        return file;
    }


    public List<String> read(File file) {
        List<String> lines = new ArrayList<String>();
        String line;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }

    public void displayFile(File file) {

        listFuture = executorService.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return read(new File(file.getPath()));
            }
        });

        try {
            List<String> lines = listFuture.get();
            txtFile.clear();

            for(String line : lines) {
                txtFile.appendText(line + "\n");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String filePath;
        btnChooseFile.setOnAction(e -> {
            files.add(filePath());
            String fileName = null;
            for (File file : files) {
                fileName = file.getName();
            }
            txtFilename.setText(fileName);
        });

        System.out.println(txtFilename.getText());

        btnView.setOnAction(e -> {
            for (File file : files) {
                displayFile(file);
            }
        });
    }

}
