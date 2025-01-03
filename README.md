# Set Up Instructions:

### Required software: VSCode, MySQL Workbench 

1. Download our zip file from the repo https://github.com/Trina-W/CS157A_AMDb.git
   
<img width="763" alt="Screenshot 2024-05-09 at 7 14 57 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/6d5d9184-92da-4fec-a13d-fea008129a38">

2. Unzip the file and open it in VS code
   
<img width="1372" alt="Screenshot 2024-05-09 at 8 30 24 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/3453e934-cda4-439c-8ed0-b3a60cb424ee">

3. Open MySQLWorkbench and run the files from the folder “sql-files” create-AMDb.sql and then afterwards insert-AMDb.sql
   
<img width="1213" alt="Screenshot 2024-05-09 at 7 36 03 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/78179e02-f47e-482f-bf13-9a939ba60361">
<img width="1217" alt="Screenshot 2024-05-09 at 7 35 53 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/82cff4a4-2901-428f-9563-f2f6dd327273">


4. Navigate to the “AmdbProgramApplication” file in the “src” file and click the play button to run the project 
   
<img width="1429" alt="Screenshot 2024-05-09 at 7 16 23 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/557a85fa-fd03-4841-ba36-90350885c712">

5. This should show up in the terminal when you run the program:
   
<img width="1163" alt="Screenshot 2024-05-09 at 7 20 51 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/1c0c9842-bb67-4df7-9801-c716adf1d0d2">

6. In a web browser go to this link: http://localhost:8080/amdb
   
  <img width="1420" alt="Screenshot 2024-05-09 at 7 23 01 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/7ad5c82d-b502-4290-9dac-9eb282b02d2d">

8. When you click “select” category you can choose between different audio media categories
(For example you can click on songs and type in the Song title “Sky” and the genre “blues” and a table of entries with that criteria shows up.)

<img width="1433" alt="Screenshot 2024-05-09 at 7 29 30 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/5c7d29d0-0144-4027-8359-2bc25e44c6dc">

9. To stop the app you can go back to mysql workbench and run the “drop-AMDb” file to drop the database. Then press the red square in vscode to stop the website.

<img width="1215" alt="Screenshot 2024-05-09 at 7 36 14 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/78bd7603-2183-4d91-83ec-49c6ca155097">

VS code stop button: 
<img width="240" alt="Screenshot 2024-05-09 at 7 36 50 PM" src="https://github.com/Trina-W/CS157A_AMDb/assets/118573860/61021801-2df0-439f-ad1e-7d5be01c54d8">

# Division of Work:

### User Input/Services 
- Averi:
  - Created TranslatorService.java
  - Class that uses user input to create SQL statements to retrieve data from the database
  - Processes the ResultSets and stores it in LinkedHashMaps / ArrayLists to be used in the HTML page
  - Created classes in ModelAttributes Package, which store the user input for use in the TranslatorService and AMDbController classes
    - Song.java, RecordLabel.java, MusicArtist.java, MusicRelease.java, PodcastHost.java, Podcast.java, PodcastEpisode.java, AudioBook.java, Publisher.java, Author.java, and Narrator.java
- Paul:
  - Created DatabaseConnection.java
  - A utility class that provides a method to create and establish the database connection to allow database queries to be executed.

### Controller
- Jonathan: 
  - Started AMDBController.java (adopted Averi’s finished version)
  - Initially created endpoint connections but implemented an improved version by Averi
  - Class that serves as the web controller that handles various types of media entities.
  - All request mappings for this controller will be prefixed with `/amdb`
  - GET method initializes the model with multiple media-related objects and returns the `home` view
  - POST method processes the submitted data from the forms bound to the desired model objects

### Front-End 
- Paul: 
  - Created home.html
  - The main page of the web application written in HTML, CSS, JavaScript, and Thymeleaf.
  - Dynamic and intuitive user interface that enables detailed searches across various audio media types including music, podcasts, audiobooks and their associated entities: song, music artist, record label, music release; podcast, podcast episode, podcast host; audiobook, author, narrator, publisher.

### SQL Statements
- Katrina: 
  - Cleaned/formatted data from CSV files using Python scripts and Open Refine. 
  - Created dummy data for missing information 
  - Converted the CSV files to MySQL insert statements 
  - Populated the SQL tables 

- Averi: 
  - Reformatted SQL INSERT statements
  - Created single file to run all inserts

- Paul: 
  - Reformatted SQL INSERT statements 
  - Created single file to run all inserts

### Documentation
The entire group collaborated to create the ER diagram, schema, presentation, and Final Report
link to our google drive folder used for planning

# Meeting Minutes:

###  3/27/24:
- Meeting length:  61
   - Project proposal
   - Creating google drive folder/ start of creating design documents
   - Creating idea Audio database 
   - Figuring out tech stack
   - Writing project proposal 

### 4/10/24:
- Meeting length: 140
   - EER diagram
   - Establishing an EER diagram 

### 4/18/24: 
- Meeting length: 148
   - Revising EER diagram
   - Making adjustments to audio_book, podcast, and song entities 
   - Established different audio media types be different entities not related by a disjoint set (originally audio_books, podcasts, and songs were all a type of audio_media)
   - Determined the use of dummy data for certain attributes 
   - Developed relational schema for all entities and relationships
   - Started .sql creation statements

### 4/26/24: 
- Meeting length: 55
   - Dividing up work into four sections
   - Averi will work on services (JDBC + using user input to create sql queries) 
   - Jonathan will work on controllers (JDBC controller, using the result set) 
   - Paul will work on the frontend (thymeleaf html, UI, filter settings, textboxes) 
   - Katrina will work on the SQL/CSV (populating the tables, creating dummy data, removing unnecessary info) 
   - Deciding on coding environment: VS code, build tool: Maven 
   - Deciding on search results being on the same page as the query 

### 5/3/24: 
- Meeting length: 31
   - Status update
   - Deciding on when to finish the code 
   - Figuring out what we still need to work on
   - Deciding on another meeting time later this week to work on the presentation and slides 

### 5/5/24: 
- Meeting length: 100 
   - Dividing up final report and presentation slides 
   - Figuring out a hard deadline for having our code completed by

### 5/8/24:
- Meeting length: 120 
   - Creating script for presentation 
   - Finish merging branches together 
   - Adding to readme draft 

