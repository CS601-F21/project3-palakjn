package applications.slack;

import applications.slack.configuration.SlackConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SlackApplicationTest {
    private SlackApplication slackApplication = new SlackApplication();

    @Test
    public void getConfig_invalidArgs_returnNull() {
        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("getConfig", String[].class);
            processMethod.setAccessible(true);
            String configFile = (String) processMethod.invoke(slackApplication, (Object) new String[]{"-config"});

            Assertions.assertNull(configFile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void getConfig_validArgs_returnString() {
        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("getConfig", String[].class);
            processMethod.setAccessible(true);
            String configFile = (String) processMethod.invoke(slackApplication, (Object) new String[]{"-config", "Configuration.json"});

            Assertions.assertNotNull(configFile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_nullConfig_returnFalse() {
        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(slackApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_nullAccessToken_returnFalse() {
        initConfig(new SlackConfig(null, "dummyChannel"));

        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(slackApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_nullChannel_returnFalse() {
        initConfig(new SlackConfig("access token", null));

        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(slackApplication);

            Assertions.assertFalse(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void verifyConfig_validConfig_returnTrue() {
        initConfig(new SlackConfig("Access Token", "Channel"));

        try {
            Method processMethod = SlackApplication.class.getDeclaredMethod("verifyConfig");
            processMethod.setAccessible(true);
            boolean isValid = (boolean) processMethod.invoke(slackApplication);

            Assertions.assertTrue(isValid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private void initConfig(SlackConfig slackConfig) {
        try {
            Field configField = SlackApplication.class.getDeclaredField("configuration");
            configField.setAccessible(true);
            configField.set(slackApplication, slackConfig);
        }
        catch (NoSuchFieldException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
