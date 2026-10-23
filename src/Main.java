import i18n.Messages;
import model.files.locationModels.LocationModel;
import model.files.locationModels.MyLocationsHolder;
import model.files.mediaFiles.MyFileModel;
import model.files.mediaFiles.MyFilesHolder;

import java.util.HashMap;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Messages messages = Messages.getInstance();
    private static HashMap<Integer, LocationModel> locationsMap;
    private static HashMap<Integer, MyFileModel> filesMap;

    public static void main(String[] args) {
        //Add exit behaviour
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            exit();
        }));

        startApp();

    }

    private static void exit() {
        System.out.println("Application closed.");
        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        MyFilesHolder filesHolder = MyFilesHolder.GetInstance();

        locationsHolder.WriteFile(locationsMap.values());
        filesHolder.WriteFile(filesMap.values());
    }

    private static void startApp() {
        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        MyFilesHolder filesHolder = MyFilesHolder.GetInstance();

        locationsMap = locationsHolder.ParseFile();
        System.out.println(locationsMap.toString());

        filesMap = filesHolder.ParseFile(locationsMap);
        System.out.println(filesMap.toString());
    }
}