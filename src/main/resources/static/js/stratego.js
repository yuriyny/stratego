
//let cell statuses be defined as:
//  open - empty space that piece can be placed
//  closed - empty space that piece cannot be placed
//  occupied - occupied space

//let additional class statuses be:
//  selected - current selected cell-- only available for open and occupied
//  revealed - pieces that are known to both ai and player

//----------------------------------------set up cell interactions------------------------//
let cells = document.querySelectorAll(".cell");
//adding eventListeners to document and cells
function initializeSetActions() {
    for (const cell of cells) {
        try {
            cell.removeEventListener("click", inGameAction);
        }
        catch(err){}
        cell.addEventListener("click", selectCell);
        cell.addEventListener("mouseover", colorCell);
        cell.addEventListener("mouseout", cleanCell);
    }

    document.addEventListener("keydown", fillPiece);
}
function initializeGameActions(){
    removeActions();

    let boardcells = document.querySelectorAll(".boardcell");
    for(let cell of boardcells){
        cell.addEventListener("click", inGameAction);
    }
}
function removeActions(){
    for(const cell of cells){
        cell.removeEventListener("click", selectCell);
        cell.removeEventListener("mouseover", colorCell);
        cell.removeEventListener("mouseout", cleanCell);
    }
    document.removeEventListener("keydown", fillPiece);
    if(selectedOrigin !== null){
        selectedOrigin = null;
    }
}

//--------------------------set up: cell function events--------------------------//
let selectedOrigin = null;
function selectCell(){
    markCell(this);
    //if this cell is the selected origin, then unselect origin
    if(this === selectedOrigin){
        console.log("case1: deselect cell");
        this.classList.remove("selected");
        selectedOrigin = null;
    }
    //if no origin (initial selection) has yet been made
    else if(selectedOrigin === null){
        console.log("case2: select cell");
        if(this.classList.contains("open") || this.classList.contains("occupied")){
            selectedOrigin = this;
            this.classList.add("selected");
        }
    }
    //if origin has a piece, then set it as selected
    else if(this.classList.contains("occupied")){
        console.log("case3: select occupied cell");
        selectedOrigin.classList.remove("selected");
        selectedOrigin = this;
        this.classList.add("selected");
    }
    //if current origin is not empty and another cell is selected-- move piece
    else if(selectedOrigin !== null && selectedOrigin.hasChildNodes()){
        console.log("case4: move selected cell");
        if(this.classList.contains("open") || this.classList.contains("occupied")){
            this.append(selectedOrigin.firstElementChild);
            updateStatus(this, "occupied");
            updateStatus(selectedOrigin, "open");
            selectedOrigin = null;

            setStartButton();
        }
    }
    //if only a origin but no piece has been selected, set new origin
    else{
        console.log("case5: no pieces selected-- origin moved");
        if(this.classList.contains("open") || this.classList.contains("occupied")){
            selectedOrigin.classList.remove("selected");
            selectedOrigin = this;
            this.classList.add("selected");
        }
    }
}

function colorCell(){
    if(selectedOrigin !== null && selectedOrigin.hasChildNodes() && this.classList.contains("boardcell")){
        markCell(this);
    }
}

function cleanCell(){   //when moving off a cell, make sure it becomes colorless
    this.classList.remove("green", "red", "blue");
}

//-------------------------------set up: key press events------------------------//
function fillPiece(e){
    if(selectedOrigin === null){
        return;
    }
    if(selectedOrigin.hasChildNodes()){
        selectedOrigin.classList.remove("selected");
        return;
    }

    let target = null;
    switch(e.keyCode){
        case 48:    //0
            target = "10";
            break;
        case 49:    //1
            target = "1";
            break;
        case 50:    //2
            target = "2";
            break;
        case 51:    //3
            target = "3";
            break;
        case 52:    //4
            target = "4";
            break;
        case 53:    //5
            target = "5";
            break;
        case 54:    //6
            target = "6";
            break;
        case 55:    //7
            target = "7";
            break;
        case 56:    //8
            target = "8";
            break;
        case 57:    //9
            target = "9";
            break;
        case 66:    //b
            target = "b";
            break;
        case 70:    //f
            target = "f";
            break;
        default:
            return;
    }

    let found = getFromHolder(target);

    if(found !== null){
        selectedOrigin.append(found.firstElementChild);
        updateStatus(found, "open");
        updateStatus(selectedOrigin, "occupied");

        setStartButton();
    }
    selectedOrigin.classList.remove("selected");
    selectedOrigin = null;
}

function getFromHolder(pieceRank){
    //console.log("trying to get from holder ", pieceRank);
    let holder = document.getElementById("pieceholder").children;
    for(let cell of holder){
        if(cell.hasChildNodes() && cell.firstElementChild.classList[1].split("-")[1] === pieceRank){
            return cell;
        }
    }
    return null;
}   //only looks for [0,1,2,3,4,5,6,7,8,9,b,f] string

function putBacktoHolder(piece){
    let holder = document.getElementById("pieceholder").children;
    piece.classList.remove("revealed");
    for(let cell of holder){
        if(!cell.hasChildNodes()){
            cell.append(piece);
            updateStatus(cell, "occupied");
            return;
        }
    }
}

//-----------------------------------set up: utility--------------------------------//
function markCell(cell){
    if(!cell.classList.contains("boardcell")){}
    if(cell.classList.contains("closed")){
        cell.classList.add("red");
    }
    else{
        cell.classList.add("green");
    }
}

function updateStatus(cell, post){
    cell.classList.remove("open", "occupied", "closed", "selected");
    cell.classList.add(post);
}

function resetBoard(){    //put piece on board back into holder
    let board = document.querySelectorAll(".boardcell");
    let holder = document.querySelectorAll(".holdercell");

    let holderPos = 0;
    for(let cell of board){
        cell.classList.remove("green", "red", "blue");
        if(cell.firstElementChild) {
            if (cell.firstElementChild.classList[1].split("-")[0] === "ai") {
                cell.removeChild(cell.firstElementChild);
            }
            else {
                let piece = cell.firstElementChild;
                piece.classList.remove("revealed");
                while (!holder[holderPos].classList.contains("open")) {
                    holderPos += 1;
                }
                holder[holderPos].append(piece);
                updateStatus(cell, "open");
                updateStatus(holder[holderPos], "occupied");
            }
        }
    }
}

function resetBoardTags(){
    let board = document.querySelectorAll(".boardcell");
    let holder = document.querySelectorAll(".holdercell");
    for(let index = 0; index < board.length; index++){
        if(index < 60){
            updateStatus(board[index], "closed");
        }
        else {
            updateStatus(board[index], "open")
        }
    }
    for(let index = 0; index < holder.length; index++){
        updateStatus(holder[index], "occupied");
    }
}

//----------------------------------getting information about current board----------------------//

let revealAll = false;
function readBoardStatus(boardStatus, aiRevealedPieces, userRevealedPieces){
    //console.log(boardStatus);
    resetBoard();

    let board = document.querySelectorAll(".boardcell");
    //assume the board (positions) is always correct...
    for (let cell of board) {

        let cellData = boardStatus[cell.id];
        if(cellData["owner"] === "player") {
            let tempCell = null;
            if(cellData["rank"] == 11){
                tempCell = getFromHolder("b");
            }
            else if(cellData["rank"] == 0){
                tempCell = getFromHolder("f");
            }
            else{
                tempCell = getFromHolder(String(cellData["rank"]));
            }
            let piece = tempCell.firstElementChild;
            cell.append(piece);
            updateStatus(tempCell, "open");
            updateStatus(cell, "occupied");
        }
        else if(cellData["owner"] === "ai"){
            let newEnemy = document.createElement("div");
            let temp = null;
            if(cellData["rank"] == 11){
                temp = "piece ai-b ai-hidden";
            }
            else if(cellData["rank"] == 0){
                temp = "piece ai-f ai-hidden";
            }
            else{
                temp = "piece ai-" + cellData["rank"] + " ai-hidden";
            }
            newEnemy.className = temp;
            cell.append(newEnemy);
        }
        else{
            continue;
        }

        if(cell.id in userRevealedPieces || cell.id in aiRevealedPieces){
            cell.firstElementChild.classList.remove("ai-hidden");
            cell.firstElementChild.classList.add("revealed");
        }
        if(revealAll && cell.firstElementChild){
            cell.firstElementChild.classList.remove("ai-hidden");
            cell.firstElementChild.classList.add("revealed");
        }
    }
    //notify("Template board has been loaded");
    setStartButton();

}

function getBoardStatus(){
    let boardCells = document.querySelectorAll(".boardcell");
    let boardStatus = {
            positions : {}
        };
    let pieceLocationsObject = boardStatus.positions;
    for(let cell of boardCells){
        pieceLocationsObject[cell.id] = {};
        if(cell.firstElementChild) {
            let owner = cell.firstElementChild.classList[1].split("-")[0];
            let rank = cell.firstElementChild.classList[1].split("-")[1];
            setPosData(pieceLocationsObject[cell.id], owner, rank);
        }
        else{
            setPosData(pieceLocationsObject[cell.id], -1, -1);
        }
    }
    //console.log(boardStatus);
    return boardStatus;
}   //this returns the boardstate --> { positions: { a1: {}, ...} }

function getRevealedStatus(){
    let boardCells = document.querySelectorAll(".boardcell");
    let userRevealedPieces = {};
    let aiRevealedPieces = {};
    for(let cell of boardCells){
        if(cell.firstElementChild && cell.firstElementChild.classList.contains("revealed")){
            let owner = cell.firstElementChild.classList[1].split("-")[0];
            let rank = cell.firstElementChild.classList[1].split("-")[1];
            if(owner === "player"){
                userRevealedPieces[cell.id] = {};
                setPosData(userRevealedPieces[cell.id], owner, rank);
            }
            else if(owner === "ai"){
                aiRevealedPieces[cell.id] = {};
                setPosData(aiRevealedPieces[cell.id], owner, rank);
            }
        }
    }
    return {"userRevealedPieces":userRevealedPieces,
        "aiRevealedPieces":aiRevealedPieces};
}
function setPosData(item, owner, rank){
    item["owner"] = owner;
    if(rank === "b"){
        item["rank"] = 11;
    }
    else if(rank === "f"){
        item["rank"] = 0;
    }
    else{
        item["rank"] = Number(rank);
    }
}

//---------------------------------------write to messages----------------------------------//
const notifications = document.getElementById("notifications");
function notify(newMessage){
    if(notifications.children.length === 9){
        notifications.removeChild(notifications.firstElementChild);
    }
    let newBox = document.createElement("div");
    newBox.className = "messagebox";
    newBox.innerHTML = newMessage;
    notifications.appendChild(newBox);
}

//---------------------------------------in game actions--------------------------------//
let gameInSession = false;
let isAutoPlay = false;
let turnNumber = 0;
function gameToggle(){
    let holder = document.querySelectorAll(".holdercell.occupied");
    if(gameInSession === true){
        finishGame("");
    }
    else if(holder.length !== 0){
        //not all pieces assigned yet
        console.log("Game cannot start yet");
    }
    else{
        initializeGameActions();
        cleanBoardTags();
        setEnemyPieces();
        document.getElementById("autoPlayBtn").classList.remove("disabled");
        notify("The game has started!");
        turnNumber = 0;
        gameInSession = true;

        document.getElementById("startBtn").innerHTML = "End Game";
    }
}

let activeCell = null;
function inGameAction(){
    //if user clicks again on activeCell, deselect
    if(this === activeCell){
        activeCell.classList.remove("green");
        activeCell = null;
        cleanBoardTags();
    }
    //if no activeCell and user clicks on a cell holding a piece
    else if(activeCell === null && this.classList.contains("occupied")){
        activeCell = this;
        this.classList.add("green");
        markValidMoves();
    }
    //if there is a activeCell and another cell was clicked
    else if(activeCell) {
        let action = {};
        action["from"] = activeCell.id;
        action["to"] = this.id;
        let postData = {"board": getBoardStatus(), "action": action};

        //console.log(postData);

        if(checkMove(postData)){    //this method checks if move is valid
            if(!findIfEmptyBlock(postData, this.id)){   //this method checks if there is an enemy or not
                let playerRank = activeCell.firstElementChild.classList[1].split("-")[1];
                let enemyRank = this.firstElementChild.classList[1].split("-")[1];
                let notifyHeading = "Player piece " + playerRank + " has moved from " +
                    action["from"] + " to " + action["to"];
                switch(attack(postData)){
                    case 1:
                        //player wins
                        notify(notifyHeading + " and defeated enemy piece " + enemyRank);
                        activeCell.firstElementChild.classList.add("revealed");
                        this.removeChild(this.firstElementChild);
                        this.append(activeCell.firstElementChild);
                        this.classList.add("occupied");
                        break;
                    case 0:
                        //draw
                        notify(notifyHeading + " and died with enemy piece " + enemyRank);
                        this.removeChild(this.firstElementChild);
                        putBacktoHolder(activeCell.firstElementChild);
                        break;
                    case -1:
                        //player loses
                        notify(notifyHeading + " and died to enemy piece " + enemyRank);
                        putBacktoHolder(activeCell.firstElementChild);
                        this.firstElementChild.classList.remove("ai-hidden");
                        this.firstElementChild.classList.add("revealed");
                        break;
                    default:
                        console.log("unexpected case has occured:", attack(postData));
                        break;
                }
            }
            else{
                notify("Player has moved piece from " + action["from"] + " to " + action["to"]);
                this.append(activeCell.firstElementChild);
                this.classList.add("occupied");
            }
            //after a valid action, any activecell will always be reset
            if(activeCell) {
                activeCell.classList.remove("green", "occupied");
                activeCell = null;
            }
            cleanBoardTags();
            turnNumber++;

            setEnemyPieces();
        }
        else{
            notify("Selected action is not valid.");
        }
    }
}

function markValidMoves(){  //colors cells that a selected activeCell can move to
    let board = document.querySelectorAll(".boardcell");
    let action = {};
    let postData = {"board": getBoardStatus(), "action": action};
    for(let cell of board){
        action["from"] = activeCell.id;
        action["to"] = cell.id;
        postData["action"] = action;
        if(cell !== activeCell && checkMove(postData)){
            cell.classList.add("green");
        }
    }
}

function setStartButton(){
    let inHolder = document.querySelectorAll(".holdercell.occupied").length;
    if(inHolder === 0){
        console.log("start button status: enabled");
        document.getElementById("startBtn").classList.remove("disabled");
    }
    else{
        console.log("start button status: disabled");
        document.getElementById("startBtn").classList.add("disabled");
        document.getElementById("autoPlayBtn").classList.add("disabled");
    }
}

function cleanBoardTags(){  //i assume no need for these once game starts
    let board = document.querySelectorAll(".boardcell");
    for(let cell of board){
        cell.classList.remove("open", "closed", "green");
    }
}

let gameID = -1;
function getPostData(){ //format of data as backend needs
    let boardStatus = getBoardStatus();
    let revealedPieces = getRevealedStatus();
    let userRevealedPieces = revealedPieces["userRevealedPieces"];
    let aiRevealedPieces = revealedPieces["aiRevealedPieces"];
    let data = {"boardState" : boardStatus, //a hash map with cell id as key and owner and rank as values for all pieces on board
        "userRevealedPieces" : userRevealedPieces,  //a hash map with cell id as key and owner and rank as values of all revealed user pieces
        "aiRevealedPieces" : aiRevealedPieces,  //a hash map with cell id as key and owner and rank as values of all revealed ai pieces
        "isAutoPlay" : isAutoPlay,  //a flag of whether autoPlay is active
        "turn" : turnNumber,    //the turn number in current game
        "gameID" : gameID};
    return data;
}

function setGameID(data){
    gameID = data;
}

function setEnemyPieces(){
    let data = getPostData();
    console.log(data);
    $.ajax({
        type: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        url: "/game/move",
        dataType: "json",
        data: JSON.stringify(data),
        success: function(data){
            console.log("ajax post success");
            console.log(data);
            if(gameID === -1) {
                setGameID(data["gameID"]);
            }
            readBoardStatus(data["boardState"].positions, data["userRevealedPieces"], data["aiRevealedPieces"]);
            turnNumber = data["turn"];
            if(data["isGameOver"]){
                //change/add to finishGame to another ajax request to end game
                finishGame("");
            }
        },
        error: function(xhr, status, error){
            console.log("ajax post error");
            console.log(xhr.responseText);
        }
    });
}

function autoPlay(){
    if(isAutoPlay === true){
        document.getElementById("autoPlayBtn").innerHTML = "Auto Play";
        notify("Please wait a moment before auto play ends");
        isAutoPlay = false;
        return;
    }
    if(gameInSession === true){
        isAutoPlay = true;
        removeActions();
        document.getElementById("autoPlayBtn").innerHTML = "Auto Play in Progress";
        console.log("starting auto play now");
        notify("Starting auto play now");
        (function aiAction(isAutoPlay){
            setTimeout(function(){
                console.log("getting ai action... (isAutoPlay set to ", isAutoPlay);
                if(gameInSession && (isAutoPlay || turnNumber % 2 === 0)){
                    setEnemyPieces();
                    aiAction(getIsAutoPlay());
                }
                else{
                    console.log("... finished getting ai actions");
                    notify("Auto play has ended now");
                    initializeGameActions();
                }
            }, 1000);
        })(getIsAutoPlay())
    }
}
function getIsAutoPlay(){
    return isAutoPlay;
}

function finishGame(winner){
    if(winner === "player"){
        notify("You have won!");
    }
    else if(winner === "ai"){
        notify("You have lost.");
    }
    else{
        notify("Ending Game");
    }
    resetBoard();
    resetBoardTags();
    initializeSetActions();
    activeCell = null;
    setStartButton();
    document.getElementById("startBtn").innerHTML = "Start Game";
    document.getElementById("autoPlayBtn").innerHTML = "Auto Play";
    gameInSession = false;
    isAutoPlay = false;
    turnNumber = 0;
}

//------------------------------------key input actions----------------------------------------//
let devTrigger = 0;
document.addEventListener("keydown", testingActions);
function testingActions(e){
    switch(e.keyCode){
        case 81:   //q
            if(gameInSession === false) {
                resetBoard();
                console.log("The board has been reset");
            }
            break;
        case 84:    //t
            if(gameInSession === false) {
                $.ajax({
                    type: 'GET',
                    url: "/js/template_board_hash.json",
                    success: function (data) {
                        readBoardStatus(data["positions"], {}, {});
                    },
                    error: function () {
                        console.log("ajax get error");
                    }
                });
            }
            break;
        case 65:    //a
            if(devTrigger > 9) {
                let enemyCells = document.querySelectorAll(".ai-hidden");
                for (let cell of enemyCells) {
                    cell.classList.remove("ai-hidden");
                    cell.classList.add("revealed");
                }
                notify("All enemies revealed. You cheater!");
                revealAll = true;
            }
            break;
        case 82:    //r
            console.log(getRevealedStatus());
            break;
        default:
    }
}
document.getElementById("logo").addEventListener("mousedown", function(){ devTrigger++; });