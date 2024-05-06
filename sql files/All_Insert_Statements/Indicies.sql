/* Music Entitiy Indicies */

CREATE INDEX record_label_id_index ON Record_Label (Label_ID);
CREATE INDEX music_artist_id_index ON Music_Artist (Artist_ID);
CREATE INDEX music_release_id_index ON Music_Release(Release_ID); 
CREATE INDEX song_id_index ON Song(Song_ID); 

/* Music Relationship Indicies */
CREATE INDEX releases_label_id_index ON Releases (Label_ID);
CREATE INDEX releases_release_id_index ON Releases (Release_ID);
CREATE INDEX signs_label_id_index ON Signs (Label_ID);
CREATE INDEX signs_artist_id_index ON Signs (Artist_ID);
CREATE INDEX creates_artist_id_index ON Creates (Artist_ID);
CREATE INDEX creates_release_id_index ON Creates (Release_ID);
CREATE INDEX records_artist_id_index ON Records (Artist_ID);
CREATE INDEX records_song_id_index ON Records (Song_ID);


/* Podcast Entity Indicies */
CREATE INDEX podcast_host_id_index ON Podcast_Host (Host_ID);
CREATE INDEX podcast_id_index ON Podcast (Podcast_ID);
CREATE INDEX podcast_episode_id_index ON Podcast_Episode (Podcast_Episode_ID);

/* Podcast Relationship Indicies */
CREATE INDEX hosts_host_id_index ON Hosts (Host_ID);
CREATE INDEX hosts_podcast_id_index ON Hosts (Podcast_ID);


/* Audiobook Entity Indicies */
CREATE INDEX publisher_id_index ON Publisher (Publisher_ID);
CREATE INDEX audio_book_id_index ON Audio_Book (Book_ID);
CREATE INDEX author_id_index ON Author (Author_ID);
CREATE INDEX narrator_id_index ON Narrator (Narrator_ID);

/* Audiobook Relationship Indicies */
CREATE INDEX publishes_publisher_id_index ON Publishes (Publisher_ID);
CREATE INDEX publishes_book_id_index ON Publishes (Book_ID);
CREATE INDEX writes_author_id_index ON Writes (Author_ID);
CREATE INDEX writes_book_id_index ON Writes (Book_ID);
CREATE INDEX narrates_narrator_id_index ON Narrates (Narrator_ID);
CREATE INDEX narrates_book_id_index ON Narrates (Book_ID);
