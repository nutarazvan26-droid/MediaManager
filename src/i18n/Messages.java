package i18n;

import exceptions.ResourceNotFoundException;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

    private static final String KEY_NOT_FOUND = "label.messages.key-not-found";
    private static final String BASE_NAME = "i18n/MessagesBundle";
    private static final Locale RO = new Locale("ro");
    private static final Locale ENG = new Locale("en");

    private static Messages instance;
    private static ResourceBundle resourceBundle;
    private Locale locale;

    private Messages(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }

    public static Messages getInstance() {
        if (instance == null) {
            instance = new Messages(RO);
        }
        return instance;
    }

    public static Locale[] getAvailableLocales() {
        return new Locale[]{RO, ENG};
    }

    /**
     * This method is used to translate available messages that are printed in this application.
     * Currently, supports 2 languages: RO, EN.
     *
     * @param key The translation key that is requested to be translated from the supported messages bundle.
     * @return The translated label if found by the requested key
     * @throws ResourceNotFoundException In the case the requested translation key is not found throws this exception.
     * @author RazvanN
     * @version 1.0 since 2026.10.11
     */
    public static String getLocatedMessage(String key) throws ResourceNotFoundException {
        if (!resourceBundle.containsKey(key)) {
            throw new ResourceNotFoundException(KEY_NOT_FOUND);
        }

        return resourceBundle.getString(key);
    }

    public String get(String key) {
        try {
            return getLocatedMessage(key);
        } catch (ResourceNotFoundException e) {
            return key;
        }
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Method used to switch the application supported language.
     *
     * @param locale "RO" or "EN"
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);
    }
}
