package model.files.mediaFiles;

public enum MediaType {
    AUDIO("mp3", "wav"),
    IMAGE("jpg", "jpeg", "png"),
    VIDEO("mp4", "avi", "mkv");

    private String[] extensions;

    MediaType(String... extensions) {
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public static MediaType fromExtension(String extension) {
        for (MediaType type : MediaType.values()) {
            for (String ext : type.extensions) {
                if (ext.equalsIgnoreCase(extension)) {
                    return type;
                }
            }
        }
        return null;
    }
}
