package applications.search;

import applications.search.configuration.SearchConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SearchApplicationTest {
    private SearchApplication searchApplication = new SearchApplication();

    @Test
    public void getConfig_invalidArgs_returnNull() {
        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("getConfig", String[].class);
            processMethod.setAccessible(true);
            String configFile = (String) processMethod.invoke(searchApplication, (Object) new String[]{"-config"});

            Assertions.assertNull(configFile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void getConfig_validArgs_returnString() {
        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("getConfig", String[].class);
            processMethod.setAccessible(true);
            String configFile = (String) processMethod.invoke(searchApplication, (Object) new String[]{"-config", "Configuration.json"});

            Assertions.assertNotNull(configFile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_nullConfig_returnFalse() {
        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_noReviewFilePath_returnFalse() {
        initConfig(new SearchConfig(null, "qa_Cell_Phones_and_Accessories.json"));

        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_noQAFilePath_returnFalse() {
        initConfig(new SearchConfig("Cell_Phones_and_Accessories_5.json", null));

        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_noReviewFile_returnFalse() {
        initConfig(new SearchConfig("Cell_Phones_and_Accessories_51.json", "qa_Cell_Phones_and_Accessories.json"));

        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_noQAFile_returnFalse() {
        initConfig(new SearchConfig("Cell_Phones_and_Accessories_5.json", "qa_Cell_Phones_and_Accessories1.json"));

        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_validConfig_returnTrue() {
        initConfig(new SearchConfig("Cell_Phones_and_Accessories_5.json", "qa_Cell_Phones_and_Accessories.json"));

        try {
            Method processMethod = SearchApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(searchApplication);

            Assertions.assertTrue(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private void initConfig(SearchConfig searchConfig) {
        try {
            Field configField = SearchApplication.class.getDeclaredField("configuration");
            configField.setAccessible(true);
            configField.set(searchApplication, searchConfig);
        }
        catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
