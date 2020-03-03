package caramel.stratego.game.storage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import caramel.stratego.game.domain.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    //find all games of specific user
    @Query(value="SELECT * from game g where username =?1", nativeQuery = true)
    List<Game> findUserGames(String username);
    //returns game by username and start_time
    @Query(value="select * from game where username =?1 and start_time =?2 limit 1", nativeQuery = true)
    Game getGameByTime(String username, String start_time);
}
