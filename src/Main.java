import exceptions.DataFileException;
import i18n.Messages;
import model.files.locationModels.LocationModel;
import model.files.locationModels.MyLocationsHolder;
import model.files.mediaFiles.MyFileModel;
import model.files.mediaFiles.MyFilesHolder;
import ui.Menu;

import java.util.HashMap;

/**
 * This is the main class of the application, that loads the data from the files, starts the menu
 * and saves the data back in the same files when the application is closed.
 * The locations are parsed before the media files, because the media files are validated against them.
 *
 * @author RazvanNuta
 * @see Menu
 * @see MyLocationsHolder
 * @see MyFilesHolder
 */
public class Main {
    private static final Messages messages = Messages.getInstance();
    private static HashMap<Integer, LocationModel> locationsMap;
    private static HashMap<Integer, MyFileModel> filesMap;
    private static boolean loaded;

    /**
     * This method is used to start the application.
     * It adds the exit behaviour first, which saves the locations and the media files when the application is closed,
     * then parses the files and shows the menu only if both files could be read.
     * If a file is unreadable or does not contain a valid JSON, the application stops without saving anything,
     * so the files are not overwritten.
     *
     * @param args The arguments from the command line, not used by the application
     */
    public static void main(String[] args) {
        //Add exit behaviour
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            exit();
        }));

        loaded = startApp();
        if (loaded) {
            Menu.GetInstance().start();
        }
    }

    private static void exit() {
        System.out.println(messages.get("label.app.closed"));
        if (!loaded) {
            return;
        }

        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        MyFilesHolder filesHolder = MyFilesHolder.GetInstance();

        try {
            locationsHolder.WriteFile(locationsMap.values());
        } catch (DataFileException e) {
            System.out.println(messages.get(e.getMessage()) + ": " + locationsHolder.getOutputFileName());
        }
        try {
            filesHolder.WriteFile(filesMap.values());
        } catch (DataFileException e) {
            System.out.println(messages.get(e.getMessage()) + ": " + filesHolder.getOutputFileName());
        }
    }

    private static boolean startApp() {
        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        MyFilesHolder filesHolder = MyFilesHolder.GetInstance();

        try {
            locationsMap = locationsHolder.ParseFile();
        } catch (DataFileException e) {
            System.out.println(messages.get(e.getMessage()) + ": " + locationsHolder.getInputFileName());
            System.out.println(messages.get("label.error.data.stopped"));
            return false;
        }

        try {
            filesMap = filesHolder.ParseFile(locationsMap);
        } catch (DataFileException e) {
            System.out.println(messages.get(e.getMessage()) + ": " + filesHolder.getInputFileName());
            System.out.println(messages.get("label.error.data.stopped"));
            return false;
        }
        return true;
    }
}