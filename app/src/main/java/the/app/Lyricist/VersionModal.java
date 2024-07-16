package the.app.Lyricist;

public class VersionModal
{
    // Variables for our version
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

    public void setVersionDescription(String versionDescription) { this.versionDescription = versionDescription; }

    public void setVersionLyrics(String versionLyrics) { this.versionLyrics = versionLyrics; }

    // Constructor
    public VersionModal(Integer id, Integer versionSongId, String versionName, String versionCreationDate, String versionEditDate)
    {
        this.id = id;
        this.versionSongId = versionSongId;
        this.versionName = versionName;
        this.versionCreationDate = versionCreationDate;
        this.versionEditDate = versionEditDate;
    }
}
