package uk.gov.dwp.uc.pairtest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TicketingConfig {
    private static final String PROPERTIES_FILE = "application.properties";
    private static final Properties PROPS = new Properties();
    static {
        try (InputStream in = TicketingConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private TicketingConfig() {}

    public static int getMaxTickets() {
        return Integer.parseInt(PROPS.getProperty("max.tickets", "25"));
    }
}
