
//let cell statuses be defined as:
//  open - empty space that piece can be placed
//  empty - empty space that piece cannot be placed
//  obstacle - empty space that no piece can be placed
//  fill - occupied space
const cells = document.querySelectorAll(".open, .empty, .obstacle, .fill");
const pieces = document.querySelectorAll(".piece");
let holding = null;
let item = null;
let origin = null;

//adding eventListeners to pieces and cells
for(const piece of pieces){
    piece.addEventListener('dragstart', dragStart);
    piece.addEventListener('dragend', dragEnd);
}

for(const cell of cells){
    cell.addEventListener("dragstart", dragOrigin);
    cell.addEventListener("dragleave", dragLeave);
    cell.addEventListener("dragenter", dragEnter);
    cell.addEventListener("dragover", dragOver);
    cell.addEventListener("drop", dragDrop);
}

//---------------------------piece function events-------------------------//
function dragStart(){
    console.log("dragStart");
    holding = this.className;
    item = this;
    setTimeout(() => bugStep(), 10);
}
//this is to work around a chrome bug
function bugStep(){
    this.className += ' hold';
    setTimeout(() => (this.className = 'invisible'), 0);
}
function dragEnd(){
    console.log("drag End: ", holding);
    this.className = holding;
    item = null;
}

//--------------------------cell function events--------------------------//
function dragOrigin(){
    console.log("dragOrigin");
    origin = this;
}
function dragEnter(e){
    console.log("dragEnter");
    e.preventDefault();
    if(this.classList.contains("boardcell")){
        this.classList.add(markCell(this));
    }
}
function dragOver(e){
    console.log("dragOver");
    e.preventDefault();
}

function dragLeave(){
    console.log("dragLeave");
    this.classList.remove("green", "red");
}
function dragDrop(){
    console.log("dragDrop");
    if(markCell(this) === "green"){
        this.append(item);
        console.log("dropping ", item);
        this.classList.remove("green");
        updateStatus(this, "fill");
        updateStatus(origin, "open");
    }
    else{
        this.classList.remove("green", "red");
    }
}

//-----------------------------------misc--------------------------------//
function markCell(cell){
    if(cell.classList.contains("open")){
        return "green";
    }
    else{
        return "red";
    }
}
function updateStatus(cell, post){
    cell.classList.remove("open", "empty", "obstacle", "fill");
    cell.classList.add(post);
}