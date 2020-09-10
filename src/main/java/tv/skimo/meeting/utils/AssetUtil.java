package tv.skimo.meeting.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AssetUtil
{ 
    private static final Logger log=LoggerFactory.getLogger(AssetUtil.class);
    
	// This method computes the crc32 code of the uploaded file
    public static String createHash(String dir, String assetName)
    {
	    long crc = 0;
		try 
		{
			crc = VideoHasher.crc32(dir + assetName);
		} 
		catch (IOException e) 
		{
			log.error("Threw an exception in AssetUtil::createHash, full stack trace follows:", e);
		}
	    String assetId = Long.toHexString(crc);
	    
	    return assetId;
    }
 
	// This method creates an assetid directory under public directory and
    // copies the asset into the assetId directory as source.mp4
    public static boolean CreateAssetDirAndMoveFiles(String dir, String assetId, String assetName, String annotationFileName)
    {
	    File file = new File(Constants.PUBLIC + assetId);
	    boolean result = file.mkdir(); 
	    
        File source = new File(dir + assetName);
        File dest = new File(Constants.PUBLIC + assetId + Constants.ASSET_NAME);
        if(annotationFileName != null)
        {
        	File annotationSource = new File(dir + annotationFileName);
        	File annotationDest = new File(Constants.PUBLIC + assetId +  "/" + annotationFileName);
        	try
        	{
    			Files.move(annotationSource.toPath(), annotationDest.toPath(),StandardCopyOption.REPLACE_EXISTING);
        	}
    		catch (IOException e) 
    		{
    			log.error("Threw an exception in AssetUtil::createAssetDir, full stack trace follows:", e);
    		}
        }
		try 
		{
			log.info("moving the asset file " + Constants.ASSET_NAME);
			Files.move(source.toPath(), dest.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} 
		catch (IOException e) 
		{
			log.error("Threw an exception in AssetUtil::createAssetDir, full stack trace follows:", e);
		}
	    return(result);
    } 
    	
	// It creates img,js,css directories under the asset directory
	// It copies css,js and img files under the assetid directory
    public static boolean provisionAsset(String assetId, String assetName)
    {
	    boolean result = false;	    
	    File file1 = new File(Constants.PUBLIC + assetId + Constants.IMG_DIR);
	    result = file1.mkdir();
	    
	    File file2 = new File(Constants.PUBLIC + assetId + Constants.JS_DIR);
	    result = file2.mkdir();
	    
	    File file3 = new File(Constants.PUBLIC + assetId + Constants.CSS_DIR);
	    result = file3.mkdir();
        
        File sourceRevealJS = new File("public/js/reveal.js");
        File destRevealJS = new File(Constants.PUBLIC + assetId + "/js/reveal.js");

        File sourceVideoPlayerJS = new File("public/js/video-player.js");
        File destVideoPlayerJS = new File(Constants.PUBLIC + assetId + "/js/video-player.js");
        
        File sourceSkimoJS = new File("public/js/skimo.js");
        File destSkimoJS = new File(Constants.PUBLIC + assetId + "/js/skimo.js");
        
        File sourceScreenCSS = new File("public/css/screen.css");
        File destScreenCSS = new File(Constants.PUBLIC + assetId + "/css/screen.css");
        
        File sourceRevealCSS = new File("public/css/reveal.css");
        File destRevealCSS = new File(Constants.PUBLIC + assetId + "/css/reveal.css");
        
        File sourceMainMinCSS = new File("public/css/main.min.css");
        File destMainMinCSS = new File(Constants.PUBLIC + assetId + "/css/main.min.css");
        
        File sourceSkimoCSS = new File("public/css/skimo.css");
        File destSkimoCSS = new File(Constants.PUBLIC + assetId + "/css/skimo.css");
        
        File sourceLogo = new File("public/img/skimologo.png");
        File destLogo = new File(Constants.PUBLIC + assetId + "/skimologo.png");
        
        File sourceHLogo = new File("public/img/logo.png");
        File destHLogo = new File(Constants.PUBLIC + assetId + "/logo.png");
        
        File sourceFavIcon = new File("public/img/favicon.ico");
        File destFavIcon = new File(Constants.PUBLIC + assetId + "/favicon.ico");
        

        try 
        {
			Files.copy(sourceRevealJS.toPath(), destRevealJS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceScreenCSS.toPath(), destScreenCSS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceRevealCSS.toPath(), destRevealCSS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceMainMinCSS.toPath(), destMainMinCSS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceSkimoCSS.toPath(), destSkimoCSS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceSkimoJS.toPath(), destSkimoJS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceVideoPlayerJS.toPath(), destVideoPlayerJS.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceLogo.toPath(), destLogo.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceHLogo.toPath(), destHLogo.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.copy(sourceFavIcon.toPath(), destFavIcon.toPath(),StandardCopyOption.REPLACE_EXISTING);
        } 
        catch (IOException e) 
        {
			log.error("Threw an exception in AssetUtil::provisionAsset, full stack trace follows:", e);
		}
	      
	    return true;  	
    } 
    
    public static void main(String[] args) 
    { 
      System.out.println(AssetUtil.createHash("./upload-dir/","a.mp4"));
      System.out.println(AssetUtil.provisionAsset("./upload-dir/" + "EJUXlL3rHzA", "a.mp4"));
    } 
} 
