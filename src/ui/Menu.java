package ui;

import exceptions.InvalidOptionException;
import i18n.Messages;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Menu {
    private static final int OPTION_EXIT = 0;
    private static final int OPTION_CHANGE_LANGUAGE = 1;
    private static final String INVALID_OPTION = "label.error.input.invalid-option";
    private static final Messages messages = Messages.getInstance();
    private static Menu instance;
    private final Scanner scanner = new Scanner(System.in);

    private Menu() {
    }

    public static Menu GetInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int option = readOption(OPTION_EXIT, OPTION_CHANGE_LANGUAGE);
                switch (option) {
                    case OPTION_CHANGE_LANGUAGE:
                        changeLanguage();
                        break;
                    case OPTION_EXIT:
                        running = false;
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println(messages.get("label.error.input.not-a-number"));
            } catch (InvalidOptionException e) {
                System.out.println(messages.get(e.getMessage()));
            } catch (NoSuchElementException e) {
                running = false;
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println(messages.get("label.menu.title"));
        System.out.println(OPTION_CHANGE_LANGUAGE + ". " + messages.get("label.menu.change-language"));
        System.out.println(OPTION_EXIT + ". " + messages.get("label.menu.exit"));
        System.out.print(messages.get("label.menu.choose-option") + ": ");
    }

    private int readOption(int min, int max) throws InvalidOptionException {
        int option = Integer.parseInt(scanner.nextLine().trim());
        if (option < min || option > max) {
            throw new InvalidOptionException(INVALID_OPTION);
        }
        return option;
    }

    private void changeLanguage() throws InvalidOptionException {
        Locale[] locales = Messages.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            System.out.println((i + 1) + ". " + locales[i].getLanguage().toUpperCase());
        }
        System.out.print(messages.get("label.menu.choose-language") + ": ");

        int option = readOption(1, locales.length);
        messages.setLocale(locales[option - 1]);
        System.out.println(messages.get("label.menu.language-changed"));
    }
}
