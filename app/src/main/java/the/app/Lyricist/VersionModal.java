package the.app.Lyricist;

public class VersionModal
{
    // Variables for our version
    private int id, versionSongId;
    private String versionName, versionDescription, versionLyrics, versionCreationDate, versionEditDate;

    /**
     * method used to return the version's int id.
     *
     * @return int id
     */
    // Getter methods
    public int getId() { return id; }

    /**
     * Method used to return the int id of the song this version belongs to.
     *
     * @return int song id.
     */
    public int getVersionSongId() { return versionSongId; }

    /**
     * Method used to return the string name of this version.
     *
     * @return string version name.
     */
    public String getVersionName() { return versionName; }

    /**
     * Method used to return the string description of this version.
     *
     * @return string version description.
     */
    public String getVersionDescription() { return versionDescription; }

    /**
     * Method used to return the string lyrics of this version.
     *
     * @return string version lyrics.
     */
    public String getVersionLyrics() { return versionLyrics; }

    /**
     * Method used to return the string create date of this version.
     *
     * @return string version creation date.
     */
    public String getVersionCreationDate()
    {
        return versionCreationDate;
    }

    /**
     * Method used to return the string last edit date of this version.
     *
     * @return string version last edit date.
     */
    public String getVersionEditDate() { return versionEditDate; }

    /**
     * Method used to set the local variable id.
     *
     * @param id int to be set.
     */
    // Setter methods
    public void setId(int id) { this.id = id; }

    /**
     * Method used to set the local variable description.
     *
     * @param versionDescription string description to be set.
     */
    public void setVersionDescription(String versionDescription) { this.versionDescription = versionDescription; }

    /**
     * Method used to set the local variable lyrics.
     *
     * @param versionLyrics string lyrics to be set.
     */
    public void setVersionLyrics(String versionLyrics) { this.versionLyrics = versionLyrics; }

    /**
     * Method used to create the version object and populate it with passed variables.
     *
     * @param id passed version int id.
     * @param versionSongId passed song int id.
     * @param versionName passed string version name.
     * @param versionCreationDate passed string creation date.
     * @param versionEditDate passed string last edit date.
     */
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
