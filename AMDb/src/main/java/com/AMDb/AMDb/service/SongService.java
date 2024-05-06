package com.AMDb.AMDb.service;

import com.AMDb.AMDb.model.Song;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.AMDb.AMDb.service.TranslatorService.getMVRS;

@Service
public class SongService {
  Connection con = null;

  /**
   * Makes a call to the database to get matching song single value and multivalue attributes
   *
   * @param song - song object that holds user input
   * @return allSongResults - ArrayList<LinkedHashMap<String,String>> that holds all the song results
   */
  public ArrayList<LinkedHashMap<String, String>> retrieveSong(@RequestBody Song song) {

    //var for returned Arraylist holding all the LinkedHashMaps
    //each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
    ArrayList<LinkedHashMap<String, String>> allSongResults = new ArrayList<LinkedHashMap<String, String>>();

    //attempt to connect and execute select queries
    try {

      //variable that indicates whether there is a condition
      boolean hasCondition = false;

      //get all the conditions to check for Song
      String where = "";

      //check song_title
      //if getSong_Title is not empty
      if (song.getSong_Title() != null && song.getSong_Title() != "") {
        where += "Song_Title LIKE '%" + song.getSong_Title() + "%'";
        hasCondition = true;
      }

      //check genre
      //if getGenre is not an empty string and not null
      if (song.getGenre() != null && song.getGenre() != "") {
        //if where is not empty, we need to append AND at the front
        if (where != "") {
          where += " AND ";
        }
        where += "Genre LIKE '%" + song.getGenre() + "%'";
        hasCondition = true;
      }

      //check tempo
      if (song.getMinTempo() > 0) {
        //if where is not empty, we need to append AND at the front
        if (where != "") {
          where += " AND ";
        }
        where += "Tempo >= " + song.getMinTempo();
        hasCondition = true;
      }

      if (song.getMaxTempo() > 0) {
        //if where is not empty, we need to append AND at the front
        if (where != "") {
          where += " AND ";
        }
        where += "Tempo <= " + song.getMaxTempo();
        hasCondition = true;
      }

      //check length
      if (song.getSMinLength() > 0) {
        //if where is not empty, we need to append AND at the front
        if (where != "") {
          where += " AND ";
        }
        where += "Length >= " + song.getSMinLength();
        hasCondition = true;
      }

      if (song.getSMaxLength() > 0) {
        //if where is not empty, we need to append AND at the front
        if (where != "") {
          where += " AND ";
        }
        where += "Length <= " + song.getSMaxLength();
        hasCondition = true;
      }

      //check explicit
      //if the box is checked, it will only return explicit results.
      //if you dont, it will return both explicit and non explicit results
      if (song.getExplicit() != null && song.getExplicit() == true) {
        //if where is not empty, we need to append AND at the front
        if (where != "") {
          where += " AND ";
        }
        where += "Explicit = true";
        hasCondition = true;
      }


      // the user did not specify any conditions, so they will not get any results
      if (!hasCondition) {
        //allSongResults is empty, return an empty arraylist without connecting to the database
        return allSongResults;
      }

      //connect to the database
      con = DatabaseConnection.getDBConnection();

      //get the result set from the Song table
      ResultSet songRS = TranslatorService.getSVRS("Song", where);

      //process result set contents
      if (songRS != null) {

        //get Metadata about singleAttrs resultSet
        ResultSetMetaData svAttrMD = songRS.getMetaData();

        //get the number of columns in the result set
        int colCount = svAttrMD.getColumnCount();

        //while loop to move through each row
        while (songRS.next()) {

          //LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
          LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

          //iterate over the columns
          for (int i = 1; i < colCount + 1; i++) {

            //get the val stored in the col for the current row
            String colContent = songRS.getString(i);

            //each col name and val are stored as a pair in the result LinkedHashMap
            result.put(svAttrMD.getColumnName(i), colContent);
          }

          //Has FK Music_Release_ID. need to join with Music_Release table to get the name of the music release
          //or just query the Music_Release table to get the name of the release based on the FK result

          String musicReleaseName = "";

          //get the FK value from the hashmap
          String id = "" + result.get("Music_Release_ID");

          //use that value in the query
          PreparedStatement fk = con.prepareStatement("SELECT Release_Title " +
              "FROM Music_Release " +
              " WHERE Release_ID = " + id);

          //execute to get the name
          ResultSet fkRS = fk.executeQuery();

          if (fkRS != null) {

            //while loop to move through each row
            while (fkRS.next()) {

              musicReleaseName = fkRS.getString(1);

            }
          }

          //add the musicReleaseName to the hashmap
          result.put("Music_Release_Name", musicReleaseName);


          //each row also has MV attributes that are listed in other tables
          //Song only has one MV attribute: Artist (relationship located in Records table)
          String[] aResult = getMVRS(con, "Artist_Name", "Music_Artist MA", "Records R", "Artist_ID", "Artist_ID", "Song S", "Song_ID", "Song_ID", result.get("Song_ID"));
          result.put(aResult[0], aResult[1]);

          //add the result LinkedHashMap to the Arraylist
          allSongResults.add(result);
        }
      }
    } catch (SQLException e) {
      System.out.println("SQLException: " + e.getMessage());
    }

    return allSongResults;
  }
}
