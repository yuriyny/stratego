package caramel.stratego.game.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Piece represents a Stratego game piece that is owned by either human player or AI
 */
@Getter
@Setter
@Entity
public class Piece implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long piece_id;
    private int rank;
    private String owner;
    @JoinColumn(name="key")
    private String key;//position
    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="board_id")
    private Board board;

}
