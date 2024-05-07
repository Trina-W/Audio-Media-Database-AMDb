package modelAttributes;

import lombok.Data;

@Data
public class Song {
    String Song_ID;
    String Song_Title;
    String Genre;
    int MinTempo;
    int MaxTempo;
    int SMinLength;
    int SMaxLength;
    Boolean Explicit;
    int Music_Release_ID;

}
