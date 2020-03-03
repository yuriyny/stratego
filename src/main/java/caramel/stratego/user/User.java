package caramel.stratego.user;

import caramel.stratego.game.domain.Game;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Table (name="user")
public class User {
    @Id
    @Column(length=200, name="username")
    @NotEmpty
    @Size(max=200)
    private String username;
    @NotEmpty
    @Size(max=255)
    @Column(name="password")
    private String password;
    @Column(name="enabled")
    private boolean enabled;
    @Column(name="active")
    private int active;
    @OneToMany(mappedBy = "user")
    private List<Game> games;
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> _roles;

}