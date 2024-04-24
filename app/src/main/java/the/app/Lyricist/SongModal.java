package the.app.Lyricist;

public class SongModal
{
    // Variables for our Song
    private int id;
    private String songName, songCreationDate, songEditDate;

    /**
     * @return returns the value of the int id of this song.
     */
    // Getter methods
    public int getId() { return id; }

    /**
     * @return returns this song's name in string format.
     */
    public String getSongName() { return songName; }

    /**
     * @return returns the song's creation date in string format.
     */
    public String getSongCreationDate()
    {
        return songCreationDate;
    }

    /**
     * @return returns the song's last edited date in string format.
     */
    public String getSongEditDate() { return songEditDate; }

    /**
     * @param id passed new id of the song.
     */
    // Setter methods
    public void setId(int id) { this.id = id; }

    /**
     * Method used to create a new song object with passed values.
     *
     * @param songName passed name of song.
     * @param songCreationDate passed string date of the song's creation.
     * @param songEditDate passed string last edit date of song.
     */
    // Constructor
    public SongModal(String songName, String songCreationDate, String songEditDate)
    {
        this.songName = songName;
        this.songCreationDate = songCreationDate;
        this.songEditDate = songEditDate;
    }
}
