package tv.skimo.meeting.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tv.skimo.meeting.lib.AssetInformation;
import tv.skimo.meeting.lib.EngineStatus;
import tv.skimo.meeting.lib.SceneDetector;
import tv.skimo.meeting.lib.StorageFileNotFoundException;
import tv.skimo.meeting.model.Skimo;
import tv.skimo.meeting.services.StorageService;

@Controller
public class SkimoMeetingController {

	private final StorageService storageService;
	
    private static final Logger LOGGER=LoggerFactory.getLogger(SkimoMeetingController.class);


	@Autowired
	public SkimoMeetingController(StorageService storageService)
	{
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException 
	{
		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(SkimoMeetingController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));
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

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) 
	{		
		try 
		{
			if(EngineStatus.isBusy())
					redirectAttributes.addFlashAttribute("message", "Skimo Engine is currently busy. Please try after sometime");
			else
			{
				storageService.store(file);
				String assetId = AssetInformation.create("./upload-dir/" , file.getOriginalFilename());

				if(!assetId.equalsIgnoreCase("present"))
				{
					SceneDetector.generateFirst(file.getOriginalFilename(), assetId);
					SceneDetector.generateThumbnail(file.getOriginalFilename(), assetId);
					SceneDetector.generateSkimo(file.getOriginalFilename(), assetId);
				    redirectAttributes.addFlashAttribute("message", "Skimo is being generated for " + file.getOriginalFilename());
				}
				else
					redirectAttributes.addFlashAttribute("message", "Skimo is already available for " + file.getOriginalFilename() + "!");
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) 
	{
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/skimo/{assetId}")
	public String viewMedia( Model model,@PathVariable(name="assetId") String assetId )
	{
		File dir = new File("public/" + assetId);
		
		if(dir.exists())
		{
			ArrayList<Skimo> skimoList = new ArrayList<Skimo>()
			{
				{
					add( new Skimo( "../8fc4e728/img/frames1.jpg", "../8fc4e728/source.mp4"));
					add( new Skimo( "../8fc4e728/img/frames2.jpg", "../8fc4e728/source.mp4"));
					add( new Skimo( "../8fc4e728/img/frames3.jpg", "../8fc4e728/source.mp4"));
					add( new Skimo( "../8fc4e728/img/frames4.jpg", "../8fc4e728/source.mp4"));
				}
			};
			model.addAttribute( "mediaList", skimoList );
			return "index";
		}
		return "404";
	}

}
