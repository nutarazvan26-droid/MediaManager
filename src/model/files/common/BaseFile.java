package model.files.common;

import exceptions.DataFileException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;

public abstract class BaseFile<T> implements IFileMethods<T> {

    private final String inputFileName;
    private final String outputFileName;
    private File inputFile;
    private File outputFile;

    public BaseFile(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * This method is used to read the input files and create an
     * output file pair if it doesn't exist on the disk yet.
     *
     * @throws IOException In the case the input file does not exist (FileNotFoundException)
     * or the output file cannot be created throws this exception.
     */
    @Override
    public void ReadFile() throws IOException {
        this.inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new FileNotFoundException();
        }
        this.outputFile = new File(outputFileName);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
    }

    public BufferedReader GetBufferedReader() throws FileNotFoundException {
        FileInputStream fluxIn = new FileInputStream(inputFileName);
        InputStreamReader isr = new InputStreamReader(fluxIn, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        return br;
    }

    public BufferedWriter GetBufferedWriter() throws FileNotFoundException {
        FileOutputStream fluxOut = new FileOutputStream(outputFileName);
        OutputStreamWriter osr = new OutputStreamWriter(fluxOut, StandardCharsets.UTF_8);
        BufferedWriter br = new BufferedWriter(osr);

        return br;
    }

    public abstract HashMap<Integer, T> ParseFile() throws DataFileException;

    public abstract void WriteFile(Collection<T> objectsToWrite) throws DataFileException;
}
