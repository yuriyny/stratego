package caramel.stratego.game.service;

import caramel.stratego.game.domain.Board;
import caramel.stratego.game.domain.Piece;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Character.getNumericValue;

@Service
public class MoveService {

    private static final String CPU_NAME = "ai";
    private static final int NUM_BOARD_COLS = 10;
    private static final String FRONT_ROW_INDEX = "3";
    private static final List<Integer> soldierRanks = new ArrayList<>(
            Arrays.asList(1,2,3,4,5,6,7,8,9,10)
    );

    public Board configureCpuInitialSetup(Board startBoard) {
        Map<String, Piece> pieceLocations = startBoard.getPositions();
        Set<String> startPositions = new HashSet<>(initializeCpuStartPositions());

        for(String location : pieceLocations.keySet()) {
            Piece cpuPiece = pieceLocations.get(location);
            if (startPositions.contains(location)) {
                cpuPiece.setOwner(CPU_NAME);
            }

        }

        while(!startPositions.isEmpty()) {
            String flagPosition = placeFlag(pieceLocations, startPositions);
            placeBombs(pieceLocations, startPositions, flagPosition);
            placeSpy(pieceLocations, startPositions, flagPosition);
            placeScouts(pieceLocations, startPositions);
            placeMiners(pieceLocations, startPositions);
            placeSoldiers(pieceLocations, startPositions);
        }
        return startBoard;
    }

    public Board updateBoard(Board inputBoard, String player, HashMap<String, Piece> revealedEnemyPieces) {
        Map<String, Piece> currentBoardPositions = inputBoard.getPositions();
        // allied soldiers only
        Map<String, Piece> soldierLocations = findMobileSoldiers(currentBoardPositions, player);
        List<String> listOfSoldierLocations = new ArrayList<>(soldierLocations.keySet());


        String opponentName;
        if (player.equals(CPU_NAME)) opponentName = "player";
        else opponentName = CPU_NAME;

        // default moving piece is random
        int randomSoldierIndex = ThreadLocalRandom.current().nextInt(listOfSoldierLocations.size());
        String movingSoldierPosition = listOfSoldierLocations.get(randomSoldierIndex);

        // branch: if any opponent pieces are revealed
        if (isEnemyRevealed(revealedEnemyPieces)) {
            // handling opponent marshall
            if (isOpponentMarshalRevealed(revealedEnemyPieces)) {
                // move 1 and 10 to each other until at least 1 space apart
                // then advance them together, 10 first then 1

                List<String> positions = soldierLocations.entrySet().stream()
                        .filter(soldier ->
                                soldier.getValue().getOwner().equals(player) &&
                                        (soldier.getValue().getRank() == 10 || soldier.getValue().getRank() == 1))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                if (positions.size() == 2) {
                    Map<String, String> newPosition;
                    if (areMarshalSpySeparate(positions)) {
                        // pick random one then
                        newPosition = makeMarshalSpyMove(currentBoardPositions, positions, null, false);
                    } else {
                        String allySpyPosition = getPiecePositionByUserAndRank(currentBoardPositions, player, 1),
                                allyMarshalPosition = getPiecePositionByUserAndRank(currentBoardPositions, player, 10);

                        String enemyMarshalPosition = getPiecePositionByUserAndRank(currentBoardPositions, opponentName, 10);
                        int allySpyDistance = calculateGridDistance(allySpyPosition, enemyMarshalPosition),
                                allyMarshalDistance = calculateGridDistance(allyMarshalPosition, enemyMarshalPosition);

                        // advance 1 if behind 10, advance 10 otherwise
                        if (allySpyDistance < allyMarshalDistance) {
                            positions.set(0, allySpyPosition);
                            positions.set(1, allyMarshalPosition);
                        } else {
                            positions.set(0, allyMarshalPosition);
                            positions.set(1, allySpyPosition);

                        }
                        newPosition = makeMarshalSpyMove(currentBoardPositions, positions, enemyMarshalPosition, true);
                    }

                    if (newPosition != null) {
                        // do move
                        String selectedPiecePosition = (String) newPosition.keySet().toArray()[0];
                        moveSoldierToNewLocation(currentBoardPositions,
                                newPosition.get(selectedPiecePosition),
                                currentBoardPositions.get(selectedPiecePosition));
                        return inputBoard;
                    }

            }

        } else if (isOpponentSoldiersRevealed(revealedEnemyPieces)) { // any random soldier
            List<String> enemyLocations = new ArrayList<>(revealedEnemyPieces.keySet());
            Collections.shuffle(enemyLocations);
            String randomEnemyLocation = enemyLocations.get(0);
            int enemyRank = revealedEnemyPieces.get(randomEnemyLocation).getRank();
            if (strongAllySoldiersAvailable(soldierLocations, enemyRank)) {
                Map<String, Piece> strongBois = soldierLocations.entrySet().stream().filter(entry->entry.getValue().getRank() > enemyRank)
                                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                movingSoldierPosition = findClosestWinningSoldierPosition(strongBois, randomEnemyLocation);
            }

        } else { // handling bomb defuse
                List<String> bombLocations = locatePieces(revealedEnemyPieces, 11);
                Collections.shuffle(bombLocations);
                String randomBombLocation = bombLocations.get(0);

                List<String> minerLocations = locatePieces(soldierLocations, 3);
                String closestMinerPosition = findClosestLocation(minerLocations, randomBombLocation, currentBoardPositions);
                // if miner can be moved
                if (closestMinerPosition != null) {
                    movingSoldierPosition = closestMinerPosition;
                }
            }

        } else { // working off of no info
            // try to gain information by probing scout
            List<String> scoutLocations = locatePieces(soldierLocations, 2);
            if (!scoutLocations.isEmpty()) {
                randomSoldierIndex =  ThreadLocalRandom.current().nextInt(scoutLocations.size());
                movingSoldierPosition = scoutLocations.get(randomSoldierIndex);
            }
        }

        List<String> possibleLocations = findPossibleLocations(movingSoldierPosition, inputBoard.getPositions());
        int randomNewLocationIndex = ThreadLocalRandom.current().nextInt(possibleLocations.size());
        String newSoldierPosition = possibleLocations.get(randomNewLocationIndex);

        Piece movingSoldier = currentBoardPositions.get(movingSoldierPosition);
        Piece opposingSoldier = currentBoardPositions.get(newSoldierPosition);
        // initiate battle
        if (opposingSoldier.getRank() != -1) {
            if (getBattleResult(movingSoldier, opposingSoldier) == 1) {
                moveSoldierToNewLocation(currentBoardPositions, newSoldierPosition, movingSoldier);
            } else if (getBattleResult(movingSoldier, opposingSoldier) == 0) {
                removeSoldier(currentBoardPositions, newSoldierPosition);
            }
        } else { // move to free space
            moveSoldierToNewLocation(currentBoardPositions, newSoldierPosition, movingSoldier);
        }

        removeSoldier(currentBoardPositions, movingSoldierPosition);

        return inputBoard;
    }

    private String getPiecePositionByUserAndRank(Map<String, Piece> currentBoardPositions, String player, int rank) {

        String queriedPiecePosition = currentBoardPositions.entrySet().stream()
                .filter(soldier->soldier.getValue().getRank() == rank
                        && soldier.getValue().getOwner().equals(player))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());

        return queriedPiecePosition;
    }

    private Map<String, String> makeMarshalSpyMove(Map<String, Piece> currentBoardPositions, List<String> positions, String target, boolean isAttack) {


        String selectedPieceLocation, targetLocation;
        String firstPosition = positions.get(0), secondPosition = positions.get(1);
        List<String> possibleMoves;
        if (!findPossibleLocations(firstPosition, currentBoardPositions).isEmpty()) {
            selectedPieceLocation = firstPosition;
            targetLocation = secondPosition;
        } else if (!findPossibleLocations(secondPosition, currentBoardPositions).isEmpty()) {
            selectedPieceLocation = secondPosition;
            targetLocation = firstPosition;
        } else {
            return null;
        }

        if (isAttack){
            targetLocation = target;
        }

        possibleMoves = findPossibleLocations(selectedPieceLocation, currentBoardPositions);
        String newLocation = findClosestLocation(possibleMoves, targetLocation, currentBoardPositions);
        return new HashMap<String, String>(){{
            put(selectedPieceLocation, newLocation);
        }};
    }

    private boolean areMarshalSpySeparate(List<String> positions) {
        String firstPosition = positions.get(0), secondPosition = positions.get(1);
        return calculateGridDistance(firstPosition, secondPosition) > 2;
    }


    private boolean isOpponentMarshalRevealed(HashMap<String, Piece> revealedEnemyPieces) {
        return revealedEnemyPieces.entrySet().stream().map(entry->entry.getValue().getRank()).anyMatch(rank-> rank == 10);
    }

    private Map<String, Piece> findMobileSoldiers(Map<String, Piece> board, String player) {
        // filter mobile allied soldiers
        return board.entrySet().stream()
                                .filter(soldier->
                                            soldier.getValue().getOwner().equals(player)
                                                    && !findPossibleLocations(soldier.getKey(), board).isEmpty()
                                )
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<String> locatePieces(Map<String, Piece> soldierLocations, int rank) {
        List<Map.Entry<String, Piece>> pieces = new ArrayList<>(soldierLocations.entrySet());
        List<String> positions = pieces.stream().
                filter(e->e.getValue().getRank() == rank).map(e->e.getKey()).collect(Collectors.toList());
        return positions;
    }


    private int calculateGridDistance(String start, String target) {
        // naive distance calculation (ignores lakes)
        char startX = start.charAt(0), targetX = target.charAt(0), startY = start.charAt(1), targetY = target.charAt(1);
        int xDistance = Math.max(startX, targetX) - Math.min(startX, targetX);
        int yDistance = Math.max(startY, targetY) - Math.min(startY, targetY);
        return xDistance + yDistance;
    }

    private String findClosestLocation(List<String> startLocations, String target, Map<String, Piece> board) {
        // filter immovable pieces
        startLocations.stream().filter(e-> !findPossibleLocations(e, board).isEmpty());
        if (startLocations.isEmpty()) {
            return null;
        }
        Map<String, Integer> distances = startLocations.stream()
                .collect(Collectors.toMap(
                        Function.identity(), start -> calculateGridDistance(start, target)
                ));

        Map.Entry<String, Integer> closestLocation = distances.entrySet()
                                                                    .stream()
                                                                    .min(Comparator.comparing(Map.Entry::getValue))
                                                                    .get();
        return closestLocation.getKey();
    }

    private String findClosestWinningSoldierPosition(Map<String, Piece> soldierLocations, String enemyLocation) {
        List<String> listSoldierPositions = new ArrayList<>(soldierLocations.keySet());

        return findClosestLocation(listSoldierPositions, enemyLocation, soldierLocations);
    }

    private boolean strongAllySoldiersAvailable(Map<String, Piece> soldierLocations, int enemyRank) {
        return soldierLocations.values().stream().anyMatch(piece -> piece.getRank() > enemyRank);
    }

    private boolean isOpponentSoldiersRevealed(HashMap<String, Piece> revealedEnemyPieces) {
        return containsSoldiers(revealedEnemyPieces.entrySet());
    }

    private boolean containsSoldiers(Set<Map.Entry<String, Piece>> pieces) {
        return pieces.stream().map(entry->entry.getValue().getRank()).anyMatch(soldierRanks::contains);
    }

    private boolean isEnemyRevealed(HashMap<String, Piece> enemies) {
        return !enemies.isEmpty();
    }

    private Map<Integer, Integer> initializeArmyMap() {
        Map<Integer, Integer> armyMap = new HashMap<>();
        int sergeantRank = 4, lieutenantRank = 5, captainRank = 6,
                majorRank = 7, colonelRank = 8, generalRank = 9, marshalRank = 10;
        armyMap.put(sergeantRank, 4);
        armyMap.put(lieutenantRank, 4);
        armyMap.put(captainRank, 4);
        armyMap.put(majorRank, 3);
        armyMap.put(colonelRank, 2);
        armyMap.put(generalRank, 1);
        armyMap.put(marshalRank, 1);
        return armyMap;
    }

    private void placeSoldiers(Map<String, Piece> pieceLocations, Set<String> availablePositions) {
        Map<Integer, Integer> armyMap = initializeArmyMap();
            Set<Integer> armyRanks = armyMap.keySet();
            Iterator<Integer> it = armyRanks.iterator();
            while(it.hasNext()) {
                int rank = it.next();
                while (armyMap.get(rank) > 0) {
                    List<String> possibleLocations = availablePositions.stream().collect(Collectors.toList());
                    int randomPosition = ThreadLocalRandom.current().nextInt(possibleLocations.size());
                    String location = possibleLocations.remove(randomPosition);
                    assignPieceToLocation(pieceLocations, availablePositions, location, rank);

                    int count = armyMap.get(rank);
                    count--;
                    armyMap.put(rank, count);
                }
                it.remove();
            }
    }

    private void placeMiners(Map<String, Piece> pieceLocations, Set<String> availablePositions) {
        // 5 miners across back two rows
        int minerRank = 3;
        List<String> backRows = Arrays.stream(new String[]{"0", "1"}).collect(Collectors.toList());

        List<String> possibleMinerLocations = availablePositions.stream()
                .filter(location -> backRows.stream().anyMatch(location::contains))
                .collect(Collectors.toList());

        int minerCount = 5;
        while (minerCount > 0) {
            int randomMinerIndex = ThreadLocalRandom.current().nextInt(possibleMinerLocations.size());
            String scoutLocation = possibleMinerLocations.remove(randomMinerIndex);
            assignPieceToLocation(pieceLocations, availablePositions, scoutLocation, minerRank);
            minerCount--;
        }
    }

    private void placeScouts(Map<String, Piece> pieceLocations, Set<String> availablePositions) {
        // 4 scouts in front, 2 each in the next rows
        int scoutRank = 2;
        int rowIndex = Character.getNumericValue(FRONT_ROW_INDEX.charAt(0));
        for (int i = rowIndex; i > 0; i-- ) {
            final int row = i;
            List<String> possibleScoutLocations = availablePositions.stream()
                                                    .filter(location ->  location.contains(
                                                            String.valueOf(Character.forDigit(row, 10)))
                                                    )
                                                    .collect(Collectors.toList());
            int scoutCount;
            if (i == 3) {
                scoutCount = 4;
            } else {
                scoutCount = 2;
            }

            while (scoutCount > 0) {
                int randomScoutIndex = ThreadLocalRandom.current().nextInt(possibleScoutLocations.size());
                String scoutLocation = possibleScoutLocations.remove(randomScoutIndex);
                assignPieceToLocation(pieceLocations, availablePositions, scoutLocation, scoutRank);
                scoutCount--;
            }
        }
    }

    private boolean isNearFlag(String observedLocation, String flagPosition) {
        char observedColumn = observedLocation.charAt(0), flagColumn = flagPosition.charAt(0);
        int obeservedRow = Character.getNumericValue(observedLocation.charAt(1)), flagRow = Character.getNumericValue(flagPosition.charAt(1));
        return (obeservedRow >= flagRow - 1 && obeservedRow <= flagRow + 1) &&
                (observedColumn >= flagColumn - 1 && observedColumn <= flagColumn + 1);
    }

    private void placeSpy(Map<String, Piece> pieceLocations, Set<String> availableSpaces, String flagPosition) {
        int spyRank = 1;
        List<String> possibleSpyLocations = availableSpaces.stream()
                .filter(location -> isNearFlag(location, flagPosition))
                .collect(Collectors.toList());

        int randomSpyLocationIndex = ThreadLocalRandom.current().nextInt(possibleSpyLocations.size());
        String spyLocation = possibleSpyLocations.get(randomSpyLocationIndex);

        assignPieceToLocation(pieceLocations, availableSpaces, spyLocation, spyRank);

    }

    private List<String> initializeCpuStartPositions() {
        List<String> startRowIndices = new ArrayList<>();
        String[] startColumn = new String[NUM_BOARD_COLS];
        for (int i = 0; i < NUM_BOARD_COLS; i++) {
            char letter = (char)('a' + i);
            startColumn[i] = Character.toString(letter);
        }

        IntStream.range(0,4).forEach(index -> {
            startRowIndices.add(Integer.toString(index));
        });

        List<String> startPositions = Arrays.asList(startColumn)
                                            .stream()
                                            .flatMap(alpha -> {
                                                return startRowIndices.stream().map(number -> alpha + number);
                                            })
                                            .collect(Collectors.toList());
        return startPositions;
    }

    private String placeFlag(Map<String, Piece> board, Set<String> availableSpaces) {
        int flag = 0;

        // Generate index from [0-9], represents 'a'-'j'
        int randomColumnIndex = ThreadLocalRandom.current().nextInt(0, NUM_BOARD_COLS);
        char letter = (char) ('a' + randomColumnIndex);
        // Generate index from [0-2]
        int randomRowIndex = ThreadLocalRandom.current().nextInt(0, 3);

        String flagPosition = Character.toString(letter) + Integer.toString(randomRowIndex);

        assignPieceToLocation(board, availableSpaces, flagPosition, flag);
        return flagPosition;
    }

    private void placeBombs(Map<String, Piece> board, Set<String> availableSpaces, String flagPosition) {
        int bomb = 11;
        String leftEdge = "a", rightEdge = "j";
        String centerBoundary = "e";

        // first bomb - one space ahead of flag
        String flagColumnIndex = String.valueOf(flagPosition.charAt(0));
        int flagRowIndex = getNumericValue(flagPosition.charAt(1));
        int firstBombRowIndex = flagRowIndex + 1;
        String firstBombPosition = flagColumnIndex + Integer.toString(firstBombRowIndex);

        assignPieceToLocation(board, availableSpaces, firstBombPosition, bomb);

        // second bomb - one space beside of flag
        String secondBombColumnIndex;
        char charIndex;
        if (flagColumnIndex.equals(leftEdge)) {
            charIndex = (char)(leftEdge.charAt(0) + 1);
            secondBombColumnIndex = String.valueOf(charIndex);
        } else if (flagColumnIndex.equals(rightEdge)) {
            charIndex = (char)(rightEdge.charAt(0) - 1);
            secondBombColumnIndex = String.valueOf(charIndex);
        } else {
            // random chance to be either side
            char leftIndex = (char)(flagColumnIndex.charAt(0) - 1);
            char rightIndex = (char)(flagColumnIndex.charAt(0) + 1);
            secondBombColumnIndex = ThreadLocalRandom.current().nextBoolean() ?
                    String.valueOf(leftIndex) : String.valueOf(rightIndex);
        }

        String secondBombPosition = secondBombColumnIndex + Integer.toString(flagRowIndex);

        assignPieceToLocation(board, availableSpaces, secondBombPosition, bomb);

        // third, fourth bomb - opposite side of flag
        // left side
        int offsetThirdBomb = ThreadLocalRandom.current().nextInt(0,5),
                offsetFourthBomb = ThreadLocalRandom.current().nextInt(0,5);
        String thirdBombColumnIndex, fourthBombColumnIndex;
        if (flagColumnIndex.compareTo(centerBoundary) <= 0) {
            thirdBombColumnIndex = String.valueOf((char)(centerBoundary.charAt(0) + offsetThirdBomb));
            fourthBombColumnIndex = String.valueOf((char)(centerBoundary.charAt(0) + offsetFourthBomb));
        } else { // right side
            thirdBombColumnIndex = String.valueOf((char)(centerBoundary.charAt(0) - offsetThirdBomb));
            fourthBombColumnIndex = String.valueOf((char)(centerBoundary.charAt(0) - offsetFourthBomb));
        }
        List<String> oppositePositionsList = availableSpaces.stream()
                                                .filter(location -> location.contains(thirdBombColumnIndex) || location.contains(fourthBombColumnIndex))
                                                .collect(Collectors.toList());

        int randomOppositePositionsListIndex = ThreadLocalRandom.current().nextInt(oppositePositionsList.size());
        String thirdBombPosition = oppositePositionsList.remove(randomOppositePositionsListIndex);

        assignPieceToLocation(board, availableSpaces, thirdBombPosition, bomb);

        randomOppositePositionsListIndex = ThreadLocalRandom.current().nextInt(oppositePositionsList.size());
        String fourthBombPosition = oppositePositionsList.remove(randomOppositePositionsListIndex);

        assignPieceToLocation(board, availableSpaces, fourthBombPosition, bomb);

        // fifth, sixth bombs
        // random positions in the front row
        List<String> frontRowPositionsList = availableSpaces.stream()
                                                .filter(location -> location.contains(FRONT_ROW_INDEX))
                                                .collect(Collectors.toList());

        int randomFrontRowPositionsListIndex = ThreadLocalRandom.current().nextInt(frontRowPositionsList.size());

        String fifthBombPosition = frontRowPositionsList.remove(randomFrontRowPositionsListIndex);

        assignPieceToLocation(board, availableSpaces, fifthBombPosition, bomb);

        randomFrontRowPositionsListIndex = ThreadLocalRandom.current().nextInt(frontRowPositionsList.size());
        String sixthBombPosition = frontRowPositionsList.remove(randomFrontRowPositionsListIndex);

        assignPieceToLocation(board, availableSpaces, sixthBombPosition, bomb);


    }

    private void assignPieceToLocation(Map<String, Piece> board, Set<String> availableSpaces, String location, int rank) {
        board.get(location).setRank(rank);
        availableSpaces.remove(location);
    }

    private void removeSoldier(Map<String, Piece> board, String oldLocation) {
        board.get(oldLocation).setOwner("-1");
        board.get(oldLocation).setRank(-1);
    }

    private void moveSoldierToNewLocation(Map<String, Piece> board, String newLocation, Piece piece) {
        board.get(newLocation).setOwner(piece.getOwner());
        board.get(newLocation).setRank(piece.getRank());
    }

    public  List<String> findPossibleLocations(String location, Map<String, Piece> position) {
        List<String> finalLocations = new ArrayList<>();
        List<String> lake = Arrays.asList("c4", "c5", "d4", "d5", "g4", "g5", "h4", "h5");
        char firstChar = location.charAt(0);
        char secondChar = location.charAt(1);
        String pieceOwner = position.get(location).getOwner();
        int scoutRank = 2;
        boolean isScout = position.get(location).getRank() == scoutRank;

        if (isScout) {
            boolean encounter = false;
            String locationToCheck;
            int iterator = 1;
            while (!encounter) {
                char newLetter = (char) (firstChar + iterator);
                locationToCheck = Character.toString(newLetter) + secondChar;
                if (!(newLetter >= 'a' && newLetter <= 'j')
                        || lake.contains(locationToCheck)
                        || (position.get(locationToCheck).getRank() != -1))
                    if (iterator < 0)  encounter = true;
                    else iterator = -1;
                else {
                    finalLocations.add(locationToCheck);
                    if (iterator > 0)  iterator++;
                    else  iterator--;
                }
            }
            encounter = false;
            iterator=1;
            while (!encounter) {
                char newLetter = (char) (secondChar + iterator);
                locationToCheck = firstChar + Character.toString(newLetter);
                if (!(newLetter >= '0' && newLetter <= '9')
                        || lake.contains(locationToCheck)
                        || (position.get(locationToCheck).getRank() != -1))
                    if (iterator < 0)  encounter = true;
                    else  iterator = -1;
                else {
                    finalLocations.add(locationToCheck);
                    if (iterator > 0)  iterator++;
                    else  iterator--;
                }
            }
        } else {
            for (int i = -1; i <= 1; i++) {
                if (i == 0) {
                    continue;
                }
                String locationToCheck;
                if (firstChar + i >= 'a' && firstChar + i <= 'j') {
                    locationToCheck = Character.toString((char) (firstChar + i)) + secondChar;
                    // if lake is encountered, don't add positions past the lake
                    if (!lake.contains(locationToCheck)
                            && !position.get(locationToCheck).getOwner().equals(pieceOwner)) {
                        if (position.get(locationToCheck).getRank() == -1)
                            finalLocations.add(locationToCheck);
                    }

                }
                if (secondChar + i >= '0' && secondChar + i <= '9') {
                    locationToCheck = firstChar + Character.toString((char) (secondChar + i));
                    // if lake is encountered, don't add positions past the lake
                    if (!lake.contains(locationToCheck)
                            && !position.get(locationToCheck).getOwner().equals(pieceOwner)) {
                        if (position.get(locationToCheck).getRank() == -1)
                            finalLocations.add(locationToCheck);
                    }
                }
            }
        }

        return finalLocations;
    }

    private int getBattleResult(Piece attacker, Piece defender) {
        if (attacker.getRank() > defender.getRank()
                || (attacker.getRank() == 1 && defender.getRank() == 10)
                || (attacker.getRank() == 3 && defender.getRank() == 11)) return 1;
        else if (attacker.getRank() < defender.getRank()) return -1;
        else return 0;
    }
    // handle battle
}
