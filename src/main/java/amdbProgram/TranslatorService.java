
package amdbProgram;

import java.sql.*;
import modelAttributes.*;
import java.util.*;

/**
 * TranslatorService class
 * Contains methods that take in a model object and uses its attributes to make sql queries
 */
public class TranslatorService {

	//variable to hold connection info
	Connection con = null;

	//vars for prepared sql statements
	PreparedStatement getSVAttrs = null;
	PreparedStatement getMVAttrs = null;

	// ***************************** MUSIC RELATED ENTITIES ***************************************

	/**
	 * Makes a call to the database to get matching song single value and multivalue attributes
	 * @param song - song object that holds user input
	 * @return allSongResults - ArrayList<LinkedHashMap<String,String>> that holds all the song results
	 */
	public ArrayList<LinkedHashMap<String,String>> retrieveSong(Song song){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allSongResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable that indicates whether there is a condition
			boolean hasCondition = false;

			//get all the conditions to check for Song
			String where = "";

			//check song_title
			//if getSong_Title is not empty
			if(song.getSong_Title() != null && song.getSong_Title().trim() != ""){
				where += "Song_Title LIKE '%" + song.getSong_Title() + "%'";
				hasCondition = true;
			}
			
			//check genre
			//if getGenre is not an empty string and not null
			if (song.getGenre() != null && song.getGenre().trim() != "") {
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Genre LIKE '%" + song.getGenre() + "%'";
				hasCondition = true;
			}
			
			//check tempo
			if(song.getMinTempo() > 0){
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Tempo >= " + song.getMinTempo();
				hasCondition = true;
			}

			if(song.getMaxTempo() > 0){
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Tempo <= " + song.getMaxTempo();
				hasCondition = true;
			}

			//check length
			if(song.getSMinLength() > 0){
				//user enters the length in minutes, so we need to convert to milliseconds
				int sMinLengthmilli = song.getSMinLength() * 60000;

				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Length >= " + sMinLengthmilli;
				hasCondition = true;
			}

			if(song.getSMaxLength() > 0){
				//use enters the length in minutes, so we need to convert to milliseconds
				int sMaxLengthmilli = song.getSMaxLength() * 60000;

				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Length <= " + sMaxLengthmilli;
				hasCondition = true;
			}

			//check explicit
			//if the box is checked, it will only return explicit results.
			//if you dont, it will return both explicit and non explicit results
			if(song.getExplicit() != null && song.getExplicit() == true){
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Explicit = true";
				hasCondition = true;
			}


			// the user did not specify any conditions, so they will not get any results
			if (hasCondition == false){
				//allSongResults is empty, return an empty arraylist without connecting to the database
				return allSongResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//get the result set from the Song table
			ResultSet songRS = getSVRS("Song", where);

			//process result set contents
			if (songRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = songRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(songRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = songRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}

					//the database stores the song's length in milliseconds
					//but we want to display the song's length in Xm Ys Zms format 

					//use parseInt to turn the String containing the int into an actual int
					int lengthInMilli = Integer.parseInt(result.get("Length"));

					//divide the integer by 60000 to get the length in minutes, rounded down
					int lengthMinutes = lengthInMilli / 60000;

					//subtract the minutes from the milliseconds
					int milliMinusMinutes = lengthInMilli - (lengthMinutes * 60000);

					//divide the remaining milliseconds by 1000 to get the leftover seconds, rounded down
					int leftoverSeconds = milliMinusMinutes / 1000;

					//subtract the seconds from the remaining milliseconds. this is the leftover milliseconds
					int leftoverMilli = milliMinusMinutes - (leftoverSeconds * 1000);

					//replace the length in milliseconds with the Xm Ys Zms format
					result.replace("Length", "" + lengthMinutes + "m " + leftoverSeconds + "s " + leftoverMilli + "ms");

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

					if (fkRS != null){
				
						//while loop to move through each row
						while(fkRS.next()){
							
							musicReleaseName = fkRS.getString(1);
		
						}
					}
					
					//add the musicReleaseName to the hashmap
					result.put("Music_Release_Name", musicReleaseName);

				
					//each row also has MV attributes that are listed in other tables
					//Song only has one MV attribute: Artist (relationship located in Records table)
					String[] aResult = getMVRS(con, "Artist_Name", "Music_Artist MA", "Records R", "Artist_ID", "Artist_ID", "Song S", "Song_ID", "Song_ID", result.get("Song_ID"));
					result.put(aResult[0],aResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allSongResults.add(result);
				}
			}
		}
		catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}

		return allSongResults;
	}

	/**
	 * Makes a call to the database to get matching Record Label single value and multivalue attributes
	 * @param recordLabel - recordLabel object that holds user input
	 * @return allRecordLabelResults - ArrayList<LinkedHashMap<String,String>> that holds all the record label results
	 */
	public ArrayList<LinkedHashMap<String,String>> retrieveRecordLabel(RecordLabel recordLabel){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allRecordLabelResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable that indicates whether there is a condition
			boolean hasCondition = false;

			//get all the conditions to check for RecordLabel
			String where = "";

			//check label_name
			//if label_name is not empty
			if(recordLabel.getLabel_Name() != null && recordLabel.getLabel_Name().trim() != ""){
				where += "Label_Name LIKE '%" + recordLabel.getLabel_Name() + "%'";
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allRecordLabelResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();
			
			//store single value attributes for recordlabel
			ResultSet recordLabelRS = getSVRS("Record_Label", where);

			//process result set contents
			if (recordLabelRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = recordLabelRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(recordLabelRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = recordLabelRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Record_Label has two MV attributes: Music_Release (relationship located in Releases table), Artist (relationship located in Signs table)
					String[] mrResult = getMVRS(con, "Release_Title", "Music_Release MR", "Releases R", "Release_ID", "Release_ID", "Record_Label RL", "Label_ID", "Label_ID", result.get("Label_ID"));
					result.put(mrResult[0],mrResult[1]);
					String[] aResult = getMVRS(con, "Artist_Name", "Music_Artist MA", "Signs S", "Artist_ID", "Artist_ID", "Record_Label RL", "Label_ID", "Label_ID", result.get("Label_ID"));
					result.put(aResult[0],aResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allRecordLabelResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allRecordLabelResults;
	}

	/**
	 * Makes a call to the database to get matching Music Artist single value and multivalue attributes
	 * @param musicArtist - musicArtist object that holds user input
	 * @return allMusicArtistResults - ArrayList<LinkedHashMap<String,String>> that holds all the music artist results
	 */
	public ArrayList<LinkedHashMap<String,String>> retrieveMusicArtist(MusicArtist musicArtist){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allMusicArtistResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for musicArtist
			String where = "";

			//check Artist_Name
			//if Artist_Name is not empty
			if(musicArtist.getArtist_Name() != null && musicArtist.getArtist_Name().trim() != ""){
				where += "Artist_Name LIKE '%" + musicArtist.getArtist_Name() + "%'";				
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allMusicArtistResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for musicArtist
			ResultSet musicArtistRS = getSVRS("Music_Artist", where);

			//process result set contents
			if (musicArtistRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = musicArtistRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(musicArtistRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = musicArtistRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Music_Artist has three MV attributes: Record_Label (relationship located in Signs table), Music_Release (relationship located in Creates table), Song (relationship located in Records table)
					String[] rlResult = getMVRS(con, "Label_Name", "Record_Label RL", "Signs S", "Label_ID", "Label_ID", "Music_Artist MA", "Artist_ID", "Artist_ID", result.get("Artist_ID"));
					result.put(rlResult[0],rlResult[1]);
					String[] mrResult = getMVRS(con, "Release_Title", "Music_Release MR", "Creates C", "Release_ID", "Release_ID", "Music_Artist MA", "Artist_ID", "Artist_ID", result.get("Artist_ID"));
					result.put(mrResult[0],mrResult[1]);
					String[] sResult = getMVRS(con, "Song_Title", "Song S", "Records R", "Song_ID", "Song_ID", "Music_Artist MA", "Artist_ID", "Artist_ID", result.get("Artist_ID"));
					result.put(sResult[0],sResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allMusicArtistResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allMusicArtistResults;
	}


	/**
	* Makes a call to the database to get matching Music Release single value and multivalue attributes
	* @param musicRelease - musicArtist object that holds user input
	* @return allMusicReleaseResults - ArrayList<LinkedHashMap<String,String>> that holds all the music release results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrieveMusicRelease(MusicRelease musicRelease){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allMusicReleaseResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for MusicRelease
			String where = "";

			//check Title
			//if Title is not empty
			if(musicRelease.getRelease_Title() != null && musicRelease.getRelease_Title().trim() != ""){
				where += "Release_Title LIKE '%" + musicRelease.getRelease_Title() + "%'";			
				hasCondition = true;
			}

			//if theres no condition inputted, returne empty arraylist
			if (hasCondition == false){
				return allMusicReleaseResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for musicRelease
			ResultSet musicReleaseRS = getSVRS("Music_Release", where);

			//process result set contents
			if (musicReleaseRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = musicReleaseRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(musicReleaseRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = musicReleaseRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Music_Release has two MV attributes: Record_Label (relationship in Releases table), ARtist (relationship located in Creates table)
					String[] rlResult = getMVRS(con, "Label_Name", "Record_Label RL", "Releases R", "Label_ID", "Label_ID", "Music_Release MR", "Release_ID", "Release_ID", result.get("Release_ID"));
					result.put(rlResult[0],rlResult[1]);
					String[] aResult = getMVRS(con, "Artist_Name", "Music_Artist MA", "Creates C", "Artist_ID", "Artist_ID", "Music_Release MR", "Release_ID", "Release_ID", result.get("Release_ID"));
					result.put(aResult[0],aResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allMusicReleaseResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allMusicReleaseResults;
	}

	// *************************** PODCAST RELATED ENTITIES *********************************

	/**
	* Makes a call to the database to get matching Podcast Host single value and multivalue attributes
	* @param podcastHost - podcastHost object that holds user input
	* @return allPodcastHostResults - ArrayList<LinkedHashMap<String,String>> that holds all the podcast host results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrievePodcastHost(PodcastHost podcastHost){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allPodcastHostResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for PodcastHost
			String where = "";

			//check Host_Name
			//if Host_Name is not empty
			if(podcastHost.getHost_Name() != null && podcastHost.getHost_Name().trim() != ""){
				where += "Host_Name LIKE '%" + podcastHost.getHost_Name() + "%'";				
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allPodcastHostResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for podcastHost
			ResultSet podcastHostRS = getSVRS("Podcast_Host", where);

			//process result set contents
			if (podcastHostRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = podcastHostRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(podcastHostRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = podcastHostRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Podcast_Host has one MV attribute: Podcast (relationship in Hosts Table)
					String[] pResult = getMVRS(con, "Podcast_Title", "Podcast P", "Hosts H", "Podcast_ID", "Podcast_ID", "Podcast_Host PH", "Host_ID", "Host_ID", result.get("Host_ID"));
					result.put(pResult[0],pResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allPodcastHostResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allPodcastHostResults;
	}

	/**
	* Makes a call to the database to get matching Podcast single value and multivalue attributes
	* @param podcast - Podcast object that holds user input
	* @return allPodcastResults - ArrayList<LinkedHashMap<String,String>> that holds all the podcast results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrievePodcast(Podcast podcast){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allPodcastResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for Podcast
			String where = "";

			//check podcast_title
			//if podcast_title is not empty
			if(podcast.getPodcast_Title() != null && podcast.getPodcast_Title().trim() != ""){
				where += "Podcast_Title LIKE '%" + podcast.getPodcast_Title() + "%'";				
				hasCondition = true;
			}

			//check explicit
			//if the box is checked, it will only return explicit results.
			//if you dont, it will return both explicit and non explicit results
			if(podcast.getPExplicit() != null && podcast.getPExplicit() == true){
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Explicit = true";
				hasCondition = true;
			}

			//if no condition given, return empty arraylist
			if(!hasCondition){
				return allPodcastResults;
			}
			
			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for podcast
			ResultSet podcastRS = getSVRS("Podcast", where);

			//process result set contents
			if (podcastRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = podcastRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(podcastRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = podcastRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Podcast has one MV attribute: Podcast_Host (relationship in Hosts Table)
					String[] phResult = getMVRS(con, "Host_Name", "Podcast_Host PH", "Hosts H", "Host_ID", "Host_ID", "Podcast P", "Podcast_ID", "Podcast_ID", result.get("Podcast_ID"));
					result.put(phResult[0],phResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allPodcastResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allPodcastResults;
	}

	/**
	* Makes a call to the database to get matching Podcast Episode single value and multivalue attributes
	* @param podcastEpisode - podcastEpisode object that holds user input
	* @return allPodcastEpisodeResults - ArrayList<LinkedHashMap<String,String>> that holds all the podcast episode results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrievePodcastEpisode(PodcastEpisode podcastEpisode){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allPodcastEpisodeResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for PodcastEpisode
			String where = "";

			//check podcast_episode_title
			//if podcast_episode_title is not empty
			if(podcastEpisode.getPodcast_Episode_Title() != null && podcastEpisode.getPodcast_Episode_Title().trim() != ""){
				where += "Podcast_Episode_Title LIKE '%" + podcastEpisode.getPodcast_Episode_Title() + "%'";				
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allPodcastEpisodeResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for podcast
			ResultSet podcastEpisodeRS = getSVRS("Podcast_Episode", where);

			//process result set contents
			if (podcastEpisodeRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = podcastEpisodeRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(podcastEpisodeRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = podcastEpisodeRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					

					//Podcast_Episode has a FK with Podcast_ID

					String podcastName = "";
					//get the FK value from the hashmap
					String id = "" + result.get("Podcast_ID");

					//use that value in the query
					PreparedStatement fk = con.prepareStatement("SELECT Podcast_Title " + 
												"FROM Podcast " +
												" WHERE Podcast_ID = " + id);

					//execute to get the name
					ResultSet fkRS = fk.executeQuery();

					if (fkRS != null){
				
						//while loop to move through each row
						while(fkRS.next()){
							
							podcastName = fkRS.getString(1);
		
						}
					}
					
					//add the podcastName to the hashmap
					result.put("Podcast_Name", podcastName);


					//add the result LinkedHashMap to the Arraylist
					allPodcastEpisodeResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allPodcastEpisodeResults;
	}

	// ****************** AUDIOBOOK RELATED ENTITIES **********************

	/**
	* Makes a call to the database to get matching Publisher single value and multivalue attributes
	* @param publisher - publisher object that holds user input
	* @return allPublisherResults - ArrayList<LinkedHashMap<String,String>> that holds all the publisher results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrievePublisher(Publisher publisher){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allPublisherResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for Publisher
			String where = "";

			//check Publisher_Name
			//if Publisher_Name is not empty
			if(publisher.getPublisher_Name() != null && publisher.getPublisher_Name().trim() != ""){
				where += "Publisher_Name LIKE '%" + publisher.getPublisher_Name() + "%'";				
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allPublisherResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for publisher
			ResultSet publisherRS = getSVRS("Publisher", where);

			//process result set contents
			if (publisherRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = publisherRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(publisherRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = publisherRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Publisher has one MV: Book (relationship located in Publishes table)
					String[] bResult = getMVRS(con, "Book_Title", "Audio_Book AB", "Publishes P", "Book_ID", "Book_ID", "Publisher PR", "Publisher_ID", "Publisher_ID", result.get("Publisher_ID"));
					result.put(bResult[0],bResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allPublisherResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allPublisherResults;
	}

	/**
	* Makes a call to the database to get matching Audio Book single value and multivalue attributes
	* @param audioBook - audioBook object that holds user input
	* @return allAudioBookResults - ArrayList<LinkedHashMap<String,String>> that holds all the audioBook results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrieveAudioBook(AudioBook audioBook){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allAudioBookResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for audiobook
			String where = "";

			//check Title
			//if Title is not an empty string and if its not null
			if(audioBook.getBook_Title() != null &&  audioBook.getBook_Title().trim() != ""){
				where += "Book_Title LIKE '%" + audioBook.getBook_Title() + "%'";		
				System.out.println("audiobook title: " + audioBook.getBook_Title());	
				hasCondition = true;
			}

			//check lengthh
			if(audioBook.getMinLength() > 0){
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Length >= " + audioBook.getMinLength();
				hasCondition = true;
			}

			if(audioBook.getMaxLength() > 0){
				//if where is not empty, we need to append AND at the front
				if (where != ""){
					where += " AND ";
				}
				where += "Length <= " + audioBook.getMaxLength();
				hasCondition = true;
			}

			//check releasedate
			//if Release_Date is not an empty string and if its not null
			//checks if the book was released after or equal to the given date (given as 'YYYY-MM-DD')
			if(audioBook.getRelease_Date() != null &&  audioBook.getRelease_Date().trim() != ""){
				where += "Release_Date >= '" + audioBook.getRelease_Date() + "'";			
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allAudioBookResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for audiobook
			ResultSet audioBookRS = getSVRS("Audio_Book", where);

			//process result set contents
			if (audioBookRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = audioBookRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(audioBookRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = audioBookRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Audio_Book has 3 MV attributes: Publisher (relationship located in Publishes), Author (relationsihp located in Writes), Narrator (relationship located in Narrates)
					String[] pResult = getMVRS(con, "Publisher_Name", "Publisher PR", "Publishes P", "Publisher_ID", "Publisher_ID", "Audio_Book AB", "Book_ID", "Book_ID", result.get("Book_ID"));
					result.put(pResult[0],pResult[1]);
					String[] aResult = getMVRS(con, "Author_Name", "Author A", "Writes W", "Author_ID", "Author_ID", "Audio_Book AB", "Book_ID", "Book_ID", result.get("Book_ID"));
					result.put(aResult[0],aResult[1]);
					String[] nResult = getMVRS(con, "Narrator_Name", "Narrator N", "Narrates NS", "Narrator_ID", "Narrator_ID", "Audio_Book AB", "Book_ID", "Book_ID", result.get("Book_ID"));
					result.put(nResult[0],nResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allAudioBookResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allAudioBookResults;
	}

	/**
	* Makes a call to the database to get matching Author single value and multivalue attributes
	* @param author - author object that holds user input
	* @return allAuthorResults - ArrayList<LinkedHashMap<String,String>> that holds all the author results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrieveAuthor(Author author){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allAuthorResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for author
			String where = "";

			//check Author_Name
			//if Author_Name is not an empty string and if its not null
			if(author.getAuthor_Name() != null && author.getAuthor_Name().trim() != ""){
				where += "Author_Name LIKE '%" + author.getAuthor_Name() + "%'";				
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allAuthorResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for author
			ResultSet authorRS = getSVRS("Author", where);

			//process result set contents
			if (authorRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = authorRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(authorRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = authorRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//Author has 1 MV attribute: Audio_Book (relationship located in Writes table)
					String[] abResult = getMVRS(con, "Book_Title", "Audio_Book AB", "Writes W", "Book_ID", "Book_ID", "Author A", "Author_ID", "Author_ID", result.get("Author_ID"));
					result.put(abResult[0],abResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allAuthorResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allAuthorResults;
	}

	/**
	* Makes a call to the database to get matching Narrator single value and multivalue attributes
	* @param narrator - narrator object that holds user input
	* @return allNarratorResults - ArrayList<LinkedHashMap<String,String>> that holds all the narrator results
	*/
	public ArrayList<LinkedHashMap<String,String>> retrieveNarrator(Narrator narrator){

		//var for returned Arraylist holding all the LinkedHashMaps
		//each LinkedHashMap contains a single result (containing its attributes) to be displayed to the user
		ArrayList<LinkedHashMap<String,String>> allNarratorResults = new ArrayList<LinkedHashMap<String,String>>();

		//attempt to connect and execute select queries
		try{

			//variable to hold whether user inputted a condition
			boolean hasCondition = false;

			//get all the conditions to check for narrator
			String where = "";

			//check Narrator_Name
			//if Narrator_Name is not an empty string and if its not null
			if(narrator.getNarrator_Name() != null && narrator.getNarrator_Name().trim() != ""){
				where += "Narrator_Name LIKE '%" + narrator.getNarrator_Name() + "%'";			
				hasCondition = true;
			}

			//if theres no condition given, return empty
			if (hasCondition == false){
				return allNarratorResults;
			}

			//connect to the database
			con = DatabaseConnection.getDBConnection();

			//store single value attributes for narrator
			ResultSet narratorRS = getSVRS("Narrator", where);

			//process result set contents
			if (narratorRS != null){

				//get Metadata about singleAttrs resultSet
				ResultSetMetaData svAttrMD = narratorRS.getMetaData();

				//get the number of columns in the result set
				int colCount = svAttrMD.getColumnCount();

				//while loop to move through each row
				while(narratorRS.next()){

					//LinkedHashMap to represent the entry's entire attributes (sv and mv attributes)
					LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

					//iterate over the columns
					for(int i = 1; i<colCount+1;i++){

						//get the val stored in the col for the current row
						String colContent = narratorRS.getString(i);

						//each col name and val are stored as a pair in the result LinkedHashMap
						result.put(svAttrMD.getColumnName(i), colContent);
					}
					
					//each row also has MV attributes that are listed in other tables
					//NArrator has 1 MV attribute: Audio_Book (relationship located in Narrates table)
					String[] abResult = getMVRS(con, "Book_Title", "Audio_Book AB", "Narrates NS", "Book_ID", "Book_ID", "Narrator N", "Narrator_ID", "Narrator_ID", result.get("Narrator_ID"));
					result.put(abResult[0],abResult[1]);

					//add the result LinkedHashMap to the Arraylist
					allNarratorResults.add(result);
				}
			}
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}

		return allNarratorResults;
	}


	/**
	 * method to get the single values attributes from table
	 * @param from - table to get attributes from
	 * @param where - conditions to filter table
	 * @return svAttrRS - ResultSet 
	 */
	public ResultSet getSVRS(String from, String where){
		ResultSet svAttrRS = null;
		try{
			getSVAttrs = con.prepareStatement("SELECT * " + 
												"FROM " + from +
												" WHERE " + where);

			//execute the select statement and store resultSet into singleAttrsResult
			svAttrRS = getSVAttrs.executeQuery();
		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}
		return svAttrRS;
		
	}

	/**
	* Makes a call to the database to get the multivalued attributes and concatenate them to a string
	* @param con - database connection
	* @param mvAttrCol - name of the column that has the mv attribute
	* @param mvAttrTable - name of the table that hold the name of the mv attribute you want to get
	* @param relationship - name of the table that holds the relationship
	* @param id1 - mvAttrTable id
	* @param id2 - relationship table id that matches mvAttrTable's id
	* @param oriTable - table of the entity that's making the call
	* @param id3 - oriTable id
	* @param id4 - relationship table id that matches oriTable's id
	* @param idVal - 
	* @return result - String array of length 2 that contains the key and mv attributes concatenated to a string
	*/
	public String[] getMVRS(Connection con, String mvAttrCol, String mvAttrTable, String relationship, String id1, String id2, String oriTable, String id3, String id4, String idVal){
		
		//result set to hold the mv results
		ResultSet mvAttrRS = null;

		//string array to hold the attr name and string
		String[] result = new String[2];

		//the name of the table we're taking the mv attribute from is the name of the key
		result[0] = mvAttrTable;

		//string to hold the contents of the column results
		String colContent = "";

		//in case no matches, set result[1] to empty string
		result[1] = colContent;
		

		//make the statement and execute it
		try{

			//match the alias with the id
			String fullID1 = mvAttrTable.split(" ")[1] +"."+id1;
			String fullID2 = relationship.split(" ")[1]+"."+id2;
			String fullID3 = oriTable.split(" ")[1]+"."+id3;
			String fullID4 = relationship.split(" ")[1]+"."+id4;

			//set up the preparedstatement
			getMVAttrs = con.prepareStatement("SELECT " + mvAttrCol + " " +
											  "FROM " + mvAttrTable + " " +
											  "JOIN " + relationship + " " +
												"ON " + fullID1 + " = " + fullID2 + " " +
											  "JOIN " + oriTable + " " +
												"ON " + fullID3 + " = " + fullID4 + " " +
											  "WHERE " + fullID3 + " = ?");
			
			//pass in idval
			getMVAttrs.setString(1, idVal);

			//execute the select statement and store resultSet into mvAttrRS
			mvAttrRS = getMVAttrs.executeQuery();

			//process result set contents
			if (mvAttrRS != null){
				
				//while loop to move through each row
				while(mvAttrRS.next()){
					
					colContent += mvAttrRS.getString(1) + "; ";

				}
			}

			//store the updated colContent into reuslt array
			result[1] = colContent;

		}
		catch (SQLException e){
			//probably move this to some error log of some kind
			System.out.println("SQLException: " + e.getMessage());
		}
		
		return result;
		
	}
}
