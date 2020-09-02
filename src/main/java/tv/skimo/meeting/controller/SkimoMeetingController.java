package tv.skimo.meeting.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.skimo.engine.SkimoEngine;
import tv.skimo.meeting.lib.StorageFileNotFoundException;
import tv.skimo.meeting.services.StorageService;
import tv.skimo.meeting.utils.AssetUtil;
import tv.skimo.meeting.utils.Constants;
import tv.skimo.meeting.utils.EngineStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


@Controller
@CrossOrigin
public class SkimoMeetingController {

	private final StorageService storageService;
	
    private static final Logger log=LoggerFactory.getLogger(SkimoMeetingController.class);
	
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
			@RequestParam("annotation") Optional <MultipartFile>  annotationFile,
			RedirectAttributes redirectAttributes) 
	{
		log.info("Inside My Skimo post method");
		OAuth2User user = getCurrentUser();
		String email = (String) user.getAttributes().get("email");
	    log.info("email is " + email);
	      
		String assetId = "";
		String accName = "basic/" + email + "/";
		String annotationFileName = null;
		log.info(file.getContentType());
		log.info(String.valueOf(annotationFile.isPresent()));
		
		storageService.store(file,accName);
		if(annotationFile.isPresent())
		{
			MultipartFile ann = annotationFile.get();
			storageService.store(ann,accName);
			annotationFileName = annotationFile.get().getOriginalFilename();
		}
		assetId = AssetUtil.createHash(Constants.UPLOAD_DIR + accName , file.getOriginalFilename());

		if(AssetUtil.CreateAssetDirAndMoveFiles(Constants.UPLOAD_DIR + accName, assetId, file.getOriginalFilename(), annotationFileName))
		{
			try 
			{
				if(EngineStatus.isBusy())
				{
					log.info("Engine is busy");
				}
				else
				{
					SkimoEngine.generatePoster(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);	
					SkimoEngine.generateThumbnails(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);
					SkimoEngine.detectScenes(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);
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
        File indexFile = new File(Constants.PUBLIC + assetId + "/skimo.html");
		
        if(indexFile.exists())
        {
    		return("redirect:/" + assetId + "/skimo.html");
        }
        else
        {
        	log.info("skimo.html file does not exist for " + assetId);
				if(dir.exists())
					return "busy.html";
				else
					return "404";
		} 

	}
	@GetMapping("/zip/{assetId}")
	public ModelAndView getZip( Model model,@PathVariable(name="assetId") String assetId )
	{
		log.info("assetid is " + assetId);
		return new ModelAndView("redirect:" + "/files/" + assetId + ".zip");
	}
			
	@PostMapping("/live/recording")
	@ResponseBody
	public  ResponseEntity<?> UploadLiveRecording(@RequestParam("file") MultipartFile file,
			@RequestParam("annotation") Optional <MultipartFile>  annotationFile,
			@RequestParam(name = "assetid") String assetId,
			@RequestParam(name = "apikey") String apikey,
			@RequestParam(name = "username") String email,			
			RedirectAttributes redirectAttributes) 
	{
		log.info("Inside live recording post method");
	    log.info("email is " + email);
	    log.info("api key is " + apikey);
	    
	    if(!apikey.equalsIgnoreCase("yKLxpeweS42A78"))
	    	return null;
	      
		String accName = "basic/" + email + "/";
		String annotationFileName = null;
		log.info(file.getContentType());
		storageService.store(file,accName);
		
		log.info(String.valueOf(annotationFile.isPresent()));
		
		if(annotationFile.isPresent())
		{
			MultipartFile ann = annotationFile.get();
			storageService.store(ann,accName);
			annotationFileName = annotationFile.get().getOriginalFilename();
		}

		if(AssetUtil.CreateAssetDirAndMoveFiles(Constants.UPLOAD_DIR + accName, assetId, file.getOriginalFilename(), annotationFileName))
		{
			try 
			{
				if(EngineStatus.isBusy())
				{
					log.info("Engine is busy");
				}
				else
				{
					SkimoEngine.generatePoster(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);	
					SkimoEngine.generateThumbnails(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);
					SkimoEngine.detectScenes(Constants.PUBLIC + accName + assetId + Constants.ASSET_NAME, assetId);
				}
				return new ResponseEntity<>("Success", HttpStatus.OK);
			} 
			catch (IOException e) 
			{
				log.error("Threw an exception in SkimoMeetingController::handleFileUpload, full stack trace follows:", e);
				return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>("Asset already exists", HttpStatus.OK);
	}
	
}
