import exceptions.DataFileException;
import i18n.Messages;
import model.files.locationModels.LocationModel;
import model.files.locationModels.MyLocationsHolder;
import model.files.mediaFiles.MyFileModel;
import model.files.mediaFiles.MyFilesHolder;
import ui.Menu;

import java.util.HashMap;

public class Main {
    private static final Messages messages = Messages.getInstance();
    private static HashMap<Integer, LocationModel> locationsMap;
    private static HashMap<Integer, MyFileModel> filesMap;
    private static boolean loaded;

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