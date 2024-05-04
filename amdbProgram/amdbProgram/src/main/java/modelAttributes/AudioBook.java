package modelAttributes;

import lombok.Data;

@Data
public class AudioBook {
    int Book_ID;
    String Book_Title;
    int MinLength;
    int MaxLength;
    String Release_Date;
    
}
