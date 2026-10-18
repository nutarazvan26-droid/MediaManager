package model.files;

import java.io.*;

public abstract class BaseFile<T> implements IFileMethods<T>{

    private String fileName;
    private File file;


    public BaseFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void ReadFile() throws FileNotFoundException {
        this.file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
    }

    public abstract void ParseFile();
}
