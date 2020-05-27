package tv.skimo.meeting.background;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tv.skimo.engine.SkimoEngine;
import tv.skimo.meeting.utils.AssetUtil;
import tv.skimo.meeting.utils.Constants;
import tv.skimo.meeting.utils.EngineStatus;

@Component
public class Scheduler 
{
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	
	@Scheduled(fixedDelay = Constants.SKIMO_JOB_FREQUENCY_IN_SECONDS)
	public void executeSkimoJobs() 
	{
		try 
		{
			if(EngineStatus.isBusy())
			{
				log.info("Background task: engine is busy");
			}
			else
			{
				File[] dirs = new File(Constants.PUBLIC).listFiles(File::isDirectory);
				File[] assetDirs = null;
			
				// look for directories that don't have js, css and img
				// provision asset directory 
				// detect timecodes that has scene changes
				// =======================================
				for(int i=0; i < dirs.length; i++)
				{
					if((!dirs[i].getName().equals("js")) && (!dirs[i].getName().equals("css")) && 
						(!dirs[i].getName().equals("img"))) 
					{
						String assetId = dirs[i].getName();
						assetDirs = new File(Constants.PUBLIC + dirs[i].getName()).listFiles(File::isDirectory);
						if(assetDirs.length < 1)
						{
							log.info("Background task: kicking off Skimo for " + assetId);
							AssetUtil.provisionAsset(assetId, Constants.PUBLIC + assetId + Constants.ASSET_NAME);
							SkimoEngine.generatePoster(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);	
							SkimoEngine.generateThumbnails(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
							SkimoEngine.detectScenes(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
						}
					}	
					
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			log.error("Threw an exception in Scheduler::executeSkimoJobs, full stack trace follows:", e);
		}
	}
	
	@Scheduled(fixedDelay = Constants.CLEANUP_TASK_FREQUENCY_IN_SECONDS)
	public void cleanupUploadDirectory() 
	{
		File dir = new File(Constants.UPLOAD_DIR);
		
		log.info("Background cleanup upload directory task kicked off");

		deleteFiles(dir);
	}

	@Scheduled(fixedDelay = Constants.CLEANUP_TASK_FREQUENCY_IN_SECONDS)
	public void cleanupAssetDirectories() 
	{
		File[] files = new File(Constants.PUBLIC).listFiles(File::isDirectory);
		
		log.info("Background cleanup asset task kicked off");
		
        for (File f:files)
        {
        	// delete all older directories than 7 days other than css,img,js 
        	
			if((!f.getName().equals("js")) && (!f.getName().equals("css")) && 
					(!f.getName().equals("img"))) 
			{
				long diff = new Date().getTime() - f.lastModified();

	        	if (diff > Constants.WEEKLY * 24 * 60 * 60 * 1000)
	        	{
		        	log.info("Background cleanup task trying to cleanup " + f.getName());
	        		for (File f2 : f.listFiles()) 
	        		{
	        			for (File f3 : f2.listFiles()) 
	        				f3.delete();
	        			f2.delete();
	        		}
	        		f.delete();
	        		log.info("Background cleanup task cleaned up " + f.getName());
	        	}
			}
        }
	}
	
	public  void cleanupDir(String name) 
	{
		File dir = new File(name);
		
		log.info("Cleanup  directory " + name);

		deleteFilesRegardless(dir);
		dir.delete();
	}
	
	public  void deleteFiles(File dir) 
	{
	    File[] files = dir.listFiles();
	    if(files != null) 
	    {
	        for (final File file : files) 
	        {
	           log.info(file.getAbsolutePath());
	           deleteFiles(file);
	        }
	    }
	    long diff = new Date().getTime() - dir.lastModified();

    	if (diff > Constants.WEEKLY * 24 * 60 * 60 * 1000)
    	{
    		if(!dir.isDirectory())
    		   dir.delete();
    	}
	}

	public  void deleteFilesRegardless(File dir) 
	{
	    File[] files = dir.listFiles();
	    if(files != null) 
	    {
	        for (final File file : files) 
	        {
	           log.info(file.getAbsolutePath());
	           deleteFilesRegardless(file);
	        }
	    }
    	if(!dir.isDirectory())
    		dir.delete();
	}
	
	public static void main(String args[])
	{
		Scheduler s = new Scheduler();
		//s.executeSkimoJobs();
		s.cleanupDir("public/28bb529e/img/");
	}
}