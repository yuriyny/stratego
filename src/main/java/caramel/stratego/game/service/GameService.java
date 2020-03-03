package caramel.stratego.game.service;

import caramel.stratego.game.domain.Board;
//import caramel.stratego.game.storage.BoardRepository;
import caramel.stratego.game.domain.Game;
import caramel.stratego.game.storage.BoardRepository;
import caramel.stratego.game.storage.GameRepository;
import caramel.stratego.game.domain.Piece;
import caramel.stratego.game.storage.PieceRepository;
import caramel.stratego.game.util.GameState;
import caramel.stratego.user.User;
import caramel.stratego.user.storage.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Getter
@Setter
@Service
public class GameService {

    private final MoveService moveService;

    @Autowired
    public GameService( MoveService moveService) {
        this.moveService = moveService;
    }
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PieceRepository pieceRepository;

    public User getCurrentUser(Principal principal){
        return userRepository.findByUsername(principal.getName());
    }
    public void saveGame(Game game){
        gameRepository.save(game);
    }
    public Game getGame(long gameID){
        return gameRepository.findById(gameID).get();
    }
    public void executePlayerTurn(Board board, Game game,int step) {//step and stuff should be handled
        board.setGame(game);
        board.setStepNumber(step);
        board.setPositions(board.getPositions());
        boardRepository.save(board);
    }

    public Board executeAiTurn(Board board,HashMap<String,Piece>revealed, int step) {//make sure gameid is different

        Board updatedBoard= moveService.updateBoard(board,"ai",revealed);;
        HashMap<String, Piece> updatedMap = new HashMap<>(updatedBoard.getPositions());
        updatedBoard.setPositions(updatedMap);
        updatedBoard.setStepNumber(step);
        boardRepository.save(updatedBoard);
        return updatedBoard;
    }

    /**
     * This is the initial setup of the board. Method will return User and Computer board setup.
     * @param board     This is the board only containing the user setup. It currently is not linked to a game.
     * @return          Full board setup with the game set and the step number set to 0.
     */

    public Board setUpAiBoardConfig(Board board, Game game) {
        Board initialSetup = moveService.configureCpuInitialSetup(board);    //Full board with boardID
        initialSetup.setStepNumber(0);                                          //set step to 0
        initialSetup.setGame(game);                                             //set game THIS CAUSES ISSUE for frontend to read
        boardRepository.save(initialSetup);                                     //save board
        return initialSetup;
    }

    /**
     * This method will call compareBoard to check to see if the last 4 plays by user have been revolved
     * around the same 2 locations.
     * @return false if last 4 user plays have been revolved around the same 2 location
     */
    public boolean isValidUserAction(Game game) {//only checks for user
        System.out.println("LOOK HERE");
        //check if there's been at least 7 plays total
        if (Math.min(game.getBoardHistory().size(), 7)!=7)
            return true;
        //retrieve the last 7 boards including the current approved move from front end
        List<Board> boards=boardRepository.getLastGameSteps(game.getGameId(),7);
        //Last four moves : 0-1 2-3 4-5 6-7
        String pieceRank = null;
        String start=null;
        String end=null;

        for(int boardIt=0;boardIt<7;boardIt+=2){ //assuming the last play is by person you are checking if move is valid
            System.out.println(boards.get(boardIt).getBoardId());
            System.out.println(boards.get(boardIt+1).getBoardId());
            System.out.println(boards.get(boardIt).getPositions().keySet().size());
            Board newBoard=boardRepository.findById(boards.get(boardIt).getBoardId()).get();
            System.out.println(boards.get(boardIt).getPositions().keySet().size());
            Dictionary<String,String> dictChange=compareBoard(boardRepository.findById(boards.get(boardIt).getBoardId()).get(),boardRepository.findById(boards.get(boardIt+1).getBoardId()).get());
            if(boardIt==0){
                pieceRank=dictChange.get("movingRank");
                start=dictChange.get("startLocation");
                end=dictChange.get("endLocation");
                System.out.println(pieceRank+" "+start+" "+end);//THESE ARE ALL NULLS FOR SOME REASON
            }else{
                System.out.println(dictChange.get("movingRank")+ " "+dictChange.get("startLocation")+ " "+dictChange.get("endLocation"));
                if(!pieceRank.equals(dictChange.get("movingRank")))
                    return true;
                else if(!(start.equals(dictChange.get("startLocation")) &&end.equals(dictChange.get("endLocation"))) &&
                        !(end.equals(dictChange.get("startLocation")) &&start.equals(dictChange.get("endLocation"))) )
                    return true;
            }
        }return false;
    }

    /**
     * This will find the current game state and return a GameState enumm
     * @param board         This board object will contain positions and pieces
     * @return              GameState of the current board. This will be PLAYER_WIN || PLAYER_LOSE || DRAW || GAME_NOT_END
     */
    public GameState checkGameState(Board board) {
        Map<String,Piece> position=board.getPositions();
        int numMovableAi = 0;
        int numMovableUser = 0;
        int minerCount = 0;
        String flagAiLocation = null;
        String flagUserLocation = null;

        for (String key : position.keySet()) {
            int rank=position.get(key).getRank();
            if (rank > 0 && rank <= 10) {
                if (position.get(key).getOwner().equals("ai"))
                    numMovableAi += 1;
                else
                    numMovableUser += 1;
            }
            if (rank == 3)
                minerCount += 1;
            else if (rank == 0 && position.get(key).getOwner().equals("ai"))
                flagAiLocation = key;
            else if (rank == 0)
                flagUserLocation = key;
        }
        //HANDLE CASES
        if((numMovableAi==0 && numMovableUser==0 ))
            return GameState.DRAW;
        else if(flagAiLocation==null || numMovableAi==0)
            return GameState.PLAYER_WIN;
        else if(flagUserLocation==null || numMovableUser==0)
            return GameState.PLAYER_LOSS;
        else if((minerCount == 0 && ifSurroundedByBombOrLake(flagAiLocation, position) && ifSurroundedByBombOrLake(flagUserLocation, position)))
            return GameState.DRAW; //check if flag is surrounded by bomb and neither player has miner
//        else if(boardRepository.getLastGameSteps(Math.min()))
        else
            return GameState.GAME_NOT_END;
        //if no attack has been made in 30 turns, draw
        // check more squared rule (https://en.wikipedia.org/wiki/Stratego#Rules_of_movement[5])
    }

    //---------------------------------------HELPER METHODS----------------------------------------------------//

    /**
     * This method compares the two boards. There will be two locations where the ranks are not equal.
     * @param prevBoard         This is the board before the change
     * @param currBoard         This is the board after the change
     * @return returnDict       This is a dictionary if startLocation, endLocation, and movingRank
     */
    public Dictionary<String, String> compareBoard(Board prevBoard, Board currBoard){
        Map<String, Piece> prevBoardState = prevBoard.getPositions();
        Map<String,Piece> newBoardState = currBoard.getPositions();
        Hashtable <String,String>returnDict = new Hashtable<>();
        System.out.println(prevBoardState.keySet().size());//why is size 0?
        for(String position : prevBoardState.keySet()) {
            System.out.println(position);
            Piece prev = prevBoardState.get(position);
            Piece curr = newBoardState.get(position);
            if (prev.getRank() != curr.getRank() || !prev.getOwner().equals( curr.getOwner())){        //if rank or owner differs
                System.out.println("CHANGE OCCURRED");
                System.out.println(position);
                if (curr.getRank()== -1) //this position is the start position
                    returnDict.put("startLocation", position);
                else {
                    returnDict.put("endLocation", position);
                    returnDict.put("movingRank", Integer.toString(curr.getRank()));
                }
            }
        }return returnDict;
    }

    /**
     * This method is used to see if a location is surrounded by a bomb or lake. If it is, then flag is fully protected.
     * It is used for checking if bombs surround the flag in the checkGameState method.
     * @param location      of center piece
     * @param position      hashmap of the board
     * @return boolean      if location is surrounded by the rank piece
     */
    private boolean ifSurroundedByBombOrLake(String location, Map<String, Piece> position){
        List <String>lake=Arrays.asList("c4","c5","d4","d5","g4","g5","h4","h5");
        char firstChar=location.charAt(0);
        char secondChar=location.charAt(1);
        for(int i=-1;i<=1;i+=2){
            String locationToCheck;
            if(firstChar+i>='a' && firstChar+i<='j'){
                locationToCheck=Character.toString((char)(firstChar+i))+secondChar;
                if(position.get(locationToCheck).getRank()!= 11 && !lake.contains(locationToCheck) )
                    return false;
            }
            if(secondChar+i>='0' && secondChar+i<='9'){
                locationToCheck=firstChar+Character.toString((char)(secondChar+i));
                if(position.get(locationToCheck).getRank()!= 11 && !lake.contains(locationToCheck))
                    return false;
            }
        }return true;
    }
}





