package net.alliancecraft.AllianceBotWebApi.TicketControllers;

public class FileInfo {
    private String name;
    private String directory;
    private String path;

    // Construtores, getters e setters
    public FileInfo(String name, String path, String directory) {
        this.name = name;
        this.path = path;
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

