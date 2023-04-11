package dk.easv;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class PixelCounterFuckYou implements Runnable, Callable<Result> {
    private Image img;
    private int xStart;
    private int xSlut;
    private int yStart;
    private int ySlut;
    private int colorGreen = 0;
    private int colorRed = 0;
    private int colorBlå = 0;
    private int mixed = 0;

    public PixelCounterFuckYou(Image img, int xStart, int xSlut, int yStart, int ySlut) {
        this.img = img;
        this.xStart = xStart;
        this.yStart = yStart;
        this.xSlut = xSlut;
        this.ySlut = ySlut;

    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result call() throws Exception {
        for (int i = xStart; i < xSlut; i++){
            for (int j = yStart; j < ySlut; j++) {
                Color color = img.getPixelReader().getColor(i,j);

                double blå = color.getBlue();
                double red = color.getRed();
                double green = color.getGreen();

                // Dan's ide
                if (red > blå && red > green) {
                    colorRed++;
                } else if (blå > red && blå > green) {
                    colorBlå++;
                } else if (green > red && green > blå) {
                    colorGreen++;
                } else if (green == blå || blå == red || green == red) {
                    mixed++;
                }
            }
        }

        return new Result(colorGreen,colorRed,colorBlå,mixed);
    }
}
