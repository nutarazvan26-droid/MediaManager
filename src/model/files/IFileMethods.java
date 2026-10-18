package model.files;

import java.io.FileNotFoundException;

public interface IFileMethods<T>{
    void ReadFile() throws FileNotFoundException;
}
