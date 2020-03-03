package caramel.stratego.game.storage;

import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Piece;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PieceRepository extends CrudRepository<Piece, String> {
    @Query(value="SELECT * from piece p where board_id=?1",nativeQuery = true )
    List<Piece> getAllBoardPieces(Long board_id);
}
