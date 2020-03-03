package caramel.stratego.game.storage;

import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends CrudRepository<Board,Long> {
    @Query("select b from Board b ")
    List<Board> findAllBoards();
    //return the last step/board object that took place in the game
    @Query(value="SELECT * FROM board b WHERE step_number=(SELECT MAX(step_number) FROM board b WHERE game_id=?1 limit 1)", nativeQuery = true )
    Board getLastGameStep(Long gameId);
    //this query returns specific last steps of the game
    //game is identified by its id
    @Query(value="SELECT * FROM board WHERE game_id=?1 ORDER BY step_number DESC limit ?2", nativeQuery = true )
    List<Board> getLastGameSteps(Long gameId, int n);
    //returns all game's boards
    @Query(value="SELECT * FROM board WHERE game_id=?1 ORDER BY step_number ASC", nativeQuery = true )
    List<Board> getAllGameBoards(Long game_id);


}
