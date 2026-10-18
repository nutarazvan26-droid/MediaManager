package model.files;

import java.io.FileNotFoundException;


public abstract class JsonFile<T> extends BaseFile<T> {

    public JsonFile(String fileName) throws FileNotFoundException {
        super(fileName);
    }

}
