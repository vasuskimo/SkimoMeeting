package tv.skimo.meeting.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
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

	
    public static void main(String[] args) 
    { 
    	//SkimoEngine.generateFirst("a.mp4",  "8fc4e728");
    	SkimoEngine.detectScenes("public/8fc4e728/source.mp4", "8fc4e728");
    	//SkimoEngine.generateThumbnail("a.mp4",  "8fc4e728");
    	//SkimoEngine.detectScenes("a.mp4",  "8fc4e728");
    } 
} 
