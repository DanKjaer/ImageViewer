package dk.easv;

import javafx.util.StringConverter;

public class ConverterHelper extends StringConverter<Number> {

    /**
     * Convert from Number num to String
     * @param num the object of type {@code T} to convert
     * @return
     */
    @Override
    public String toString(Number num) {
        return String.valueOf(Math.round(num.longValue()));
    }

    /**
     *
     * @param string the {@code String} to convert
     * @return
     */
    @Override
    public Number fromString(String string) {
        try {
            Double d = Double.parseDouble(string);
            return Math.round(d);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }
}
