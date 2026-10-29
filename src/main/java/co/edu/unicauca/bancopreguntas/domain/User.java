package co.edu.unicauca.bancopreguntas.domain;

import java.util.EnumSet;
import java.util.Set;

/** Usuario del banco de preguntas (autor, revisor, docente o administrador). */
public class User {
    private Long id;
    private String fullName;
    private String email;
    private final Set<Role> roles = EnumSet.noneOf(Role.class);

    public User() {}
    public User(Long id, String fullName, String email, Set<Role> roles) {
        this.id = id; this.fullName = fullName; this.email = email;
        if (roles != null) this.roles.addAll(roles);
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return Set.copyOf(roles); }
    public boolean hasRole(Role role) { return roles.contains(role); }
}
