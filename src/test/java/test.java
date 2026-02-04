
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class test {
  
  public static void main(String[] args) {
    String s2 = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + " 00:00:00";
    String s = "2025-10-31 00:00:00";
    LocalDateTime parse = LocalDateTime.parse(s2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    System.out.println("parse =  " + parse);
  }
}
