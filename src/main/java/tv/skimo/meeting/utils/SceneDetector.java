package tv.skimo.meeting.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.skimo.meeting.background.Scheduler;
 
public class SceneDetector
{ 
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	public static void generateFirst(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        String [] command = {cwd + "/scripts/first.sh", cwd  + Constants.ROOT + asset, assetId, cwd};  
        
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
			log.error("Threw an exception in SceneDetector::generateFirst, full stack trace follows:", e);
        }
	}
	
	public static void generateThumbnail(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        String [] command = {cwd + "/scripts/thumbnails.sh", cwd + Constants.ROOT + asset, assetId, cwd};
        
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
			log.error("Threw an exception in SceneDetector::generateThumbnail, full stack trace follows:", e);
        }
	}
	
	public static void generateSkimo(String asset, String assetId)
	{
        String cwd = System.getProperty(Constants.USER_DIR);
        
        log.info("cwd is " + cwd);
        log.info("assetId " + assetId);
        log.info("asset is "  + asset);
        
        String [] command = {cwd + "/scripts/skimo.sh", cwd + Constants.ROOT + asset, assetId, cwd};
        
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
			log.error("Threw an exception in SceneDetector::generateSkimo, full stack trace follows:", e);
        }
	}

	
    public static void main(String[] args) 
    { 
    	//SceneDetector.generateFirst("a.mp4",  "8fc4e728");
    	SceneDetector.generateSkimo("public/8fc4e728/source.mp4", "8fc4e728");
    	//SceneDetector.generateThumbnail("a.mp4",  "8fc4e728");
    	//SceneDetector.generateSkimo("a.mp4",  "8fc4e728");
    } 
} 
