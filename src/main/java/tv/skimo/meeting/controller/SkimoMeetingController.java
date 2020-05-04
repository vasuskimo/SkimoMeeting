package tv.skimo.meeting.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.skimo.meeting.lib.FileSorter;
import tv.skimo.meeting.lib.StorageFileNotFoundException;
import tv.skimo.meeting.lib.ThymeLeafConfig;
import tv.skimo.meeting.model.Skimo;
import tv.skimo.meeting.services.StorageService;
import tv.skimo.meeting.utils.AssetUtil;
import tv.skimo.meeting.utils.Constants;
import tv.skimo.meeting.utils.EngineStatus;
import tv.skimo.meeting.utils.SceneDetector;
import tv.skimo.meeting.utils.Zipper;

@Controller
public class SkimoMeetingController {

	private final StorageService storageService;
	
    private static final Logger logger=LoggerFactory.getLogger(SkimoMeetingController.class);

	private String baseUrl;
	
    @Autowired private TemplateEngine templateEngine;

	@Autowired
	public SkimoMeetingController(StorageService storageService)
	{
		this.storageService = storageService;
	}

	@GetMapping("/videos/upload")
	public String listUploadedFiles(Model model) throws IOException 
	{
		List<String> filesList =  storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(SkimoMeetingController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList());

	    ArrayList<String> urlList = new ArrayList<String>();
		for (int i = 0; i < filesList.size(); i++)
		{
			String st = filesList.get(i).replace(".zip", "");
			String st2 = st.replace("files", "skimo");
			urlList.add(st2);
		}
		if(urlList.size() ==0)
		{
			filesList = new ArrayList<String>();
			urlList = new ArrayList<String>();
			filesList.add("No entries");
			urlList.add("No entries");
			model.addAttribute("files",filesList);
			model.addAttribute("urls",urlList);
		}
		else
		{
			model.addAttribute("files",filesList);
			model.addAttribute("urls",urlList);
		}
		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) 
	{ 
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/videos/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) 
	{	
		String assetId = null;
		storageService.store(file);
		assetId = AssetUtil.createHash(Constants.UPLOAD_DIR , file.getOriginalFilename());

		if(AssetUtil.createAssetDir(Constants.UPLOAD_DIR, assetId, file.getOriginalFilename()))
		{
			try 
			{
				if(EngineStatus.isBusy())
				{
					logger.info("Engine is busy");
				}
				else
				{
					SceneDetector.generateFirst(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
					SceneDetector.generateThumbnail(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
					SceneDetector.generateSkimo(Constants.PUBLIC + assetId + Constants.ASSET_NAME, assetId);
				}
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    redirectAttributes.addFlashAttribute("message", "Skimo is being generated for " + file.getOriginalFilename());
			String retVal = "redirect:/" + "skimo/" + assetId;
			return retVal;				
		}
		else
			redirectAttributes.addFlashAttribute("message", "Skimo is already available for " + file.getOriginalFilename() + "!");

		String retVal = "redirect:/" + "skimo/" + assetId;
		return retVal;
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) 
	{
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/skimo/{assetId}")
	public String viewMedia( Model model,@PathVariable(name="assetId") String assetId )
	{
		File dir = new File(Constants.PUBLIC + assetId);
		String timeCodeResource = dir + "/timecodes.txt";
		String videoResource = dir  + Constants.ASSET_NAME;
		String imgResource = dir + "/img";
		
		try 
		{
			if(EngineStatus.isRunningSkimo(assetId))
				return "busy.html";
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File imgDirect = new File(Constants.PUBLIC + assetId + "/img");
		if(imgDirect.exists())
		{
			List<String> timeCodeList;
			try (Stream<String> lines = Files.lines( Paths.get(timeCodeResource)))
			{
				timeCodeList = lines.collect( Collectors.toList() );
			}
			catch ( IOException e )
			{
				e.printStackTrace();
				return "error.html";
			}
			timeCodeList.add(0,"0.0");

			List<Integer> updatedList = new ArrayList<>();
			String initVal = timeCodeList.get( 0 );
			if ( initVal == null )
			{
				return "error.html";
			}
			updatedList.add(0);
			for ( int i = 0; i < timeCodeList.size(); i++ )
			{ 
				if ( ( Double.parseDouble( timeCodeList.get( i ) ) - Double.parseDouble( initVal ) ) > 30 )
				{
					updatedList.add(i );
					initVal = timeCodeList.get( i );
					i = timeCodeList.indexOf( timeCodeList.get( i ) ) - 1;
				}
			}
		    File imgDir = new File(Constants.PUBLIC + assetId +"/img");
			List<String> imgList = FileSorter.sort(imgDir);
			
			File videoFile = new File(videoResource);
			String videoFileName = videoFile.getName();
			baseUrl = "../" +assetId  + "/";
			ArrayList<Skimo> skimoList = new ArrayList<>();
			List<String> finalImgList = imgList;
			
			List<String> updatedTimeCodeList =new ArrayList<>(1000);
			List<String> updatedImgList = new ArrayList<>(1000);

			try
			{
				for(int  i=0; i < updatedList.size();  i++)
				{
					int ix = updatedList.get(i);
					updatedTimeCodeList.add(timeCodeList.get(ix).toString());
					updatedImgList.add(finalImgList.get(ix).toString());
				}
			}
			catch(Exception e)
			{
				return "404";
			}
			
			IntStream.range(1, updatedTimeCodeList.size() ).forEach( i -> {
				double v = Double.parseDouble( updatedTimeCodeList.get( i ) );
				int videoTime = ( int ) v;
				skimoList.add( new Skimo( this.baseUrl.concat( "img/" ).concat( updatedImgList.get( i ) ), this.baseUrl.concat( videoFileName ).concat( "#t=" + videoTime ) ) );
			} );

			Skimo  first_item =  new Skimo( this.baseUrl.concat( "img/" ).concat(updatedImgList.get( 0) ), this.baseUrl.concat( videoFileName ).concat( "#t=" + "0" ) );
			model.addAttribute("first_item",  first_item );			
			model.addAttribute( "mediaList", skimoList );
			
			
		    Context context = new Context();
		    context.setVariable("first_item", first_item);
		    context.setVariable("mediaList", skimoList);

	        Writer writer = null;
	        File indexFile = new File(Constants.PUBLIC + assetId + "/skimo.html");
	        if(!indexFile.exists())
	        {
	            String[] skimoFiles = {Constants.PUBLIC + assetId};
	            String zipFile = "upload-dir/" + assetId + ".zip";
	            Zipper zipUtil = new Zipper();
	        	try 
	        	{
	        		writer = new FileWriter(Constants.PUBLIC + assetId + "/skimo.html");
	        		writer.write(ThymeLeafConfig.getTemplateEngine().process("skimo.html", context));
	        		writer.close();
	                zipUtil.zip(skimoFiles, zipFile);
	        	} 
	        	catch (Exception e) 
	        	{
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
	        	
	        }
			return "upload";
		}
		else
		{
			if(dir.exists())
				return "busy.html";
			else
				return "404";
		}
	}

}
