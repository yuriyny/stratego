
/**
 *Two uses:
 * 1.See if the scout path contains only empty spaces.
 * 2.See if an attack is occurring. return true is it is not an attack.
 * @param  boardState
 * @param  location          this locations the block in the scout path
 * @return  boolean          if the location is empty, then return true
 */
function findIfEmptyBlock(boardState, location){
    return boardState.board.positions[location]["rank"]===-1
}

/**
 * This function will put the location/rank/owner from both locations into a dictionary
 * @param  boardState
 * @return  dict
 */
function findRankAndLocations(boardState){
    var dict={};                //return dictionary
    dict["firstLocation"]=boardState.action["from"];
    dict["secondLocation"] =boardState.action["to"];
    dict["firstPieceRank"] = boardState.board.positions[dict["firstLocation"]]["rank"];
    dict["firstPieceOwner"] = boardState.board.positions[dict["firstLocation"]]["owner"];
    dict["secondPieceRank"] = boardState.board.positions[dict["secondLocation"]]["rank"];
    dict["secondPieceOwner"] = boardState.board.positions[dict["secondLocation"]]["owner"];
    return dict;
}

/**
 *
 * @param  boardState
 * @return  boolean          if action is valid or not
 */

function checkMove(boardState){
    var locationCharIndex;                              //0 for first char in location is the same. 1 for second char in location is the same
    var dict=findRankAndLocations(boardState);          //dict of firstLocation, secondLocation, firstPieceRank, secondPieceRank, firstPieceOwner, secondPieceOwner

    //storing info from dict
    var firstPieceRank=dict["firstPieceRank"];          //rank of moving piece
    var firstLocation=dict["firstLocation"];            //start location
    var secondLocation=dict["secondLocation"];          //destination

    //lake coordinates
    var lake=["c4","c5","d4","d5","g4","g5","h4","h5"];

    if(firstPieceRank===0 ||firstPieceRank===11 || lake.indexOf(secondLocation)!==-1 )          //if selected a flag, or a bomb|| moving into to lake
        return false;
    else if(firstLocation.charAt(locationCharIndex=0)===secondLocation.charAt(locationCharIndex) ||
        firstLocation.charAt(locationCharIndex=1)===secondLocation.charAt(locationCharIndex)){
        //vertical/horizontal test is passed. This checks if first location characters are equal or the second location characters are equal

        if(firstPieceRank===2){//handle scout power
            var min=Math.min(firstLocation.charCodeAt(1-locationCharIndex),secondLocation.charCodeAt(1-locationCharIndex)); //Converts the changing char to charcode so can be iterated
            var max=Math.max(firstLocation.charCodeAt(1-locationCharIndex),secondLocation.charCodeAt(1-locationCharIndex));
            //Since min and max positions may or may not contain a piece, we do not need to check if empty. We only need to check from min+1 to max-1 to see if those blocks are empty
            for (var scoutPosition=min+1;scoutPosition<max;scoutPosition++) {
                var coordinatesToCheck;//This assembles the coordinates to coordinate like "a1" but assembly order depends on which location character is constant
                if (locationCharIndex===0)
                    coordinatesToCheck= (firstLocation.charAt(locationCharIndex)).concat(String.fromCharCode(scoutPosition));
                else
                    coordinatesToCheck=(String.fromCharCode(scoutPosition)).concat(firstLocation.charAt(locationCharIndex));
                if(!findIfEmptyBlock(boardState,coordinatesToCheck) || lake.indexOf(coordinatesToCheck)!==-1)// if location has a piece on it or location is in lake
                    return false;
            }
        }else if((Math.abs(firstLocation.charCodeAt(1-locationCharIndex)-secondLocation.charCodeAt(1-locationCharIndex))!==1 )) {
            return false; // if not scout and move is greater than 1.
        }
        //compare owners to make sure not same owner
        return dict["firstPieceOwner"] !== dict["secondPieceOwner"];
    }else//vertical/horizontal test failed
        return false;
}


/**
 *
 * @param  boardState
 * @return  Number          win:1       tied:0      lose:-1
 */
function attack(boardState){
    var dict=findRankAndLocations(boardState);                      //Returns dictionary of info to identify piece
    if(dict["firstPieceRank"]>dict["secondPieceRank"] || (dict["firstPieceRank"]===1 && dict["secondPieceRank"]===10)||
        (dict["firstPieceRank"]===3 && dict["secondPieceRank"]===11)) // if attack lower rank OR spy attack marshall OR miner dismantle bomb
        return 1;
    else if(dict["firstPieceRank"]===dict["secondPieceRank"])
        return 0;
    else
        return -1;
}


