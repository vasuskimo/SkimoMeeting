package tv.skimo.meeting.lib;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
 
public class VideoHasher 
{
   public static long crc32(String filepath) throws IOException 
   {
      InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
      CRC32 crc = new CRC32();
      int cnt;
      while ((cnt = inputStream.read()) != -1) 
      {
         crc.update(cnt);
      }
      return crc.getValue();
   }
    
   public static void main(String[] args) throws IOException 
   {
      String filepath = "./a.mp4";
      long crc = crc32(filepath);
      System.out.println(Long.toHexString(crc));
 
   }
}
