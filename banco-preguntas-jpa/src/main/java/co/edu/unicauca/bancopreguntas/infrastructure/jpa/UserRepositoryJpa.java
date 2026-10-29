package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.User;
import co.edu.unicauca.bancopreguntas.domain.repository.UserRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringUserDataRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepositoryJpa implements UserRepository {
    private final SpringUserDataRepository data;

    public UserRepositoryJpa(SpringUserDataRepository data) { this.data = data; }

    @Override
    public User save(User user) {
        return CatalogMapper.toDomain(data.save(CatalogMapper.toRecord(user)));
    }

    @Override
    public Optional<User> findById(Long id) {
        return data.findById(id).map(CatalogMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.findByEmail(email).map(CatalogMapper::toDomain);
    }
}
