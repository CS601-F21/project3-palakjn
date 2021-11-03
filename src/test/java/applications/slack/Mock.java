package applications.slack;

import applications.slack.controller.Slack;
import org.mockito.Mockito;

import java.util.Map;

/**
 * Return Mock objects of classes
 *
 * @author Palak Jain
 */
public class Mock {

    /**
     * Mock Slack class to return specific response when post method with specific input is being called.
     *
     * @return Mock object
     */
    public static Slack getSlackMock(String response) {
        Slack slack = Mockito.mock(Slack.class);
        Mockito.when(slack.post(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);

        return slack;
    }
}
