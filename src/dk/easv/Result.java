package dk.easv;

public class Result {
    private int colorGreen = 0;
    private int colorRed = 0;
    private int colorBlå = 0;
    private int mixed = 0;

    public Result(int colorGreen, int colorRed, int colorBlå, int mixed) {
        this.colorGreen = colorGreen;
        this.colorRed = colorRed;
        this.colorBlå = colorBlå;
        this.mixed = mixed;
    }

    public int getColorGreen() {
        return colorGreen;
    }

    public void setColorGreen(int colorGreen) {
        this.colorGreen = colorGreen;
    }

    public int getColorRed() {
        return colorRed;
    }

    public void setColorRed(int colorRed) {
        this.colorRed = colorRed;
    }

    public int getColorBlå() {
        return colorBlå;
    }

    public void setColorBlå(int colorBlå) {
        this.colorBlå = colorBlå;
    }

    public int getMixed() {
        return mixed;
    }

    public void setMixed(int mixed) {
        this.mixed = mixed;
    }
}
