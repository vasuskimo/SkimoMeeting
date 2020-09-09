package tv.skimo.meeting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tv.skimo.meeting.model.Annotation;

import java.util.List;
import java.util.Optional;

@Service
public class AnnotationService {
    private final AnnotationRepository annotationRespository;

    @Autowired
    public AnnotationService(AnnotationRepository annotationRepository) {
        this.annotationRespository = annotationRepository;
    }


    public List<Annotation> findAll() {
        return annotationRespository.findAll();
    }

    public Optional<Annotation> findById(Long id) {
        return annotationRespository.findById(id);
    }

    public Annotation save(Annotation stock) {
        return annotationRespository.save(stock);
    }

    public void deleteById(Long id) {
        annotationRespository.deleteById(id);
    }
}