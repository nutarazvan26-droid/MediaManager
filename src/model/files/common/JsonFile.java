package model.files.common;

import exceptions.DataFileException;
import i18n.Messages;
import org.json.JSONException;

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
    private static final Messages messages = Messages.getInstance();

    public JsonFile(String inputFileName, String outputFileName) {
        super(inputFileName, outputFileName);
    }

    /**
     * This method is used to parse a generic json file that supports type of T as model,
     * and will parse to the type of T in the override method of parseJSON in each class that derives from this class.
     * If the input file is missing or empty, a message is printed and the returned map is empty.
     *
     * @return {@code HashMap<Integer, T>}, where T is the model of the object to be read from the input file.
     * Because T extends ICommonFileModel, it has access to the getId method from T.
     * @throws DataFileException In the case the input file cannot be read or it does not contain a valid JSON
     * throws this exception.
     * @see model.files.mediaFiles.MyFilesHolder
     * @see model.files.locationModels.MyLocationsHolder
     */
    @Override
    public HashMap<Integer, T> ParseFile() throws DataFileException {
        HashMap<Integer, T> myFiles = new HashMap<>();
        String linie = "";
        String jsonStr = "";

        try {
            ReadFile();
        } catch (FileNotFoundException e) {
            System.out.println(messages.get("label.error.data.not-found") + ": " + getInputFileName());
            return myFiles;
        } catch (IOException e) {
            throw new DataFileException("label.error.data.unreadable");
        }

        try {
            BufferedReader br = GetBufferedReader();

            while ((linie = br.readLine()) != null) {
                jsonStr = jsonStr + linie;
            }

            br.close();
        } catch (IOException e) {
            throw new DataFileException("label.error.data.unreadable");
        }

        if (jsonStr.trim().isEmpty()) {
            System.out.println(messages.get("label.error.data.empty") + ": " + getInputFileName());
            return myFiles;
        }

        List<T> myFilesList = null;
        try {
            myFilesList = parseJSON(jsonStr);
        } catch (JSONException e) {
            throw new DataFileException("label.error.data.invalid-json");
        }
        myFilesList.forEach(file -> {
            if (!myFiles.containsKey(file.getId())) {
                myFiles.put(file.getId(), file);
            }
        });

        return myFiles;
    }

    protected abstract List<T> parseJSON(String jsonStr);

    /**
     * This method is used to write the objects in the output file, as JSON, using the override method of toJSON
     * from each class that derives from this class. It is called when the application is closed.
     * For the holders the output file is the same as the input file, so the next start reads the saved data.
     *
     * @param objectsToWrite The objects to be written in the output file
     * @throws DataFileException In the case the output file cannot be written throws this exception.
     * @see #ParseFile()
     */
    @Override
    public void WriteFile(Collection<T> objectsToWrite) throws DataFileException {
        String jsonStr = toJSON(objectsToWrite);
        try {
            BufferedWriter br = GetBufferedWriter();
            br.write(jsonStr);
            br.close();
        } catch (IOException e) {
            throw new DataFileException("label.error.data.not-saved");
        }
    }

    protected abstract String toJSON(Collection<T> myFilesList);
}
