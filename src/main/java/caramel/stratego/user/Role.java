package caramel.stratego.user;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="role_id")

    private Long id;
    private String name;
    @ManyToMany(mappedBy = "_roles")
    @Column(name="user_id")
    private Set<User> users;
}