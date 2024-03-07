package goldsprite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTools {
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static String currentDateTime(){
        return "["+LocalDateTime.now().format(dtf)+"] ";
    }
}
