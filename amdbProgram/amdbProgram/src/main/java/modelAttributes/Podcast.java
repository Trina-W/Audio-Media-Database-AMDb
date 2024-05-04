package modelAttributes;


import lombok.Data;

@Data
public class Podcast {
    int Podcast_ID;
    String Podcast_Title;
    String Url;
    Boolean PExplicit;
    String Description;
}
