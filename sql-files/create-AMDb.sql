-- Drop and create the database
DROP DATABASE IF EXISTS AMDb;
CREATE DATABASE AMDb;
USE AMDb;

-- User management
DROP USER IF EXISTS 'user'@'%';
CREATE USER 'user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON AMDb.* TO 'user'@'%';
FLUSH PRIVILEGES;


/* Music Tables */

CREATE TABLE Record_Label
(
    Label_ID   INTEGER PRIMARY KEY AUTO_INCREMENT,
    Label_Name VARCHAR(255) NOT NULL
);

CREATE TABLE Music_Artist
(
    Artist_ID   INTEGER PRIMARY KEY AUTO_INCREMENT,
    Artist_Name VARCHAR(255) NOT NULL
);

CREATE TABLE Music_Release
(
    Release_ID    INTEGER PRIMARY KEY AUTO_INCREMENT,
    Release_Title VARCHAR(255) NOT NULL
);

CREATE TABLE Song
(
    Song_ID          VARCHAR(255) PRIMARY KEY,
    Song_Title       VARCHAR(255) NOT NULL,
    Genre            VARCHAR(255) NOT NULL,
    Tempo            INTEGER      NOT NULL,
    Length           INTEGER      NOT NULL,
    Explicit         BOOLEAN      NOT NULL,
    Music_Release_ID INTEGER      NOT NULL,
    FOREIGN KEY (Music_Release_ID) REFERENCES Music_Release (Release_ID) ON
        DELETE CASCADE
);

CREATE TABLE Releases
(
    Label_ID   INTEGER NOT NULL,
    Release_ID INTEGER NOT NULL,
    PRIMARY KEY (Label_ID, Release_ID),
    FOREIGN KEY (Label_ID) REFERENCES Record_Label (Label_ID) ON DELETE CASCADE,
    FOREIGN KEY (Release_ID) REFERENCES Music_Release (Release_ID) ON DELETE
        CASCADE
);

CREATE TABLE Signs
(
    Label_ID  INTEGER NOT NULL,
    Artist_ID INTEGER NOT NULL,
    PRIMARY KEY (Label_ID, Artist_ID),
    FOREIGN KEY (Label_ID) REFERENCES Record_Label (Label_ID) ON DELETE CASCADE,
    FOREIGN KEY (Artist_ID) REFERENCES Music_Artist (Artist_ID) ON DELETE
        CASCADE
);

CREATE TABLE Creates
(
    Artist_ID  INTEGER NOT NULL,
    Release_ID INTEGER NOT NULL,
    PRIMARY KEY (Artist_ID, Release_ID),
    FOREIGN KEY (Artist_ID) REFERENCES Music_Artist (Artist_ID) ON DELETE
        CASCADE,
    FOREIGN KEY (Release_ID) REFERENCES Music_Release (Release_ID) ON DELETE
        CASCADE
);

CREATE TABLE Records
(
    Artist_ID INTEGER      NOT NULL,
    Song_ID   VARCHAR(255) NOT NULL,
    PRIMARY KEY (Artist_ID, Song_ID),
    FOREIGN KEY (Artist_ID) REFERENCES Music_Artist (Artist_ID) ON DELETE
        CASCADE,
    FOREIGN KEY (Song_ID) REFERENCES Song (Song_ID) ON DELETE CASCADE
);


/* Podcast Tables */

CREATE TABLE Podcast_Host
(
    Host_ID   INTEGER PRIMARY KEY AUTO_INCREMENT,
    Host_Name VARCHAR(255) NOT NULL
);

CREATE TABLE Podcast
(
    Podcast_ID    INTEGER PRIMARY KEY AUTO_INCREMENT,
    Podcast_Title VARCHAR(255) NOT NULL,
    Url           VARCHAR(255) NOT NULL,
    Explicit      BOOLEAN      NOT NULL,
    Description   VARCHAR(255) NOT NULL
);

CREATE TABLE Podcast_Episode
(
    Podcast_Episode_ID    INTEGER PRIMARY KEY AUTO_INCREMENT,
    Podcast_Episode_Title VARCHAR(255) NOT NULL,
    Podcast_ID            INTEGER      NOT NULL,
    FOREIGN KEY (Podcast_ID) REFERENCES Podcast (Podcast_ID) ON DELETE CASCADE
);

CREATE TABLE Hosts
(
    Host_ID    INTEGER NOT NULL,
    Podcast_ID INTEGER NOT NULL,
    PRIMARY KEY (Host_ID, Podcast_ID),
    FOREIGN KEY (Host_ID) REFERENCES Podcast_Host (Host_ID) ON DELETE CASCADE,
    FOREIGN KEY (Podcast_ID) REFERENCES Podcast (Podcast_ID) ON DELETE CASCADE
);


/* Audiobook Tables*/

CREATE TABLE Publisher
(
    Publisher_ID   INTEGER PRIMARY KEY AUTO_INCREMENT,
    Publisher_Name VARCHAR(255) NOT NULL
);

CREATE TABLE Audio_Book
(
    Book_ID      INTEGER PRIMARY KEY AUTO_INCREMENT,
    Book_Title   VARCHAR(255) NOT NULL,
    Length       INTEGER      NOT NULL,
    Release_Date DATE         NOT NULL
);

CREATE TABLE Author
(
    Author_ID   INTEGER PRIMARY KEY AUTO_INCREMENT,
    Author_Name VARCHAR(255) NOT NULL
);

CREATE TABLE Narrator
(
    Narrator_ID   INTEGER PRIMARY KEY AUTO_INCREMENT,
    Narrator_Name VARCHAR(255) NOT NULL
);

CREATE TABLE Publishes
(
    Publisher_ID INTEGER NOT NULL,
    Book_ID      INTEGER NOT NULL,
    PRIMARY KEY (Publisher_ID, Book_ID),
    FOREIGN KEY (Publisher_ID) REFERENCES Publisher (Publisher_ID) ON DELETE
        CASCADE,
    FOREIGN KEY (Book_ID) REFERENCES Audio_Book (Book_ID) ON DELETE CASCADE
);

CREATE TABLE Writes
(
    Author_ID INTEGER NOT NULL,
    Book_ID   INTEGER NOT NULL,
    PRIMARY KEY (Author_ID, Book_ID),
    FOREIGN KEY (Author_ID) REFERENCES Author (Author_ID) ON DELETE CASCADE,
    FOREIGN KEY (Book_ID) REFERENCES Audio_Book (Book_ID) ON DELETE CASCADE
);

CREATE TABLE Narrates
(
    Narrator_ID INTEGER NOT NULL,
    Book_ID     INTEGER NOT NULL,
    PRIMARY KEY (Narrator_ID, Book_ID),
    FOREIGN KEY (Narrator_ID) REFERENCES Narrator (Narrator_ID) ON DELETE
        CASCADE,
    FOREIGN KEY (Book_ID) REFERENCES Audio_Book (Book_ID) ON DELETE CASCADE
);
