package tv.skimo.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import tv.skimo.meeting.utils.Constants;
 
public class SkimoEngine
{ 
	private static final Logger log = LoggerFactory.getLogger(SkimoEngine.class);

	public static void generatePoster(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        String [] command = {cwd + "/scripts/postergenerate.sh", cwd  + Constants.ROOT + asset, assetId, cwd};  
        
		log.info("generate first " + Arrays.toString(command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command); 
        processBuilder.directory(new File(System.getProperty(Constants.USER_HOME)));
        
        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        } 
        catch (IOException e) 
        {
			log.error("Threw an exception in Skimo Engine::generatePoster, full stack trace follows:", e);
        }
	}
	
	public static void generateThumbnails(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        String [] command = {cwd + "/scripts/thumbnailgenerate.sh", cwd + Constants.ROOT + asset, assetId, cwd};
        
		log.info("generate thumbnail " + Arrays.toString(command));

        
        ProcessBuilder processBuilder = new ProcessBuilder(command); 
        processBuilder.directory(new File(System.getProperty(Constants.USER_HOME)));
        
        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        } 
        catch (IOException e) 
        {
			log.error("Threw an exception in SkimoEngine::generateThumbnail, full stack trace follows:", e);
        }
	}
	
	public static void detectScenes(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        log.info("cwd is " + cwd);
        log.info("assetId " + assetId);
        log.info("asset is "  + asset);
        
        String [] command = {cwd + "/scripts/scenedetect.sh", cwd + Constants.ROOT + asset, assetId, cwd};
        
		log.info("generate skimo " + Arrays.toString(command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command); 
        processBuilder.directory(new File(System.getProperty(Constants.USER_HOME)));
        
        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        } 
        catch (IOException e) 
        {
			log.error("Threw an exception in SkimoEngine::Detect Scene, full stack trace follows:", e);
        }
	}
	
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
	   
	   public static ArrayList<String> getTextFromImage(String assetId) 
	   {
		   ArrayList<String> text = new ArrayList<String>();
	       String cwd = System.getProperty(Constants.USER_DIR) + "/" + Constants.PUBLIC + assetId + Constants.IMG_DIR;       
	       Path dir = Paths.get(cwd);
	       
	       try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jpg")) 
	       {
	           for (Path entry: stream) 
	           {
	             text.add(getOCR(cwd + entry.getFileName().toString(), Constants.TESS_DATA));
	           }
	       } catch (Exception ex) 
	       {
	    	   log.error("exception thrown by Tesseract go method" + ex);
	       }
	       return text;
	   }

	
    public static void main(String[] args) 
    { 
    	//SkimoEngine.generateFirst("a.mp4",  "8fc4e728");
    	SkimoEngine.detectScenes("public/8fc4e728/source.mp4", "8fc4e728");
    	//SkimoEngine.generateThumbnail("a.mp4",  "8fc4e728");
    	//SkimoEngine.detectScenes("a.mp4",  "8fc4e728");
    } 
} 