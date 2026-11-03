package model.files.mediaFiles;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class MyReportFile {
    public static final String REPORT_LOCATION = "test-data-files\\report.txt";
    private static MyReportFile instance;

    private MyReportFile() {
    }

    public static MyReportFile GetInstance() {
        if (instance == null) {
            instance = new MyReportFile();
        }
        return instance;
    }

    public void WriteFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(REPORT_LOCATION), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
