package aiss.videominer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    // Para que puedas filtrar vídeos por nombre (tu tarea de filtros)
    Page<Video> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
