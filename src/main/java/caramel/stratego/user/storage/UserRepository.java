package caramel.stratego.user.storage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import caramel.stratego.user.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findByUsername(@Param("username") String username);
}