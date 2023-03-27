package dk.easv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.naming.Binding;

public class ImageViewerWindowController implements Initializable
{
    private final List<Image> images = new ArrayList<>();
    public Label lblImageName;
    @FXML
    private Slider sldrDelay;
    @FXML
    private Label lblDelay;
    @FXML
    private TextField tfDelay;
    private int currentImageIndex = 0;
    private int i = 0;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;


    public ImageViewerWindowController() {
        imageLoop();
    }

    private void setUpBindings() {
        sldrDelay.valueProperty().addListener(((observable, oldValue, newValue) -> {
            lblDelay.setText(String.valueOf(Math.round(newValue.floatValue())));
        }));
    }

    @FXML
    private void handleBtnLoadAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty())
        {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
            ExecutorService es = Executors.newFixedThreadPool(1);
            imageLoop();
        }
    }

    @FXML
    private void handleBtnPreviousAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction()
    {
        nextImage();
    }

    private void nextImage(){
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            imageView.setImage(images.get(currentImageIndex));
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            i = (int) Math.round(sldrDelay.getValue());
            while(!images.isEmpty()){
                nextImage();
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };
    ExecutorService es = Executors.newFixedThreadPool(1);


    private void imageLoop(){
        System.out.println("running");
        es.submit(runnable);

    }

    public void handleStop(ActionEvent actionEvent) {
        System.out.println("stopped");
        es.shutdownNow();
        es.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpBindings();
    }
}