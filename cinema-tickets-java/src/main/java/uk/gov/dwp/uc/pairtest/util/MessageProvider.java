package uk.gov.dwp.uc.pairtest.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MessageProvider {
    private static final String BUNDLE_NAME = "messages";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private MessageProvider() {
        // Utility class, hide constructor
    }

    public static String getMessage(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    public static String getMessage(String key, Object... params) {
        String pattern = getMessage(key);
        return MessageFormat.format(pattern, params);
    }
}