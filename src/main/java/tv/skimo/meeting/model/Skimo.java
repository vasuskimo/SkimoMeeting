package tv.skimo.meeting.model;
import java.io.Serializable;

public class Skimo implements Serializable
{
	private static final long serialVersionUID = -375414813253543648L;
	private String videoUrl;
	private String currentTime;
	private String text;
 
	public Skimo()
	{
	}
	
	public Skimo( String videoUrl)
	{
		this.videoUrl = videoUrl;
	}

	public Skimo( String videoUrl, int cTime, String text)
	{
		this.videoUrl = videoUrl;
		this.text = text;
		
		int days, hours, minutes, seconds;

		days = cTime / 86400;
		hours = (cTime % 86400 ) / 3600 ;
		minutes = ((cTime % 86400 ) % 3600 ) / 60;
		seconds = ((cTime % 86400 ) % 3600 ) % 60  ;
		
		if(seconds < 10)
			if(minutes < 10)
				this.currentTime = Integer.toString(hours) + ":0" + Long.toString(minutes) + ":0" + 
					Long.toString(seconds);
			else
				this.currentTime = Integer.toString(hours) + ":" + Long.toString(minutes) + ":0" + 
						Long.toString(seconds);
		else
			if(minutes < 10)
				this.currentTime = Integer.toString(hours) + ":0" + Long.toString(minutes) + ":" + 
						Long.toString(seconds);
			else
				this.currentTime = Integer.toString(hours) + ":" + Long.toString(minutes) + ":" + 
					Long.toString(seconds);
			
	}

	public String getVideoUrl()
	{
		return videoUrl;
	}

	public void setVideoUrl( String videoUrl )
	{
		this.videoUrl = videoUrl;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setcurrentTime(String c) {
		this.currentTime = c;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
