package tv.skimo.meeting.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
import org.thymeleaf.context.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.skimo.meeting.background.Scheduler;
import tv.skimo.meeting.lib.FileSorter;
import tv.skimo.meeting.lib.StorageFileNotFoundException;
import tv.skimo.meeting.lib.ThymeLeafConfig;
import tv.skimo.meeting.model.Skimo;
import tv.skimo.meeting.services.StorageService;
import tv.skimo.meeting.utils.AssetUtil;
import tv.skimo.meeting.utils.Constants;
import tv.skimo.meeting.utils.EngineStatus;
import tv.skimo.meeting.utils.LineCounter;
import tv.skimo.meeting.utils.SceneDetector;
import tv.skimo.meeting.utils.TesseractWrapper;
import tv.skimo.meeting.utils.Zipper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


@Controller
public class SkimoMeetingController {

	private final StorageService storageService;
	
    private static final Logger log=LoggerFactory.getLogger(SkimoMeetingController.class);

	private String baseUrl;
	

	@Autowired
	public SkimoMeetingController(StorageService storageService)
	{
		this.storageService = storageService;
	}

	@GetMapping("/myskimo")
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
	
	public OAuth2User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return ((OAuth2AuthenticationToken)auth).getPrincipal();
	}


	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) 
	{ 
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/myskimo")
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) 
	{
		log.info("Inside My Skimo post method");
		OAuth2User user = getCurrentUser();
		String email = (String) user.getAttributes().get("email");
	    log.info("email is " + email);
	      
		String assetId = "";
		String accName = "basic/" + email + "/";
		
		storageService.store(file,accName);
		assetId = AssetUtil.createHash(Constants.UPLOAD_DIR + accName , file.getOriginalFilename());

		if(AssetUtil.createAssetDir(Constants.UPLOAD_DIR + accName, assetId, file.getOriginalFilename()))
		{
			try 
			{
				if(EngineStatus.isBusy())
				{
					log.info("Engine is busy");
				}
				else
				{
					SceneDetector.generateFirst(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);	
					SceneDetector.generateThumbnail(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);
					SceneDetector.generateSkimo(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);
				}
			} 
			catch (IOException e) 
			{
				log.error("Threw an exception in SkimoMeetingController::handleFileUpload, full stack trace follows:", e);
			}
			String retVal = "skimo/" + assetId;
			
			return(retVal);			
		}
		String retVal = "skimo/" + assetId;
		return(retVal);			
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
        Writer writer = null;
        File indexFile = new File(Constants.PUBLIC + assetId + "/skimo.html");
        File imgDirect = new File(Constants.PUBLIC + assetId + "/img");
		int noOfLines = 0;
		
        if(indexFile.exists())
        {
    		return("redirect:/" + assetId + "/skimo.html");
        }
        else
        {
        	log.info("skimo.html file does not exist for " + assetId);
        }
        File f = new File(timeCodeResource); 
		
		if(f.exists())
		{
			noOfLines = LineCounter.count(timeCodeResource);
		}
		
		try 
		{
			if(!EngineStatus.isBusy() && imgDirect.exists() && (noOfLines > 3))
			{
				List<String> timeCodeList;
				try (Stream<String> lines = Files.lines( Paths.get(timeCodeResource)))
				{
					timeCodeList = lines.collect( Collectors.toList() );
				}
				catch ( IOException e )
				{
					log.error("Threw an exception in Scheduler::viewMedia, full stack trace follows:", e);
					return "error.html";
				}
				timeCodeList.add(0,"0.0");

				List<Integer> updatedList = new ArrayList<>();
				String initVal = timeCodeList.get( 0 );
				if ( initVal == null )
				{
					log.warn("initVal is null for assetId " + assetId);
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
			    File imgDir = new File(Constants.PUBLIC + assetId + Constants.IMG_DIR);
				List<String> imgList = FileSorter.sort(imgDir);
				
				File videoFile = new File(videoResource);
				String videoFileName = videoFile.getName();
				baseUrl = "../" +assetId  + "/";
				ArrayList<Skimo> skimoList = new ArrayList<>();
				List<String> finalImgList = imgList;
				
				List<String> updatedTimeCodeList =new ArrayList<>(1000);

				try
				{
					for(int  i=0; i < updatedList.size();  i++)
					{
						int ix = updatedList.get(i);
						updatedTimeCodeList.add(timeCodeList.get(ix).toString());
					}
				}
				catch(Exception e)
				{
					log.error("Threw an exception in Scheduler::viewMedia, full stack trace follows:", e);
					return "404";
				}
				
				final ArrayList<String> result = TesseractWrapper.go(assetId);
				IntStream.range(1, updatedTimeCodeList.size() ).forEach( i -> {
					double v = Double.parseDouble( updatedTimeCodeList.get( i ) );
					int videoTime = ( int ) v;
					log.info("text is " + result.get(i));
					skimoList.add( new Skimo( this.baseUrl.concat( videoFileName ).concat( "#t=" + videoTime ),videoTime,result.get(i+1)) );
				} );

				Skimo  first_item =  new Skimo( this.baseUrl.concat( videoFileName ).concat( "#t=" + "0" ) ,0, result.get(0));
				model.addAttribute("first_item",  first_item );			
				model.addAttribute( "mediaList", skimoList );
				
				
			    Context context = new Context();
			    context.setVariable("first_item", first_item);
			    context.setVariable("mediaList", skimoList);


			    if(!indexFile.exists())
			    {
			    	Scheduler s = new Scheduler();
			    	s.cleanupDir(Constants.PUBLIC + assetId + Constants.IMG_DIR);
			    	File file = new File(Constants.PUBLIC + assetId + Constants.TIME_CODE_FILE);
			    	file.delete();
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
						log.error("Threw an exception in Scheduler::viewMedia, full stack trace follows:", e);
			    	}
			    }
				return "busy.html";
			}
			else
			{
				if(dir.exists())
					return "busy.html";
				else
					return "404";
			}
		} 
		catch (Exception e) 
		{
			log.error("Threw an exception in Scheduler::viewMedia, full stack trace follows:", e);
		}
		return "404";
	}

}
