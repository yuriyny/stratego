/*window.setInterval(function(){
    console.log(getBoardStatus());
  }, 5000);*/

//$(document).ready    <- this alerts immediately vs after page has loaded
//put <body onload="prompt()"> onto div that this prompt should load for
function prompt(){  //body onload
    if(true) {  //if previous game exists
        document.getElementById("page-cover").classList.add("fade-in");
        document.getElementById("customAlert").classList.add("show");
    }
    else{
        declineAlert();
    }
}
function declineAlert(){
    let temp = document.getElementById("page-cover");
    temp.parentElement.removeChild(temp);
    temp = document.getElementById("customAlert");
    temp.parentElement.removeChild(temp);
}

initializeSetActions(); //set cells up for set-up rather than gameplay

notify("Welcome to Caramel Designs-Stratego!");
notify("Press T to load a premade template");
notify("Select a open cell and press 0-9, b, or f to assign a piece");
notify("Or select a piece in the holder and click a open cell to assign it");
notify("If you want to reset your board, press Q");
notify("When your pieces are all allocated, press Start Game to begin!");
notify("Enjoy!");