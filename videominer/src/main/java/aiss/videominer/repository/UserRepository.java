package aiss.videominer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Este método para filtrar por nombre (ignora mayúsculas/minúsculas)
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
