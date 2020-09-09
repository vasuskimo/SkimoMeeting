package tv.skimo.meeting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import tv.skimo.meeting.model.Annotation;
import tv.skimo.meeting.services.AnnotationService;

import java.util.List;


@Controller
public class AnnotationAPI 
{
    private final AnnotationService annotationService;

    @Autowired
    public AnnotationAPI(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping("/live/annotations")
    public ResponseEntity<List<Annotation>> findAll() {
        return ResponseEntity.ok(annotationService.findAll());
    }

    @PostMapping("/live/annotations")
    public ResponseEntity<?> create(@RequestBody Annotation annotation,
			@RequestParam(name = "apikey") String apikey) 
    {
	    if(!apikey.equalsIgnoreCase("yKLxpeweS42A78"))
	    	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect API Key");
    	return ResponseEntity.status(HttpStatus.CREATED).body(annotationService.save(annotation));
    }

}