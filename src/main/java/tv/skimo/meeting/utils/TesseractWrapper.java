package tv.skimo.meeting.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TesseractWrapper 
{ 
   private static final Logger log=LoggerFactory.getLogger(TesseractWrapper.class);
   
   public static String getOCR(String asset, String tessData)
   {
	   //log.debug("Getting OCR for " + asset);
	   File image = new File(asset);
	   Tesseract tesseract = new Tesseract();
	   tesseract.setDatapath(tessData);
	   tesseract.setLanguage("eng");
	   String result = "";
	   try 
	   {
		   result = tesseract.doOCR(image);
	   } 
	   catch (TesseractException e) 
	   {
		   e.printStackTrace();
	   }
	   result = result.replace("\n", "").replaceAll("[\\W]|_", "").toLowerCase();
	   int len = result.split(System.getProperty("line.separator")).length;
	   //log.debug("The image " + asset +  " has " + len + " lines and " + result.length() + " characters");
	   //log.info(result);
	   return result;
   }
   
   public static ArrayList<String> go(String assetId) 
   {
	   ArrayList<String> text = new ArrayList<String>();
       String cwd = System.getProperty(Constants.USER_DIR) + "/" + Constants.PUBLIC + assetId + Constants.IMG_DIR;       
       Path dir = Paths.get(cwd);
       
       try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jpg")) 
       {
           for (Path entry: stream) 
           {
             text.add(TesseractWrapper.getOCR(cwd + entry.getFileName().toString(), Constants.TESS_DATA));
           }
       } catch (Exception ex) 
       {
    	   log.error("exception thrown by Tesseract go method" + ex);
       }
       return text;
   }
   
   
   public static void main(String[] args) throws IOException 
   {
	  ArrayList<String> result = TesseractWrapper.go("8fc4e728");
	  //System.out.println(TesseractWrapper.getOCR("/Users/vasusrinivasan/Development/SkimoMeeting/public/8fc4e728/img/frames3.jpg", "/usr/local/Cellar/tesseract/4.1.1/share/tessdata"));
	  System.out.println(result);
	  for (int i = 0; i < result.size(); i++)
          System.out.println(result.get(i));
   }
}
