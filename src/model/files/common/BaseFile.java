package model.files.common;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class BaseFile<T> implements IFileMethods<T> {

    private String inputFileName;
    private String outputFileName;
    private File inputFile;
    private File outputFile;

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public BaseFile(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        try {
            ReadFile();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to read the input files and create an
     * output file pair if it doesn't exist on the disk yet.
     * @throws IOException
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

    public BufferedReader GetBufferedReader(){
        FileInputStream fluxIn = null;
        try {
            fluxIn = new FileInputStream(inputFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader isr = new InputStreamReader(fluxIn);
        BufferedReader br = new BufferedReader(isr);

        return br;
    }

    public BufferedWriter GetBufferedWriter(){
        FileOutputStream fluxOut = null;
        try {
            fluxOut = new FileOutputStream(outputFileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        OutputStreamWriter osr = new OutputStreamWriter(fluxOut);
        BufferedWriter br = new BufferedWriter(osr);

        return br;
    }

    public abstract HashMap<Integer,T> ParseFile();
    public abstract void WriteFile(Collection<T> objectsToWrite);
}
