package ui;

import exceptions.DuplicateLocationException;
import exceptions.InvalidFilterException;
import exceptions.InvalidLocationException;
import exceptions.InvalidOptionException;
import exceptions.LocationNotFoundException;
import i18n.Messages;
import model.files.locationModels.LocationModel;
import model.files.locationModels.MyLocationsHolder;
import model.files.mediaFiles.FileFilterType;
import model.files.mediaFiles.FileSortType;
import model.files.mediaFiles.MediaType;
import model.files.mediaFiles.MyFileModel;
import model.files.mediaFiles.MyFilesHolder;
import model.files.mediaFiles.MyReportFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Menu {
    private static final int OPTION_EXIT = 0;
    private static final int OPTION_LOCATIONS = 1;
    private static final int OPTION_FILES = 2;
    private static final int OPTION_REPORT = 3;
    private static final int OPTION_CHANGE_LANGUAGE = 4;
    private static final int OPTION_BACK = 0;
    private static final int OPTION_SHOW_LOCATIONS = 1;
    private static final int OPTION_ADD_LOCATION = 2;
    private static final int OPTION_DELETE_LOCATION = 3;
    private static final int OPTION_SHOW_FILES = 1;
    private static final int OPTION_FILTER_FILES = 2;
    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = KILOBYTE * 1024;
    private static final String INVALID_OPTION = "label.error.input.invalid-option";
    private static final String INDENT = "    ";
    private static final Messages messages = Messages.getInstance();
    private static Menu instance;
    private final Scanner scanner = new Scanner(System.in);

    private Menu() {
    }

    public static Menu GetInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int option = readOption(OPTION_EXIT, OPTION_CHANGE_LANGUAGE);
                switch (option) {
                    case OPTION_LOCATIONS:
                        locationsMenu();
                        break;
                    case OPTION_FILES:
                        filesMenu();
                        break;
                    case OPTION_REPORT:
                        showReport();
                        break;
                    case OPTION_CHANGE_LANGUAGE:
                        changeLanguage();
                        break;
                    case OPTION_EXIT:
                        running = false;
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println(messages.get("label.error.input.not-a-number"));
            } catch (InvalidOptionException e) {
                System.out.println(messages.get(e.getMessage()));
            } catch (NoSuchElementException e) {
                running = false;
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println(messages.get("label.menu.title"));
        System.out.println(OPTION_LOCATIONS + ". " + messages.get("label.menu.locations"));
        System.out.println(OPTION_FILES + ". " + messages.get("label.menu.files"));
        System.out.println(OPTION_REPORT + ". " + messages.get("label.menu.report"));
        System.out.println(OPTION_CHANGE_LANGUAGE + ". " + messages.get("label.menu.change-language"));
        System.out.println(OPTION_EXIT + ". " + messages.get("label.menu.exit"));
        System.out.print(messages.get("label.menu.choose-option") + ": ");
    }

    private void locationsMenu() {
        boolean inLocationsMenu = true;
        while (inLocationsMenu) {
            printLocationsMenu();
            try {
                int option = readOption(OPTION_BACK, OPTION_DELETE_LOCATION);
                switch (option) {
                    case OPTION_SHOW_LOCATIONS:
                        showLocations();
                        break;
                    case OPTION_ADD_LOCATION:
                        addLocation();
                        break;
                    case OPTION_DELETE_LOCATION:
                        deleteLocation();
                        break;
                    case OPTION_BACK:
                        inLocationsMenu = false;
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println(messages.get("label.error.input.not-a-number"));
            } catch (InvalidOptionException | InvalidLocationException | DuplicateLocationException | LocationNotFoundException e) {
                System.out.println(messages.get(e.getMessage()));
            }
        }
    }

    private void printLocationsMenu() {
        System.out.println();
        System.out.println(messages.get("label.menu.locations.title"));
        System.out.println(OPTION_SHOW_LOCATIONS + ". " + messages.get("label.menu.show-locations"));
        System.out.println(OPTION_ADD_LOCATION + ". " + messages.get("label.menu.add-location"));
        System.out.println(OPTION_DELETE_LOCATION + ". " + messages.get("label.menu.delete-location"));
        System.out.println(OPTION_BACK + ". " + messages.get("label.menu.back"));
        System.out.print(messages.get("label.menu.choose-option") + ": ");
    }

    private void filesMenu() {
        boolean inFilesMenu = true;
        while (inFilesMenu) {
            printFilesMenu();
            try {
                int option = readOption(OPTION_BACK, OPTION_FILTER_FILES);
                switch (option) {
                    case OPTION_SHOW_FILES:
                        showFiles();
                        break;
                    case OPTION_FILTER_FILES:
                        filterFiles();
                        break;
                    case OPTION_BACK:
                        inFilesMenu = false;
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println(messages.get("label.error.input.not-a-number"));
            } catch (InvalidOptionException | InvalidFilterException e) {
                System.out.println(messages.get(e.getMessage()));
            }
        }
    }

    private void printFilesMenu() {
        System.out.println();
        System.out.println(messages.get("label.menu.files.title"));
        System.out.println(OPTION_SHOW_FILES + ". " + messages.get("label.menu.show-files"));
        System.out.println(OPTION_FILTER_FILES + ". " + messages.get("label.menu.filter-files"));
        System.out.println(OPTION_BACK + ". " + messages.get("label.menu.back"));
        System.out.print(messages.get("label.menu.choose-option") + ": ");
    }

    private int readOption(int min, int max) throws InvalidOptionException {
        int option = Integer.parseInt(scanner.nextLine().trim());
        if (option < min || option > max) {
            throw new InvalidOptionException(INVALID_OPTION);
        }
        return option;
    }

    private void changeLanguage() throws InvalidOptionException {
        Locale[] locales = Messages.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            System.out.println((i + 1) + ". " + locales[i].getLanguage().toUpperCase());
        }
        System.out.print(messages.get("label.menu.choose-language") + ": ");

        int option = readOption(1, locales.length);
        messages.setLocale(locales[option - 1]);
        System.out.println(messages.get("label.menu.language-changed"));
    }

    private void showLocations() {
        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        HashMap<Integer, LocationModel> locations = locationsHolder.getLocations();
        if (locations == null || locations.isEmpty()) {
            System.out.println(messages.get("label.location.none"));
            return;
        }

        for (LocationModel location : locationsHolder.getRootLocations(locations.values())) {
            printLocation(location, "");
        }
    }

    private void printLocation(LocationModel location, String indent) {
        System.out.println(indent + formatLocation(location));

        for (LocationModel subLocation : location.getSubLocations()) {
            printLocation(subLocation, indent + INDENT);
        }
    }

    private String formatLocation(LocationModel location) {
        String type = messages.get("label.location.type." + location.getLocationType());
        return location.getName() +
                " [" + messages.get("label.location.id") + ": " + location.getId() +
                ", " + messages.get("label.location.path") + ": " + location.getPath() +
                ", " + messages.get("label.location.type") + ": " + type + "]";
    }

    private void addLocation() throws InvalidLocationException, DuplicateLocationException {
        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        System.out.print(messages.get("label.location.enter-path") + ": ");
        String path = locationsHolder.validatePath(scanner.nextLine().trim());

        System.out.print(messages.get("label.location.enter-name") + ": ");
        LocationModel location = locationsHolder.addLocation(path, scanner.nextLine().trim());

        System.out.println(messages.get("label.location.added"));
        printLocation(location, "");
    }

    private void deleteLocation() throws InvalidLocationException, LocationNotFoundException, InvalidOptionException {
        MyLocationsHolder locationsHolder = MyLocationsHolder.GetInstance();
        System.out.print(messages.get("label.location.enter-name-or-id") + ": ");
        List<LocationModel> matches = locationsHolder.findByNameOrId(scanner.nextLine().trim());

        LocationModel location = matches.size() == 1 ? matches.get(0) : chooseLocation(matches);
        List<Integer> deletedIds = locationsHolder.deleteLocation(location);
        int removedFiles = MyFilesHolder.GetInstance().removeFilesOfLocations(deletedIds);

        System.out.println(messages.get("label.location.deleted"));
        printLocation(location, "");
        System.out.println(messages.get("label.location.files-removed") + ": " + removedFiles);
    }

    private LocationModel chooseLocation(List<LocationModel> matches) throws InvalidOptionException {
        System.out.println(messages.get("label.location.several-matches") + ":");
        for (LocationModel location : matches) {
            System.out.println(INDENT + formatLocation(location));
        }
        System.out.print(messages.get("label.location.enter-id") + ": ");

        int id = Integer.parseInt(scanner.nextLine().trim());
        for (LocationModel location : matches) {
            if (location.getId() == id) {
                return location;
            }
        }
        throw new InvalidOptionException(INVALID_OPTION);
    }

    private void showFiles() throws InvalidOptionException {
        HashMap<Integer, MyFileModel> files = MyFilesHolder.GetInstance().getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println(messages.get("label.file.none"));
            return;
        }

        printFiles(files.values());
    }

    private void printFiles(Collection<MyFileModel> files) throws InvalidOptionException {
        FileSortType[] sortTypes = FileSortType.values();
        for (int i = 0; i < sortTypes.length; i++) {
            System.out.println((i + 1) + ". " + messages.get("label.file.sort." + sortTypes[i]));
        }
        System.out.print(messages.get("label.menu.choose-sort") + ": ");

        int option = readOption(1, sortTypes.length);
        List<MyFileModel> sortedFiles = new ArrayList<>(files);
        sortedFiles.sort(sortTypes[option - 1].getComparator());
        for (MyFileModel file : sortedFiles) {
            System.out.println(formatFile(file));
        }
    }

    private void filterFiles() throws InvalidOptionException, InvalidFilterException {
        MyFilesHolder filesHolder = MyFilesHolder.GetInstance();
        HashMap<Integer, MyFileModel> files = filesHolder.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println(messages.get("label.file.none"));
            return;
        }

        FileFilterType[] filterTypes = FileFilterType.values();
        for (int i = 0; i < filterTypes.length; i++) {
            System.out.println((i + 1) + ". " + messages.get("label.file.filter." + filterTypes[i]));
        }
        System.out.print(messages.get("label.menu.choose-filter") + ": ");

        int option = readOption(1, filterTypes.length);
        FileFilterType filterType = filterTypes[option - 1];
        String value;
        if (filterType == FileFilterType.TYPE) {
            value = chooseMediaType().name();
        } else {
            System.out.print(messages.get("label.file.enter." + filterType) + ": ");
            value = scanner.nextLine().trim();
        }

        List<MyFileModel> filteredFiles = filesHolder.filterFiles(filterType, value);
        if (filteredFiles.isEmpty()) {
            System.out.println(messages.get("label.file.no-match"));
            return;
        }
        printFiles(filteredFiles);
    }

    private MediaType chooseMediaType() throws InvalidOptionException {
        MediaType[] mediaTypes = MediaType.values();
        for (int i = 0; i < mediaTypes.length; i++) {
            System.out.println((i + 1) + ". " + messages.get("label.file.type." + mediaTypes[i]));
        }
        System.out.print(messages.get("label.menu.choose-type") + ": ");

        int option = readOption(1, mediaTypes.length);
        return mediaTypes[option - 1];
    }

    private void showReport() {
        MyFilesHolder filesHolder = MyFilesHolder.GetInstance();
        HashMap<Integer, MyFileModel> files = filesHolder.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println(messages.get("label.file.none"));
            return;
        }

        List<String> lines = buildReport(filesHolder);
        for (String line : lines) {
            System.out.println(line);
        }

        try {
            MyReportFile.GetInstance().WriteFile(lines);
            System.out.println(messages.get("label.report.saved") + " " + MyReportFile.REPORT_LOCATION);
        } catch (IOException e) {
            System.out.println(messages.get("label.error.report.not-saved"));
        }
    }

    private List<String> buildReport(MyFilesHolder filesHolder) {
        MediaType[] mediaTypes = MediaType.values();
        int[] filesCountByType = filesHolder.countFilesByType();
        long[] sizesByType = filesHolder.sumSizesByType();
        int totalFiles = 0;
        long totalSize = 0;
        for (int i = 0; i < mediaTypes.length; i++) {
            totalFiles += filesCountByType[i];
            totalSize += sizesByType[i];
        }

        List<String> lines = new ArrayList<>();
        lines.add(messages.get("label.report.title") + " - " + new SimpleDateFormat(MyFilesHolder.DATE_FORMAT).format(new Date()));
        lines.add(messages.get("label.report.total") + ": " + formatFileCount(totalFiles) + ", " + formatSize(totalSize));
        for (int i = 0; i < mediaTypes.length; i++) {
            lines.add(messages.get("label.file.type." + mediaTypes[i]) + ": " + formatFileCount(filesCountByType[i]) + ", " + formatSize(sizesByType[i]));
        }

        List<String> extensions = MediaType.getAllExtensions();
        int[] filesCountByExtension = filesHolder.countFilesByExtension();
        for (int i = 0; i < extensions.size(); i++) {
            lines.add(extensions.get(i) + ": " + formatFileCount(filesCountByExtension[i]));
        }

        lines.add(messages.get("label.report.largest-file") + ": " + formatFile(filesHolder.findLargestFile()));
        return lines;
    }

    private String formatFile(MyFileModel file) {
        String type = messages.get("label.file.type." + file.getType());
        String locationPath = MyLocationsHolder.GetInstance().getLocations().get(file.getLocationId()).getPath();
        String lastModified = new SimpleDateFormat(MyFilesHolder.DATE_FORMAT).format(file.getLastModified());
        return file.getName() +
                " [" + messages.get("label.file.id") + ": " + file.getId() +
                ", " + messages.get("label.file.extension") + ": " + file.getExtension() +
                ", " + messages.get("label.file.type") + ": " + type +
                ", " + messages.get("label.file.size") + ": " + formatSize(file.getSizeBytes()) +
                ", " + messages.get("label.file.location") + ": " + locationPath +
                ", " + messages.get("label.file.last-modified") + ": " + lastModified +
                ", " + messages.get("label.file.path") + ": " + file.getPath() + "]";
    }

    private String formatSize(long sizeBytes) {
        if (sizeBytes < KILOBYTE) {
            return sizeBytes + " B";
        }
        if (sizeBytes < MEGABYTE) {
            return String.format(Locale.ROOT, "%.1f KB", (double) sizeBytes / KILOBYTE);
        }
        return String.format(Locale.ROOT, "%.1f MB", (double) sizeBytes / MEGABYTE);
    }

    private String formatFileCount(int filesCount) {
        String label = filesCount == 1 ? "label.report.file" : "label.report.files";
        return filesCount + " " + messages.get(label);
    }
}
