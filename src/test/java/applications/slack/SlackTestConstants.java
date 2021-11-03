package applications.slack;

public class SlackTestConstants {

    public static final String NO_AUTH_SLACK_RESPONSE = """
            {
                "ok": false,
                "error": "invalid_auth",
                "warning": "missing_charset",
                "response_metadata": {
                    "warnings": [
                        "missing_charset"
                    ]
                }
            }""";

    public static final String NO_CHANNEL_SLACK_RESPONSE = """
            {
                "ok": false,
                "error": "invalid_arguments",
                "warning": "missing_charset",
                "response_metadata": {
                    "messages": [
                        "[ERROR] must provide a string [json-pointer:/channel]"
                    ],
                    "warnings": [
                        "missing_charset"
                    ]
                }
            }""";

    public static final String NO_TEXT_SLACK_RESPONSE = """
            {
                "ok": false,
                "error": "no_text",
                "warning": "missing_charset",
                "response_metadata": {
                    "warnings": [
                        "missing_charset"
                    ]
                }
            }""";

    public static final String SLACK_SUCCESS_RESPONSE = """
            {
                 "ok": true,
                 "channel": "C02K8UR736Y",
                 "ts": "1635902514.000100",
                 "message": {
                     "bot_id": "B02K81ZRQS0",
                     "type": "message",
                     "text": "It's Friday :smile:",
                     "user": "U02KVFJG2TS",
                     "ts": "1635902514.000100",
                     "team": "T02B2ACTB0Q",
                     "bot_profile": {
                         "id": "B02K81ZRQS0",
                         "app_id": "A02K5R9F22X",
                         "name": "project3-palakjn",
                         "icons": {
                             "image_36": "https://a.slack-edge.com/80588/img/plugins/app/bot_36.png",
                             "image_48": "https://a.slack-edge.com/80588/img/plugins/app/bot_48.png",
                             "image_72": "https://a.slack-edge.com/80588/img/plugins/app/service_72.png"
                         },
                         "deleted": false,
                         "updated": 1635294344,
                         "team_id": "T02B2ACTB0Q"
                     }
                 },
                 "warning": "missing_charset",
                 "response_metadata": {
                     "warnings": [
                         "missing_charset"
                     ]
                 }
             }""";

    public static final String INCORRECT_CHANNEL_SLACK_RESPONSE = """
            {
                "ok": false,
                "error": "channel_not_found",
                "warning": "missing_charset",
                "response_metadata": {
                    "warnings": [
                        "missing_charset"
                    ]
                }
            }""";
}
