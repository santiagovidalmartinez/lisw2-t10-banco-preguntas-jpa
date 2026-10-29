package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import co.edu.unicauca.bancopreguntas.domain.Role;
import jakarta.persistence.*;
import java.util.EnumSet;
import java.util.Set;

/** Usuario. Los roles se guardan como colección de elementos (sin tabla ni vida propia: ver informe 2.1). */
@Entity
@Table(name = "users")
public class UserRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    protected UserRecord() {}

    public UserRecord(String fullName, String email, Set<Role> roles) {
        this.fullName = fullName; this.email = email;
        if (roles != null && !roles.isEmpty()) this.roles = EnumSet.copyOf(roles);
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Set<Role> getRoles() { return roles; }
}
