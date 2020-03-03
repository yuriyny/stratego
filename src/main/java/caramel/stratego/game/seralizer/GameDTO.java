package caramel.stratego.game.seralizer;

import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Piece;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class GameDTO {

    private HashMap<String, Piece> userRevealedPieces;
    private HashMap<String, Piece> aiRevealedPieces;
    private Board boardState;
    private boolean isAutoplay;
    private int turn;
    private long gameID;
    private boolean isGameOver;
}
