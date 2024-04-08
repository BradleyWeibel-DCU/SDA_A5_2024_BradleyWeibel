package com.example.sda_a5_2024_bradleyweibel;

public class VersionModal
{
    // Variables for our Song
    private int id, versionSongId;
    private String versionName, versionDescription, versionLyrics, versionCreationDate, versionEditDate;

    // Getter methods
    public int getId() { return id; }
    public int getVersionSongId() { return versionSongId; }
    public String getVersionName() { return versionName; }
    public String getVersionDescription() { return versionDescription; }
    public String getVersionLyrics() { return versionLyrics; }
    public String getVersionCreationDate()
    {
        return versionCreationDate;
    }
    public String getVersionEditDate() { return versionEditDate; }

    // Setter methods
    public void setId(int id) { this.id = id; }
    public void setVersionSongId(int versionSongId) { this.versionSongId = versionSongId; }
    public void setVersionName(String versionName) { this.versionName = versionName; }
    public void setVersionDescription(String versionDescription) { this.versionDescription = versionDescription; }
    public void setVersionLyrics(String versionLyrics) { this.versionLyrics = versionLyrics; }
    public void setVersionCreationDate(String versionCreationDate) { this.versionCreationDate = versionCreationDate; }
    public void setVersionEditDate(String versionEditDate) { this.versionEditDate = versionEditDate; }

    // Constructor
    public VersionModal(String versionName, String versionCreationDate, String versionEditDate)
    {
        this.versionName = versionName;
        this.versionCreationDate = versionCreationDate;
        this.versionEditDate = versionEditDate;
    }
}
