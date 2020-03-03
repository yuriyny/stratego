package caramel.stratego.game.controller;

import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Game;
import caramel.stratego.game.domain.Piece;
import caramel.stratego.game.seralizer.GameDTO;
import caramel.stratego.game.service.GameService;
import caramel.stratego.game.storage.GameRepository;
import caramel.stratego.game.util.GameState;
import caramel.stratego.user.storage.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Controller
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/game")
    public String getGamePage(){
        return "stratego-board";
    }
    /**
     *
     * @param game
     * @return game (client receives object as JSON object)
     */
    @PostMapping(value = "/game/move")
    @ResponseBody
    public GameDTO updateGame(@RequestBody GameDTO game, Principal principal) {

        int gameTurn=game.getTurn();
        long gameId=game.getGameID();                             // AFTER PULL FROM FRONTEND ADD THIS

        Board updatedBoard;
        Board userMoveBoard=game.getBoardState();
        userMoveBoard.setStepNumber(gameTurn);                      //save turn number as step number in userMoveBoard because front end doesn't save it into board object

        if (gameTurn == 0) {
            Game newGame=new Game();                                                    //create new game
            gameService.saveGame(newGame);                                              //save so new id is generated
            gameId= newGame.getGameId();                                                //save game id so can be used later
            game.setGameID(gameId);                                                     //set the return gameID so future plays will have this ID as well

            newGame.setUser(gameService.getCurrentUser(principal));                     //save the user in the game
            gameService.saveGame(newGame);
            updatedBoard=gameService.setUpAiBoardConfig(userMoveBoard,newGame);         //update board and set turn to 0
            newGame.getBoardHistory().add(updatedBoard);                                //adding board to board list in game (unsure)
            gameService.saveGame(newGame);                                              //save game
        } else {
            Game gameObject =gameService.getGame(gameId);
            if(gameService.isValidUserAction(gameObject)){
                //USER DATABASE UPDATE, ADD BOARD TO GAME, AND CHECK IF GAME OVER
                gameService.executePlayerTurn(userMoveBoard,gameObject,gameTurn);                               //put user board into database
                gameObject.getBoardHistory().add(userMoveBoard);                            //Add userBoard and aiBoardinto boardHistory
                gameService.saveGame(gameObject);                                           //save game
                if(gameService.checkGameState(userMoveBoard)!=GameState.GAME_NOT_END){
                    game.setGameOver(true);
                    game.setBoardState(userMoveBoard);
                    return game;
                }
                //AI DATABASE UPDATE, ADD BOARD TO GAME, AND CHECK IF GAME OVER
                game.setTurn(game.getTurn()+1);                                             //increment turn for the AI turn
                updatedBoard = gameService.executeAiTurn(userMoveBoard,game.getUserRevealedPieces(),game.getTurn());                    //update with AI move and put into database
                gameObject.getBoardHistory().add(updatedBoard);                             //adding board to board list in game (unsure if this will update )
                gameService.saveGame(gameObject);                                           //save into gameRepository
                if(gameService.checkGameState(updatedBoard)!=GameState.GAME_NOT_END) {
                    game.setGameOver(true);
                    game.setBoardState(userMoveBoard);
                    game.setTurn(gameTurn+1);
                    return game;
                }
            }else{ //send previous board back to the front end to reset
                updatedBoard=new Board();
                updatedBoard.setPositions(gameObject.getBoardHistory().get(gameObject.getBoardHistory().size()-1).getPositions());

            }
            game.setTurn(gameTurn+1);                                                   //Increment turn
        }

        //creating a new board to send back without all the junk
        Board board=new Board();
        HashMap<String, Piece> temp = new HashMap<>(updatedBoard.getPositions());
        board.setPositions(temp);
        game.setBoardState(board);
        game.setGameOver(false);

        // ### if game is over, retrieve game stats from database (how many pieces lost, game duration, etc.)
//            game.setGameState(gameService.retrieveGameState());
//            game.setBoard(gameService.getCurrentBoard());
//            return game;
//        }
        return game;

    }

}
