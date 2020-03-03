//package caramel.stratego.user.controller;
//
//import caramel.stratego.game.domain.Board;
//import caramel.stratego.game.domain.Game;
//import caramel.stratego.game.domain.Piece;
//import caramel.stratego.game.storage.BoardRepository;
//import caramel.stratego.game.storage.GameRepository;
//import caramel.stratego.user.User;
//import caramel.stratego.game.storage.PieceRepository;
//import caramel.stratego.user.storage.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TestClass {
//    UserController uc;
//    public TestClass(UserController uc){
//        this.uc = uc;
//
//    }
//    @Autowired
//    private UserRepository userRepository;
//
//    public void populateTables(){
//        //ALL GAMES FOR USER ROB START HERE===============
//        User u = new User();
//        u.setUsername("Rob");
//        u.setPassword("dfhdhdd");
//        Game g = new Game();
//        g.setUser(u);
//
//        Board bb = new Board();
//        bb.setStepNumber(511);
//        bb.setGame(g);
//        Board bb2 = new Board();
//        bb2.setStepNumber(611);
//        bb2.setGame(g);
//        Board bb3 = new Board();
//        bb3.setStepNumber(711);
//        bb3.setGame(g);
//        //adding boards to the created game
//        g.getBoardHistory().add(bb);
//        g.getBoardHistory().add(bb2);
//        g.getBoardHistory().add(bb3);
//
//        Piece p = new Piece();
//        p.setRank(8);
//        p.setOwner("AI");
//        p.setKey("B5");
//        p.setBoard(bb);
//        Piece p2 = new Piece();
//        p2.setRank(1);
//        p2.setOwner("AI");
//        p2.setKey("B1");
//        p2.setBoard(bb);
//        Piece p3 = new Piece();
//        p3.setRank(5);
//        p3.setOwner("AI");
//        p3.setKey("B2");
//        p3.setBoard(bb);
//        Piece p4 = new Piece();
//        p4.setOwner("AI");
//        p4.setKey("B3");
//        p4.setRank(9);
//        p4.setBoard(bb);
//        //set the pieces to the board
//        bb.getPositions().put(p.getKey(),p);
//        bb.getPositions().put(p2.getKey(),p2);
//        bb.getPositions().put(p3.getKey(),p3);
//        bb.getPositions().put(p4.getKey(),p4);
//
//        uc.userDomainService.saveUser(u);
//        uc.gameRepository.save(g);
//        uc.boardRepository.save(bb);
//        uc.boardRepository.save(bb2);
//        uc.boardRepository.save(bb3);
//        uc.pieceRepository.save(p);
//        uc.pieceRepository.save(p2);
//        uc.pieceRepository.save(p3);
//        uc.pieceRepository.save(p4);
//        //ALL GAMES FOR USER SARA START HERE===============
//
//    }
//    //testing created queries in repository interfaces
//    public void queryCalls(){
//
//        List<Game> lg = uc.gameRepository.findAllGames();
//        for(Game g3 : lg){
//            System.out.println("============" + g3.getGameId() +"  "+ g3.getUser().getUsername() );
//
//        }
//        //finding all user games using username
//        List<Game> lg2 = uc.gameRepository.findUserGames("Rob");
//        for(Game g3 : lg2){
//            System.out.println("============" + g3.getGameId() +"  "+ g3.getUser().getUsername() );
//
//        }
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        //get the last step(board) of specific game (gameid is used as an argument)
//        List<Board> lastStep = uc.boardRepository.getLastGameStep(1L);
//        for(Board g3 : lastStep){
//            System.out.println("============" + g3.getBoardId() +"  "+ g3.getStepNumber());
//        }
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        List<Board> lastSteps = uc.boardRepository.getLastGameSteps(1L, 5);
//        for(Board g3 : lastSteps){
//            System.out.println("============" + g3.getBoardId() +"  "+ g3.getStepNumber());
//
//        }
//
//    }
//    //creates a hashmap for specific board from database
//    //also prints out all hash map to terminal
//    public void getboardMap(){
//        Map<String,Piece> positions = new HashMap<>();
//        System.out.println("=====All pieces for the board: ======");
//        List<Piece> boardPieces = uc.pieceRepository.getAllBoardPieces(2L);
//        for(Piece piece : boardPieces){
//            //putting elements into hash map
//            positions.put(piece.getKey(),piece);
//            System.out.println("============key:" + piece.getKey() +" rank:  "+ piece.getRank());
//    //now we have all piece positions of specific board
//        }
//    }
//}
