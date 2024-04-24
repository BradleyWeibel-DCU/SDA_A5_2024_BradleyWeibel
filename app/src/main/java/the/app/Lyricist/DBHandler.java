package the.app.Lyricist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper
{
    // Constant variables for database
    private static final String DB_NAME = "lyricistDB";
    private static final int DB_VERSION = 1;

    // SONGS table
    private static final String SONGS_TABLE_NAME = "SONGS";
    private static final String SONGS_ID_COL = "ID";
    private static final String SONGS_NAME_COL = "NAME";
    private static final String SONGS_CREATION_DATE_COL = "CREATION_DATE";
    private static final String SONGS_EDIT_DATE_COL = "EDIT_DATE";

    // VERSION table
    private static final String VERSIONS_TABLE_NAME = "VERSIONS";
    private static final String VERSIONS_ID_COL = "ID";
    private static final String VERSIONS_SONG_ID_COL = "SONG_ID";
    private static final String VERSIONS_NAME_COL = "NAME";
    private static final String VERSIONS_DESCRIPTION_COL = "DESCRIPTION";
    private static final String VERSIONS_LYRICS_COL = "LYRICS";
    private static final String VERSIONS_CREATION_DATE_COL = "CREATION_DATE";
    private static final String VERSIONS_EDIT_DATE_COL = "EDIT_DATE";

    /**
     * Establishes the DB context by using the DB name and version
     *
     * @param context
     */
    // Constructor for database handler
    public DBHandler(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates the SQLite DB if not created yet.
     * Triggers at startup.
     *
     * @param db The database.
     */
    // Method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // SQLite query to create SONGS table
        String songsTableQuery = "CREATE TABLE " + SONGS_TABLE_NAME + " ("
                + SONGS_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SONGS_NAME_COL + " TEXT,"
                + SONGS_CREATION_DATE_COL + " TEXT,"
                + SONGS_EDIT_DATE_COL + " TEXT)";

        // Execute above sql query, create SONGS table
        db.execSQL(songsTableQuery);

        // SQLite query to create VERSIONS table
        String versionsTableQuery = "CREATE TABLE " + VERSIONS_TABLE_NAME + " ("
                + VERSIONS_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VERSIONS_SONG_ID_COL + " INTEGER,"
                + VERSIONS_NAME_COL + " TEXT,"
                + VERSIONS_DESCRIPTION_COL + " TEXT,"
                + VERSIONS_LYRICS_COL + " TEXT,"
                + VERSIONS_CREATION_DATE_COL + " TEXT,"
                + VERSIONS_EDIT_DATE_COL + " TEXT)";

        // Execute above sql query
        db.execSQL(versionsTableQuery);
    }


    /**
     * Adds a new song to the  song table in the database.
     *
     * @param songName
     * @param songCreationDate
     * @param songEditDate
     * @return
     */
    // -------------------------------- Adding entry to DB --------------------------------
    // Method to add new song to our SONGS table
    public Integer addNewSong(String songName, String songCreationDate, String songEditDate)
    {
        // Creating a variable for our sqlite database and calling writable method as we are writing data in our database
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating a variable for content values
        ContentValues values = new ContentValues();

        // Passing all values along with its key and value pair
        values.put(SONGS_NAME_COL, songName);
        values.put(SONGS_CREATION_DATE_COL, songCreationDate);
        values.put(SONGS_EDIT_DATE_COL, songEditDate);

        // Passing content values to SONGS table
        db.insert(SONGS_TABLE_NAME, null, values);

        // Closing our database after adding the song
        db.close();

        // GET newly create song's id
        return getSongId(songName);
    }

    /**
     * Adds a new version to the version table in the database.
     *
     * @param versionName
     * @param versionSongId
     * @param versionDescription
     * @param versionLyrics
     * @param versionCreationDate
     * @param versionEditDate
     * @return
     */
    // Method to add new song-version to our VERSIONS table
    public Integer addNewVersion(String versionName, Integer versionSongId, String versionDescription, String versionLyrics, String versionCreationDate, String versionEditDate)
    {
        // Creating a variable for our sqlite database and calling writable method as we are writing data in our database
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating a variable for content values
        ContentValues values = new ContentValues();

        // Passing all values along with its key and value pair
        values.put(VERSIONS_NAME_COL, versionName);
        values.put(VERSIONS_SONG_ID_COL, versionSongId);
        values.put(VERSIONS_DESCRIPTION_COL, versionDescription);
        values.put(VERSIONS_LYRICS_COL, versionLyrics);
        values.put(VERSIONS_CREATION_DATE_COL, versionCreationDate);
        values.put(VERSIONS_EDIT_DATE_COL, versionEditDate);

        // Passing content values to VERSIONS table
        db.insert(VERSIONS_TABLE_NAME, null, values);

        // Closing our database after adding the song
        db.close();

        return getVersionId(versionSongId, versionName);
    }


    /**
     * Returns a list of all songs in the DB.
     *
     * @return list of songs in List<SongModal> form
     */
    // -------------------------------- Reading from DB --------------------------------
    // Reading all songs
    public ArrayList<SongModal> readSongs()
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating a cursor with query to read songs data from database
        String searchQuery = "SELECT * FROM " + SONGS_TABLE_NAME + " ORDER BY " + SONGS_NAME_COL + " ASC";
        Cursor cursorSongs = db.rawQuery(searchQuery, null);

        ArrayList<SongModal> songModalArrayList = new ArrayList<>();

        // Moving our cursor to first position
        if (cursorSongs.moveToFirst())
        {
            do
            {
                // Adding the data from cursor to our array list
                songModalArrayList.add(new SongModal(
                        cursorSongs.getString(1), // song name
                        cursorSongs.getString(2), // creation date
                        cursorSongs.getString(3))); // edit date
            } while (cursorSongs.moveToNext());
            // Moving cursor to next entry
        }

        // Closing our cursor and returning our array list
        cursorSongs.close();
        // Closing our database after adding the song
        db.close();

        return songModalArrayList;
    }

    /**
     * Search for all mathing songs
     *
     * @param songName name or partial song name to search for
     * @return list of songs in List<SongModal> form
     */
    // Reading all matching songs
    public ArrayList<SongModal> readSongs(String songName)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // String handling in case of apostrophes - add escape to be searched in DB
        songName = songName.replace("'", "''");

        // Creating a cursor with query to read songs data from database
        String searchQuery = "SELECT * FROM " + SONGS_TABLE_NAME + " WHERE " + SONGS_NAME_COL + " LIKE ? ORDER BY " + SONGS_NAME_COL + " ASC";
        Cursor cursorSongs = db.rawQuery(searchQuery, new String[] {"%" + songName + "%"});

        ArrayList<SongModal> songModalArrayList = new ArrayList<>();

        // Moving our cursor to first position
        if (cursorSongs.moveToFirst())
        {
            do
            {
                // Adding the data from cursor to our array list
                songModalArrayList.add(new SongModal(
                        cursorSongs.getString(1), // song name
                        cursorSongs.getString(2), // creation date
                        cursorSongs.getString(3))); // edit date
            } while (cursorSongs.moveToNext());
            // Moving cursor to next entry
        }

        // Closing our cursor and returning our array list
        cursorSongs.close();
        // Closing our database after adding the song
        db.close();

        return songModalArrayList;
    }

    /**
     * Return all versions of this song
     *
     * @param songId Versions below to this song
     * @return list of versions in List<VersionModal> form
     */
    // Reading all song-versions
    public ArrayList<VersionModal> readVersions(Integer songId)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT * FROM " + VERSIONS_TABLE_NAME + " WHERE " + VERSIONS_SONG_ID_COL + " = " + songId;
        Cursor cursorVersions = db.rawQuery(searchQuery, null);

        ArrayList<VersionModal> versionModalArrayList = new ArrayList<>();

        // Moving our cursor to first position
        if (cursorVersions.moveToFirst())
        {
            do
            {
                // Adding the data from cursor to our array list
                versionModalArrayList.add(new VersionModal(
                        cursorVersions.getInt(0), // id
                        cursorVersions.getInt(1), // song id
                        cursorVersions.getString(2), // name
                        cursorVersions.getString(5), // creation date
                        cursorVersions.getString(6))); // edit date
            } while (cursorVersions.moveToNext());
            // Moving cursor to next entry
        }

        // Closing our cursor and returning our array list
        cursorVersions.close();
        // Closing our database after adding the song
        db.close();

        return versionModalArrayList;
    }

    /**
     * Return a particular song version from DB
     *
     * @param versionId version's id
     * @return version
     */
    // Get song version
    public VersionModal getSongVersion(Integer versionId)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT * FROM " + VERSIONS_TABLE_NAME + " WHERE " + VERSIONS_ID_COL + " = " + versionId;
        Cursor cursorVersion = db.rawQuery(searchQuery, null);

        VersionModal versionModal = null;

        // Moving our cursor to first position
        if (cursorVersion.moveToFirst())
        {
            // Adding the data from cursor to versionModal
            versionModal = new VersionModal(
                    cursorVersion.getInt(0), // id
                    cursorVersion.getInt(1), // song id
                    cursorVersion.getString(2), // name
                    cursorVersion.getString(5), // creation date
                    cursorVersion.getString(6)); // edit date
            versionModal.setVersionDescription(cursorVersion.getString(3));
            versionModal.setVersionLyrics(cursorVersion.getString(4));
        }

        // Closing our cursor and returning our array list
        cursorVersion.close();
        // Closing our database after adding the song
        db.close();

        return versionModal;
    }

    /**
     * Return the id of a song
     *
     * @param songName name of song to be searched for
     * @return integer id of song
     */
    // Getting a song's ID from DB
    public Integer getSongId(String songName)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // String handling in case of apostrophes - add escape to be searched in DB
        songName = songName.replace("'", "''");

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT " + SONGS_ID_COL + " FROM " + SONGS_TABLE_NAME + " WHERE " + SONGS_NAME_COL + " = '" + songName + "'";
        Cursor cursorSong = db.rawQuery(searchQuery, null);

        Integer songId = 0;

        if (cursorSong.moveToFirst()) {
            // Getting the song id
            songId = cursorSong.getInt(0);
        }

        // Closing our cursor and returning our array list
        cursorSong.close();
        // Closing our database after adding the song
        db.close();

        return songId;
    }

    /**
     * Return name of song
     *
     * @param songId if of song to search for
     * @return string name of the song
     */
    // Getting a song's name from DB
    public String getSongName(Integer songId)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT " + SONGS_NAME_COL + " FROM " + SONGS_TABLE_NAME + " WHERE " + SONGS_ID_COL + " = " + songId;
        Cursor cursorSong = db.rawQuery(searchQuery, null);

        String songName = null;

        if (cursorSong.moveToFirst()) {
            // Getting the song name
            songName = cursorSong.getString(0);
        }

        // Closing our cursor and returning our array list
        cursorSong.close();
        // Closing our database after adding the song
        db.close();

        return songName;
    }

    /**
     * Get the if of this version
     *
     * @param songId song id the version belongs to
     * @param versionName name of this version
     * @return integer id of version
     */
    // Getting a version's ID from DB
    public Integer getVersionId(Integer songId, String versionName)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT " + VERSIONS_ID_COL + " FROM " + VERSIONS_TABLE_NAME + " WHERE " + VERSIONS_SONG_ID_COL + " = " + songId + " AND " + VERSIONS_NAME_COL + " = '" + versionName + "'";
        Cursor cursorSong = db.rawQuery(searchQuery, null);

        Integer versionId = null;

        if (cursorSong.moveToFirst()) {
            // Getting the version id
            versionId = cursorSong.getInt(0);
        }

        // Closing our cursor and returning our array list
        cursorSong.close();
        // Closing our database after adding the song
        db.close();

        return versionId;
    }

    /**
     * Is this sing name unique in the DB
     *
     * @param songName string name of song
     * @return true or false
     */
    // Is song name unique
    public Boolean isSongNameUnique(String songName)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // String handling in case of apostrophes - add escape to be searched in DB
        songName = songName.replace("'", "''");

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT " + SONGS_ID_COL + " FROM " + SONGS_TABLE_NAME + " WHERE " + SONGS_NAME_COL + " = '" + songName + "'";
        Cursor cursorSong = db.rawQuery(searchQuery, null);

        Boolean songNameIsUnique = true;

        if (cursorSong.moveToFirst()) {
            // Getting the song id
            Integer songId = cursorSong.getInt(0);
            if (songId != 0 && songId != null)
                songNameIsUnique = false;
        }

        // Closing our cursor and returning our array list
        cursorSong.close();
        // Closing our database after adding the song
        db.close();

        return songNameIsUnique;
    }

    /**
     * Is version name unique in the context of this song's versions.
     *
     * @param songId if of song
     * @param versionName name of version
     * @return true or false
     */
    // Is version name unique in context of song's versions
    public Boolean isVersionNameUnique(Integer songId, String versionName)
    {
        // Creating a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();

        // String handling in case of apostrophes - add escape to be searched in DB
        versionName = versionName.replace("'", "''");

        // Creating a cursor with query to read versions data from database
        String searchQuery = "SELECT " + VERSIONS_ID_COL + " FROM " + VERSIONS_TABLE_NAME + " WHERE " + VERSIONS_SONG_ID_COL + " = " + songId + " AND " + VERSIONS_NAME_COL + " = '" + versionName + "'";
        Cursor cursorSong = db.rawQuery(searchQuery, null);

        Boolean versionNameIsUnique = true;

        if (cursorSong.moveToFirst()) {
            // Getting the version id
            Integer versionId = cursorSong.getInt(0);
            if (versionId != 0 && versionId != null)
                versionNameIsUnique = false;
        }

        // Closing our cursor and returning our array list
        cursorSong.close();
        // Closing our database after adding the song
        db.close();

        return versionNameIsUnique;
    }


    /**
     * Update the song in the DB
     *
     * @param songId id of song
     * @param songName new/old name of song
     * @param songEditDate date of edit
     */
    // -------------------------------- Updating entry in DB --------------------------------
    // Updating a song
    public void updateSong(Integer songId, String songName, String songEditDate) {
        // Calling a method to get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Passing all values along with its key and value pair
        values.put(SONGS_NAME_COL, songName);
        values.put(SONGS_EDIT_DATE_COL, songEditDate);

        // Calling a update method to update our SONGS table in database and passing our values and comparing it with id of our song
        db.update(SONGS_TABLE_NAME, values, SONGS_ID_COL + "=" + songId, null);
        db.close();
    }

    /**
     * Update the version
     *
     * @param versionId version id
     * @param versionName name of version
     * @param songName name of the song
     * @param versionDescription description of version
     * @param versionLyrics lyrics of the version
     * @param versionEditDate date of edit
     */
    // Updating a song-version
    public void updateVersion(Integer versionId, String versionName, String songName, String versionDescription, String versionLyrics, String versionEditDate)
    {
        // Calling a method to get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Passing all values along with its key and value pair
        values.put(VERSIONS_NAME_COL, versionName);
        values.put(VERSIONS_DESCRIPTION_COL, versionDescription);
        values.put(VERSIONS_LYRICS_COL, versionLyrics);
        values.put(SONGS_EDIT_DATE_COL, versionEditDate);

        // Calling a update method to update our VERSIONS table in database and passing our values and comparing it with id of our version
        db.update(VERSIONS_TABLE_NAME, values, VERSIONS_ID_COL + "=" + versionId,null);

        // Update modification date in song entry
        values = new ContentValues();

        // Passing all values along with its key and value pair
        values.put(SONGS_NAME_COL, songName);
        values.put(SONGS_EDIT_DATE_COL, versionEditDate);

        // Calling a update method to update our SONGS table in database and passing our values and comparing it with id of our song
        db.update(SONGS_TABLE_NAME, values, SONGS_NAME_COL + "='" + songName + "'", null);

        db.close();
    }


    /**
     * Delete song and all housed versions
     *
     * @param songId id of song
     */
    // -------------------------------- Deleting entry in DB --------------------------------
    // Deleting a song
    public void deleteSongAndVersions(Integer songId)
    {
        // Creating a variable to write our database
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete all versions where song id matches this song id
        db.delete(VERSIONS_TABLE_NAME, VERSIONS_SONG_ID_COL + "=" + songId, null);
        // Delete song where song id matches
        db.delete(SONGS_TABLE_NAME, SONGS_ID_COL + "=" + songId, null);
        db.close();
    }

    /**
     * Delete version of song
     *
     * @param versionId id of version
     */
    // Deleting a song-version
    public void deleteVersion(Integer versionId)
    {
        // Creating a variable to write our database
        SQLiteDatabase db = this.getWritableDatabase();

        // Calling a method to delete version and comparing it with version id
        db.delete(VERSIONS_TABLE_NAME, VERSIONS_ID_COL + "=" + versionId, null);
        db.close();
    }

    /**
     * Update version of DB.
     * Clears the DB
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Wipe and recreate DB
        db.execSQL("DROP TABLE IF EXISTS " + VERSIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SONGS_TABLE_NAME);
        onCreate(db);
    }
}
