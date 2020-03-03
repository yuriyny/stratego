
let gameData = null;
let replayTurnIndex = 0;

function getGameHistories(){  //get list gameIDs (for getting gameData) and timestamps (for visual display)
    $.ajax({
        type: 'GET',
        url: "/replay",
        success: function (data) {  //data will be a list containing game ids + timestamps
            console.log(data);
            for(let look of data){
                insertGameHistory(look);
            }
            console.log("ajax get success");
        },
        error: function () {
            console.log("ajax get error");
        }
    });
}

function getGameData(gameID){   //get and set gameData for a chosen timestamp (and it's associated gameID)
    let mapping = "/replay/" + gameID;
    $.ajax({
        type: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        url: mapping,
        dataType: "json",
        // JSON.stringify important here
        data: JSON.stringify(gameID),
        success: function(data){        //i assume a list of turn data
            //console.log(data);
            loadGameHistory(data);
            console.log("ajax post success");
        },
        error: function(xhr, status, error){
            console.log("ajax post error");
            console.log(xhr.responseText);
        }
    });
}

function insertGameHistory(tag){   //put new list element to ui
    let newTemplate = document.createElement("li");
    newTemplate.id = tag["gameID"];
    newTemplate.onclick = function(){getGameData(tag["gameID]"])};
    newTemplate.innerHTML = tag["timestamp"];
    document.getElementById("playHistory").getElementsByTagName("ul")[0].append(newTemplate);
}

function loadGameHistory(data){    //initialize gameData
    gameData = data;
    readBoard(gameData[0]["board"]);
    replayTurnIndex = 0;
}

function prevStep(){
    if(gameData !== null && !this.document.getElementById("prevtoggle").classList.contains("disabled") && replayTurnIndex !== 0) {
        replayTurnIndex -= 1;
        readBoard(gameData[replayTurnIndex]["board"], gameData[replayTurnIndex]["revealed"]);
    }
}

function nextStep(){
    if(gameData !== null && !this.document.getElementById("nexttoggle").classList.contains("disabled") && gameData[replayTurnIndex + 1]) {
        replayTurnIndex += 1;
        readBoard(gameData[replayTurnIndex]["board"], gameData[replayTurnIndex]["revealed"]);
    }
}

function firstStep(){
    if(gameData !== null && !this.document.getElementById("firsttoggle").classList.contains("disabled")) {
        replayTurnIndex = 0;
        readBoard(gameData[replayTurnIndex]["board"], gameData[replayTurnIndex]["revealed"]);
    }
}

function lastStep(){
    if(gameData !== null && !this.document.getElementById("lasttoggle").classList.contains("disabled")) {
        replayTurnIndex = gameData.length - 1;
        readBoard(gameData[replayTurnIndex]["board"], gameData[replayTurnIndex]["revealed"]);
    }
}

function resetBoard(){  //clear board of all pieces
    let board = document.getElementById("board").children;

    for(let cell of board){
        if(cell.firstElementChild) {
            cell.removeChild(cell.firstElementChild);
        }
    }
}

function readBoard(newboard){   //set up board
    resetBoard();
    console.log(newboard);
    let board = document.querySelectorAll(".boardcell");
    for(let cell of board){
        let cellData = newboard[cell.id];
        let owner = cellData["owner"];
        if(owner == -1){
            continue;
        }
        let rank = cellData["rank"];
        rank = (rank == 11) ? "b" : rank;
        rank = (rank == 0) ? "f" : rank;
        let newPiece = document.createElement("div");
        let temp = "piece " + owner + "-" + rank;
        newPiece.className = temp;
        cell.append(newPiece);
    }
}

//PROBABLY DELETE THIS
function printStatistics(){
    $.ajax({
        type: 'GET',
        url: "/_",
        success: function (data) {  //data will contain statistics
            for(let attr in data){
                let newRow = document.getElementById("statistics").insertRow();
                newRow.innerHTML = "<td>" + attr + "</td><td>" + data[attr] + "</td>";
            }
            console.log("ajax get success");
        },
        error: function () {
            console.log("ajax get error");
        }
    });
}

let autoRun = false;
function replayToggle(){
    if(gameData === null){ return; }
    let playbtn = document.getElementById("playtoggle");
    let btns = document.getElementsByClassName("replayControls");
    if(autoRun === true){
        for(let btn of btns){
            btn.classList.remove("disabled");
        }
        playbtn.classList.remove("fa-pause");
        playbtn.classList.add("fa-play");
        autoRun = false;
        return;
    }
    if(autoRun === false){
        for(let btn of btns){
            btn.classList.add("disabled");
        }
        playbtn.classList.remove("disabled", "fa-play");
        playbtn.classList.add("fa-pause");
        autoRun = true;
        (function autoStep(running){
            setTimeout(function(){
                if(running && replayTurnIndex < gameData.length){
                    console.log("getting next step...")
                    nextStep();
                    autoStep(getAutoRun());
                }
            }, 1000);
        })(getAutoRun())
    }
}
function getAutoRun(){
    return autoRun;
}

//DELETE THIS LATER
document.addEventListener("keydown", testingActions);
function testingActions(e) {
    switch (e.keyCode) {  //TESTING PURPOSES ONLY!
        case 90:    //z
            $.ajax({
                type: 'GET',
                url: "/js/sample_replay.json",
                success: function (data) {  //data will be a list containing game ids + timestamps
                    loadGameHistory(data);
                    console.log("sample game data get");
                    console.log("ajax get success");
                },
                error: function () {
                    console.log("ajax get error");
                }
            });
            break;
        case 88:    //x
            let newRow = document.getElementById("statistics").insertRow();
            newRow.innerHTML = "<td>sample</td><td>1920</td>";
            break;
        default:
    }
}
