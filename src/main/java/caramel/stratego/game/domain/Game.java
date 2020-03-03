package caramel.stratego.game.domain;

import caramel.stratego.game.controller.GameController;
import caramel.stratego.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="game_id")
    private Long gameId;
    @ManyToOne
    @JoinColumn(name="username")
    private User user;
    @Column(name = "start_time")
    private String startTime;

    //the list of boards starting with the game will be stored here
    //when game is started the first board with zero steps will be stored
    @OneToMany(mappedBy = "game")
    private List<Board> boardHistory;
    @Transient
    private final SimpleDateFormat formatter;
    @Transient
    private final Date date;

    public Game(){
        this.boardHistory = new ArrayList<>();
        this.date = new Date();
        this.formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.startTime = formatter.format(date);
        }
}
