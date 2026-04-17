package aiss.videominer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aiss.videominer.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
