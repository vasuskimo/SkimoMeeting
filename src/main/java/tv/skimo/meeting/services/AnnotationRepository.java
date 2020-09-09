package tv.skimo.meeting.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tv.skimo.meeting.model.Annotation;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {

	Optional<Annotation> findByAssetIdAndEmail(String assetId, String email);
}