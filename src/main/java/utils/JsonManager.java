package utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.Reader;

/**
 * Parse Json into objects.
 *
 * @author Palak Jain
 */
public class JsonManager {
    private static final Logger logger = (Logger) LogManager.getLogger(JsonManager.class);
    /**
     * Parse JSON string into an object
     * @param reader Reader object
     * @param classOfT Type of Class
     * @return Parsed object
     */
    public static <T> T fromJson(Reader reader, Class<T> classOfT) {
        Gson gson = new Gson();

        T object = null;

        try {
            object = gson.fromJson(reader, classOfT);
        }
        catch (JsonSyntaxException exception) {
            logger.printf(Level.ERROR,"Unable to parse json");
        }

        return object;
    }

    /**
     * Parse JSON string into an object
     * @param json JSON string
     * @param classOfT Type of Class
     * @return Paresed object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = new Gson();

        T object = null;

        try {
            object = gson.fromJson(json, classOfT);
        }
        catch (JsonSyntaxException exception) {
            logger.printf(Level.ERROR,"Unable to parse json %s. %s", json, exception);
        }

        return object;
    }
}
