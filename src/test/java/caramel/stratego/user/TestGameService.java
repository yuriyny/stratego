package caramel.stratego.user;

import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Piece;
import caramel.stratego.game.service.GameService;
import caramel.stratego.game.util.GameState;
//import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

/* ALL TESTS ARE PASSED*/

//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class TestGameService {
//    @Autowired
//    private GameService gameService=new GameService();
//    private Piece piece1=new Piece();
//    private Piece piece2=new Piece();
//    private Piece piece3=new Piece();
//    private Piece piece4=new Piece();
//    private Piece piece5=new Piece();
//    private Piece piece6=new Piece();
//    private Piece piece7=new Piece();
//    private Board board=new Board();
//    private Board board1=new Board();
//    private Board board2=new Board();
//
//    @Test
//    public void compareBoardTest(){ //compares 2 boards with ONLY 2 different locations. Figures out startLocation, endLocation, and movingRank
//
//        HashMap<String, Piece> position=new HashMap<>();
//        board1.setPositions(position);
//
//        piece1.setRank(10);
//        piece1.setOwner("ai");
//
//        piece2.setRank(-1);
//        piece2.setOwner("-1");
//
//        position.put("a1",piece1);//piece1 rank 10 at a1
//        position.put("a2",piece2);//piece2 rank -1 at a2
//
//
//        HashMap<String, Piece> position2=new HashMap<>();
//        board2.setPositions(position2);
//
//        position2.put("a1",piece2);//piece1 rank 10 at a1
//        position2.put("a2",piece1);//piece2 rank -1 at a2
//
//        Dictionary<String,String> result=gameService.compareBoard(board1,board2);
//        Assert.assertEquals(result.get("startLocation"),"a1");
//        Assert.assertEquals(result.get("endLocation"),"a2");
//        Assert.assertEquals(result.get("movingRank"),"10");
//    }
//    @Test
//    public void oneFlagRemaining(){//AI: one flag and one moving piece
//        HashMap<String, Piece> position=new HashMap<>();
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//        piece2.setRank(1);
//        piece2.setOwner("ai");
//        position.put("a1",piece1);
//        position.put("a2",piece2);
//        board.setPositions(position);
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result,GameState.PLAYER_LOSS);
//    }
//    @Test
//    public void onlyTwoFlagsRemaining(){ //Two flags only
//        HashMap<String, Piece> position=new HashMap<>();
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//        position.put("a1",piece1);
//        position.put("a2",piece2);
//        board.setPositions(position);
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result, GameState.DRAW);
//    }
//    @Test
//    public void gameNotOver(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(3);
//        piece3.setOwner("player");
//
//        piece4.setRank(3);
//        piece4.setOwner("ai");
//
//        position.put("a1",piece1);
//        position.put("a2",piece2);
//        position.put("a3",piece3);
//        position.put("a4",piece4);
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result,GameState.GAME_NOT_END);
//    }
//    @Test
//    public void surroundFlagNoMiner(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(11);
//        piece3.setOwner("player");
//
//        piece4.setRank(11);
//        piece4.setOwner("ai");
//
//        piece5.setRank(5);
//        piece5.setOwner("player");
//
//        piece6.setRank(5);
//        piece6.setOwner("ai");
//
//
//        position.put("a1",piece1);
//        position.put("a0",piece4);
//        position.put("b1",piece4);
//        position.put("a2",piece4);
//
//        position.put("e8",piece2);
//        position.put("d8",piece3);
//        position.put("f8",piece3);
//        position.put("e7",piece3);
//        position.put("e9",piece3);
//
//        position.put("g6",piece5);
//        position.put("d6",piece6);
//
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result,GameState.DRAW);
//    }
//
//    @Test
//    public void surroundFlagNoMinerCornerCase(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(11);
//        piece3.setOwner("player");
//
//        piece4.setRank(11);
//        piece4.setOwner("ai");
//
//        piece5.setRank(5);
//        piece5.setOwner("player");
//
//        piece6.setRank(5);
//        piece6.setOwner("ai");
//
//
//        position.put("a0",piece1);
//        position.put("a1",piece4);
//        position.put("b0",piece4);
//
//        position.put("j9",piece2);
//        position.put("j8",piece3);
//        position.put("i9",piece3);
//
//        position.put("g6",piece5);
//        position.put("d6",piece6);
//
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result,GameState.DRAW);
//    }
//
//    @Test
//    public void flagNotFullySurroundedByBomb(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(11);
//        piece3.setOwner("player");
//
//        piece4.setRank(11);
//        piece4.setOwner("ai");
//
//        piece5.setRank(5);
//        piece5.setOwner("player");
//
//        piece6.setRank(5);
//        piece6.setOwner("ai");
//
//        piece7.setRank(-1);
//        piece7.setOwner("-1");
//
//
//        position.put("a0",piece1);
//        position.put("a1",piece4);
//        position.put("b0",piece4);
//
//        position.put("j9",piece2);
//        position.put("j8",piece3);
//        position.put("i9",piece7);//empty location
//
//        position.put("g6",piece5);
//        position.put("d6",piece6);
//
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result,GameState.GAME_NOT_END);
//    }
//
//    @Test
//    public void surroundFlagNoMinerWithBombAndLake(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(11);
//        piece3.setOwner("player");
//
//        piece4.setRank(11);
//        piece4.setOwner("ai");
//
//        piece5.setRank(5);
//        piece5.setOwner("player");
//
//        piece6.setRank(5);
//        piece6.setOwner("ai");
//
//        piece7.setRank(-1);
//        piece7.setOwner("-1");
//
//
//        position.put("c3",piece1);
//        position.put("b3",piece4);
//        position.put("d3",piece4);
//        position.put("c2",piece4);
//        position.put("c4",piece7);
//
//        position.put("c6",piece2);
//        position.put("b6",piece3);
//        position.put("d6",piece3);
//        position.put("c7",piece3);
//        position.put("c5",piece7);
//
//        position.put("g6",piece5);
//        position.put("d9",piece6);
//
//        GameState result=gameService.checkGameState(board);
//        Assert.assertEquals(result,GameState.DRAW);
//    }
//
//    @Test
//    public void testFindPossibleLocation(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(11);
//        piece3.setOwner("player");
//
//        piece4.setRank(11);
//        piece4.setOwner("ai");
//
//        piece5.setRank(2);
//        piece5.setOwner("player");
//
//        piece6.setRank(5);
//        piece6.setOwner("ai");
//
//        piece7.setRank(-1);
//        piece7.setOwner("-1");
//
//
//        position.put("c3",piece5);
//        position.put("c4",piece7);
//        position.put("b3",piece7);
//        position.put("c2",piece7);
//        position.put("d3",piece7);
//
//        position.put("c0",piece7);
//        position.put("c1",piece7);
//        position.put("c5",piece7);
//        position.put("c6",piece7);
//        position.put("c7",piece7);
//        position.put("c8",piece7);
//        position.put("c9",piece7);
//
//        position.put("a3",piece7);
//        position.put("e3",piece7);
//        position.put("f3",piece7);
//        position.put("g3",piece7);
//        position.put("h3",piece7);
//        position.put("i3",piece7);
//        position.put("j3",piece7);
//
//        List<String> result=gameService.findPossibleLocations("c3",position);
//        System.out.println(result);
////        Assert.assertEquals(result,GameState.DRAW);
//    }
//    @Test
//    public void testFindPossibleLocationWithPiece(){
//        HashMap<String, Piece> position=new HashMap<>();
//        board.setPositions(position);
//
//        piece1.setRank(0);
//        piece1.setOwner("ai");
//
//        piece2.setRank(0);
//        piece2.setOwner("player");
//
//        piece3.setRank(11);
//        piece3.setOwner("player");
//
//        piece4.setRank(11);
//        piece4.setOwner("ai");
//
//        piece5.setRank(2);
//        piece5.setOwner("player");
//
//        piece6.setRank(5);
//        piece6.setOwner("ai");
//
//        piece7.setRank(-1);
//        piece7.setOwner("-1");
//
//
//        position.put("c3",piece6);
//        position.put("c4",piece7);
//        position.put("b3",piece7);
//        position.put("c2",piece5);
//        position.put("d3",piece7);
//
//        position.put("c0",piece7);
//        position.put("c1",piece7);
//        position.put("c5",piece7);
//        position.put("c6",piece7);
//        position.put("c7",piece7);
//        position.put("c8",piece7);
//        position.put("c9",piece7);
//
//        position.put("a3",piece7);
//        position.put("e3",piece7);
//        position.put("f3",piece7);
//        position.put("g3",piece7);
//        position.put("h3",piece7);
//        position.put("i3",piece7);
//        position.put("j3",piece7);
//
//        List<String> result=gameService.findPossibleLocations("c3",position);
//        System.out.println(result);
////        Assert.assertEquals(result,GameState.DRAW);
//    }
//}

