package tv.skimo.meeting.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class SceneDetector
{ 
	public static void generateFirst(String asset, String assetId)
	{
        String cwd = System.getProperty("user.dir");
        
        String [] command = {cwd + "/scripts/first.sh", cwd + "/upload-dir/" + asset, assetId, cwd};     
        
        ProcessBuilder processBuilder = new ProcessBuilder(command); 
        processBuilder.directory(new File(System.getProperty("user.home")));
        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	public static void generateThumbnail(String asset, String assetId)
	{
        String cwd = System.getProperty("user.dir");
        
        String [] command = {cwd + "/scripts/thumbnails.sh", cwd + "/upload-dir/" + asset, assetId, cwd};
        
        ProcessBuilder processBuilder = new ProcessBuilder(command); 
        processBuilder.directory(new File(System.getProperty("user.home")));
        
        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	public static void generateSkimo(String asset, String assetId)
	{
        String cwd = System.getProperty("user.dir");
        
        String [] command = {cwd + "/scripts/skimo.sh", cwd + "/upload-dir/" + asset, assetId, cwd};
        
        ProcessBuilder processBuilder = new ProcessBuilder(command); 
        processBuilder.directory(new File(System.getProperty("user.home")));
        
        try 
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}

	
    public static void main(String[] args) 
    { 
    	SceneDetector.generateFirst("a.mp4",  "8fc4e728");
    	//SceneDetector.generateThumbnail("a.mp4",  "8fc4e728");
    	//SceneDetector.generateSkimo("a.mp4",  "8fc4e728");
    } 
} 
