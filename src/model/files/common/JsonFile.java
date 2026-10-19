package model.files.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This is the extension on top of BaseFile that will extend the functionality of the files in
 * order to parse JSON contents from the input and output files.
 *
 * @param <T> extends ICommonFileModel
 * @see ICommonFileModel
 * @see BaseFile
 */
public abstract class JsonFile<T extends ICommonFileModel> extends BaseFile<T> {

    public JsonFile(String inputFileName, String outputFileName) {
        super(inputFileName, outputFileName);
    }

    /**
     * This method is used to parse a generic json file that supports type of T as model,
     * and will parse to the type of T in the override method of parseJSON in each class that derives from this class.
     *
     * @return HashMap<Integer, T>, where T is the model of the object to be read from the input file.
     * Because T extends CommonFile, it has access to the getId method from T.
     * @see model.files.mediaFiles.MyFilesHolder
     * @see model.files.locationModels.MyLocationsHolder
     * @since 2026.19.10
     */
    @Override
    public HashMap<Integer, T> ParseFile() {
        HashMap<Integer, T> myFiles = new HashMap<>();
        String linie = "";
        String jsonStr = "";

        try {
            BufferedReader br = GetBufferedReader();

            while ((linie = br.readLine()) != null) {
                jsonStr = jsonStr + linie;
            }

            List<T> myFilesList = parseJSON(jsonStr);
            myFilesList.forEach(file -> {
                if (!myFiles.containsKey(file.getId())) {
                    myFiles.put(file.getId(), file);
                }
            });

            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return myFiles;
    }

    protected abstract List<T> parseJSON(String jsonStr);

    @Override
    public void WriteFile(Collection<T> objectsToWrite) {
        String jsonStr = toJSON(objectsToWrite);
        BufferedWriter br = GetBufferedWriter();
        try {
            br.write(jsonStr);
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String toJSON(Collection<T> myFilesList);
}
