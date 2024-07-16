package the.app.Lyricist;

public class SongModal
{
    // Variables for our Song
    private int id;
    private String songName, songCreationDate, songEditDate;

    // Getter methods
    public int getId() { return id; }

    public String getSongName() { return songName; }

    public String getSongCreationDate()
    {
        return songCreationDate;
    }

    public String getSongEditDate() { return songEditDate; }

    // Setter methods
    public void setId(int id) { this.id = id; }

    // Constructor
    public SongModal(String songName, String songCreationDate, String songEditDate)
    {
        this.songName = songName;
        this.songCreationDate = songCreationDate;
        this.songEditDate = songEditDate;
    }
}
