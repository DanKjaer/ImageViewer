package dk.easv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.naming.Binding;

public class ImageViewerWindowController implements Initializable {
    private final List<Image> images = new ArrayList<>();
    @FXML
    private Label lblBlå;
    @FXML
    private Label lblMixed;
    @FXML
    private Label lblRed;
    @FXML
    private Label lblGreen;
    @FXML
    private Button btnStop;
    @FXML
    private Label lblImageName;
    @FXML
    private Slider sldrDelay;
    @FXML
    private Label lblDelay;
    @FXML
    private TextField tfDelay;
    private int currentImageIndex = 0;
    private int i = 0;
    private boolean stopped;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;
    private ExecutorService es = Executors.newFixedThreadPool(1);


    public ImageViewerWindowController() {
        imageLoop();
    }

    private void setUpBindings() {
        sldrDelay.valueProperty().addListener(((observable, oldValue, newValue) -> {
            lblDelay.setText(String.valueOf(Math.round(newValue.floatValue())));
        }));
    }

    @FXML
    private void handleBtnLoadAction() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty()) {
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
    private void handleBtnPreviousAction() throws Exception {
        if (!images.isEmpty()) {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction() throws Exception {
        nextImage();
    }

    private void nextImage() throws Exception {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
            changeFilenameLabel(images.get(currentImageIndex));
        }
    }

    private void countPixels(Image image) throws Exception{

        int x1 = (int) image.getWidth()/4;
        int x2 = x1 * 2;
        int x3 = x1 * 3;
        int x4 = (int) image.getWidth();

        int y1 = (int) image.getHeight()/4;
        int y2 = y1 * 2;
        int y3 = y1 * 3;
        int y4 = (int) image.getHeight();

        ExecutorService es = Executors.newFixedThreadPool(4);
        PixelCounterFuckYou fuckYou1 = new PixelCounterFuckYou(image, 0, x1, 0 ,y1);
        PixelCounterFuckYou fuckYou2 = new PixelCounterFuckYou(image,x1, x2, y1,y2);
        PixelCounterFuckYou fuckYou3 = new PixelCounterFuckYou(image,x2,x3,y2,y3);
        PixelCounterFuckYou fuckYou4 = new PixelCounterFuckYou(image,x3,x4,y3,y4);

        Future f1 = es.submit((Callable<Result>) fuckYou1);
        Future f2 = es.submit((Callable<Result>) fuckYou2);
        Future f3 = es.submit((Callable<Result>) fuckYou3);
        Future f4 = es.submit((Callable<Result>) fuckYou4);

        Result r1 = (Result) f1.get();
        Result r2 = (Result) f2.get();
        Result r3 = (Result) f3.get();
        Result r4 = (Result) f4.get();

        int green = r1.getColorGreen() + r2.getColorGreen() + r3.getColorGreen() + r4.getColorGreen();
        int red = r1.getColorRed() + r2.getColorRed() + r3.getColorRed() + r4.getColorRed();
        int blå = r1.getColorBlå() + r2.getColorBlå() + r3.getColorBlå() + r4.getColorBlå();
        int mixed = r1.getMixed() + r2.getMixed() + r3.getMixed() + r4.getMixed();

        Platform.runLater(() -> lblBlå.setText("Blå pixels: " + blå));
        Platform.runLater(() -> lblRed.setText("Red pixels: " + red));
        Platform.runLater(() -> lblGreen.setText("Green pixels: " + green));
        Platform.runLater(() -> lblMixed.setText("Mixed pixels: " + mixed));
    }

    private void displayImage() throws Exception {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentImageIndex));
            countPixels(images.get(currentImageIndex));
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            i = (int) Math.round(sldrDelay.getValue());
            while (!images.isEmpty()) {
                try {
                    nextImage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    TimeUnit.SECONDS.sleep(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };


    private void imageLoop() {
        es.submit(runnable);
    }

    @FXML
    private void handleStop(ActionEvent actionEvent) {
        if (stopped) {
            startShow();
        } else {
            stopShow();
        }
    }

    private void startShow() {
        imageLoop();
        stopped = false;
        btnStop.setText("STOP");
    }

    private void stopShow() {
        es.shutdownNow();
        es = Executors.newFixedThreadPool(1);
        stopped = true;
        btnStop.setText("START");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpBindings();
    }

    private void changeFilenameLabel(Image image) {
        String imageURL = image.getUrl();
        String imageName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
        Platform.runLater(() -> lblImageName.setText(imageName));
    }
}