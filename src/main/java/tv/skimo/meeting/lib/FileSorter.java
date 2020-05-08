package tv.skimo.meeting.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileSorter 
{
 
	public static List<String> sort(File  dir)
	{
		List<String> list = new ArrayList<>();
		File[] files = dir.listFiles();

		Arrays.sort(files, (f1, f2) -> 
		{
			return new Date(f1.lastModified()).compareTo(new Date(f2.lastModified()));
		});


		for (File file : files) 
		{
			if (!file.isHidden()) 
			{
				if (!file.isDirectory())
					list.add(file.getName());
			}
		}
		return list;
   }
	
   public static void main(String[] args) 
   {
      File dir = new File("public/" + "8fc4e728" +"/img");
      
      List<String> l = FileSorter.sort(dir);
      System.out.println(l.toString());
   }
}