package caramel.stratego.replay.controller;


import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Game;
import caramel.stratego.game.storage.BoardRepository;
import caramel.stratego.game.storage.GameRepository;
import caramel.stratego.replay.seralizer.ReplayDTO;
import caramel.stratego.replay.seralizer.ReplayListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.*;

import java.util.List;

@Controller
public class ReplayController {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/replay")
    public String getReplayList(@ModelAttribute("replays") ReplayListDTO replayDTOList){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getPrincipal().toString();
        //assigning values for replaylist
        //using database query
        List<Game> games = gameRepository.findUserGames(username);
        for(Game g : games){
            ReplayDTO replayDTO = new ReplayDTO();
            replayDTO.setGameId(g.getGameId());
            replayDTO.setTimestamp(g.getStartTime());
            replayDTOList.getReplayDTOList().add(replayDTO);
        }
        return "replay";
    }
    @PostMapping("/replay/{id}")
    @ResponseBody
    public List<Board> getBoardById(@PathVariable Long id){
        List<Board> boards = boardRepository.getAllGameBoards(id);

        return boards;
    }

}
