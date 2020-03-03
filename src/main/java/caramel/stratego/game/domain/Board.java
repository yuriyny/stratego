package caramel.stratego.game.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Board class that contains information of game pieces and their current grid position
 */
@Getter
@Setter
@Entity
@Table(name="board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="board_id")
    private Long boardId;
    @JsonManagedReference
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY ,mappedBy = "board")
    @MapKeyColumn(name="key")
    private Map<String,Piece> positions;
    //initial board should have 0 steps
    @Column(name="step_number")
    private int stepNumber;
    @ManyToOne
    @JoinColumn(name="game_id")
    private Game game;

    public Board(){
        positions = new HashMap<>();
    }
}
