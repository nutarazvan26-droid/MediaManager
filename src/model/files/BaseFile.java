package model.files;

import java.io.*;
import java.util.HashMap;

public abstract class BaseFile<T> implements IFileMethods<T>{

    private String fileName;
    private File file;

    public String getFileName() {
        return fileName;
    }

    public BaseFile(String fileName) {
        this.fileName = fileName;
        try {
            ReadFile();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void ReadFile() throws FileNotFoundException {
        this.file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
    }

    public BufferedReader GetBR(){
        FileInputStream fluxIn = null;
        try {
            fluxIn = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader isr = new InputStreamReader(fluxIn);
        BufferedReader br = new BufferedReader(isr);

        return br;
    }

    public abstract HashMap<Integer,? extends CommonFile> ParseFile();
}
