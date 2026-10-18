import i18n.Messages;
import model.files.LocationModels.Location;
import model.files.MediaFiles.MyFile;
import model.files.MediaFiles.MyFilesIn;
import model.files.LocationModels.MyLocationsInFile;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static Messages messages = Messages.getInstance();

    public static void main(String[] args) {

        MyLocationsInFile locations = MyLocationsInFile.GetInstance();
        MyFilesIn filesIn = MyFilesIn.GetInstance();

        java.util.HashMap<Integer, Location> locationsMap = locations.ParseFile();
        System.out.println(locationsMap.toString());

        java.util.HashMap<Integer, MyFile> filesMap = filesIn.ParseFile();
        System.out.println(filesMap.toString());
    }

    public void start(){
        
    }
}