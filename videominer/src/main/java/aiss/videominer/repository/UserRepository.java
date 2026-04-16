package aiss.videominer.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import aiss.videominer.model.User;

@Repository
public class UserRepository {
    private List<User> users = new ArrayList<>();

    public List<User> findAll() {
        return users;
    }

    public User findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Método para poder guardar usuarios que vengan de los mineros
    public void save(User user) {
        users.add(user);
    }
}

