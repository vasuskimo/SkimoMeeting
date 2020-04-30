package tv.skimo.meeting.background;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tv.skimo.meeting.utils.AssetUtil;
import tv.skimo.meeting.utils.Constants;
import tv.skimo.meeting.utils.EngineStatus;
import tv.skimo.meeting.utils.SceneDetector;

@Component
public class Scheduler {

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	@Scheduled(fixedDelay = Constants.SKIMO_JOB_FREQUENCY_IN_SECONDS)
	public void executeSkimoJobs() 
	{
		try 
		{
			if(EngineStatus.isBusy())
			{
				log.info("Engine is busy");
			}
			else
			{
				File[] dirs = new File(Constants.PUBLIC).listFiles(File::isDirectory);
				File[] assetDirs = null;
			
				// look for directories that don't have js, css and img
				// provision asset directory 
				// detect first scene, thumbnails and timecodes that has scene changes
				// ===================================================================
				for(int i=0; i < dirs.length; i++)
				{
					if((!dirs[i].getName().equals("js")) && (!dirs[i].getName().equals("css")) && 
						(!dirs[i].getName().equals("img"))) 
					{
						String assetId = dirs[i].getName();
						assetDirs = new File(Constants.PUBLIC + dirs[i].getName()).listFiles(File::isDirectory);
						if(assetDirs.length < 1)
						{
							log.info("Kicking off Skimo for " + assetId);
							AssetUtil.provisionAsset(assetId, Constants.PUBLIC + assetId + Constants.ASSET_NAME);
							SceneDetector.generateFirst(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
							SceneDetector.generateThumbnail(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
							SceneDetector.generateSkimo(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
						}
					}	
					
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		Scheduler s = new Scheduler();
		s.executeSkimoJobs();
	}
}