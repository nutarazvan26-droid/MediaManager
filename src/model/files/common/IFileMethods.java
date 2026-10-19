package model.files.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileMethods<T> {
    void ReadFile() throws IOException;

    BufferedReader GetBufferedReader();
    BufferedWriter GetBufferedWriter();
}
