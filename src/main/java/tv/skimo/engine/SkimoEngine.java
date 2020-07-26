package tv.skimo.engine;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import tv.skimo.meeting.lib.ProcessLauncher;
import tv.skimo.meeting.lib.SRTParser;
import tv.skimo.meeting.utils.Constants;
 
public class SkimoEngine
{ 
	private static final Logger log = LoggerFactory.getLogger(SkimoEngine.class);

	public static void generatePoster(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        String [] command = {cwd + "/scripts/postergenerate.sh", cwd  + Constants.ROOT + asset, assetId, cwd};  
        
		log.info("generate first " + Arrays.toString(command));

		ProcessLauncher.launch(command, log, "generatePoster");
	}
	
	public static void generateThumbnails(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        String [] command = {cwd + "/scripts/thumbnailgenerate.sh", cwd + Constants.ROOT + asset, assetId, cwd};
        
		log.info("generate thumbnail " + Arrays.toString(command));

		ProcessLauncher.launch(command, log, "generateThumbnails");
	}
	
	public static void detectScenes(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        log.info("cwd is " + cwd);
        log.info("assetId " + assetId);
        log.info("asset is "  + asset);
        
        String [] command = {cwd + "/scripts/scenedetect.sh", cwd + Constants.ROOT + asset, assetId, cwd};
        
		log.info("Detect Scenes " + Arrays.toString(command));
        
		ProcessLauncher.launch(command, log, "detectScenes");
	}
	
	public static void generateSRT(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        String srt = assetId + ".srt";
        
        String [] command = {cwd + "/scripts/subtitlegenerate.sh", cwd + Constants.ROOT + asset, assetId, cwd, srt};
        
		log.info("generate SRT " + Arrays.toString(command));
		
		ProcessLauncher.launch(command, log, "generateSRT");
	}
	
	public static void generateSub(String assetId)
	{
		SRTParser s = new SRTParser();
		String srtFile = System.getProperty(Constants.USER_DIR) + "/" + Constants.PUBLIC + assetId + "/" + assetId + ".srt";
		String subFile = System.getProperty(Constants.USER_DIR) + "/" + Constants.PUBLIC + assetId + "/" + "subtitles.sub";
		HashMap<String, String> hMap = s.process(srtFile);
		s.writeToFile(hMap,subFile);
	}

    private static String getOCR(String asset, String tessData)
    {
		   log.info("Getting OCR for " + asset);
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
	    } 
	    catch (Exception ex) 
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
