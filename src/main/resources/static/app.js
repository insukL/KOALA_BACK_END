var stompClient = null;
var currentRoom = null;
var subscription = null;
var headers = {login : 'test_login', passcode:'test_passcode', Authorization: sessionStorage.getItem("access-token")};

function setRoom(roomName) {
    currentRoom = roomName.text();
    $("#currentRoom").text(currentRoom);

    if(subscription !== null) {
        subscription.unsubscribe();
        subscription = stompClient.subscribe('/sub/' + currentRoom, function (chat) {
            showChat(JSON.parse(chat.body).sender + " : " + JSON.parse(chat.body).message);
        });
        $("#chats").html("");
    }

}

function setConnected(connected) {

    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#name").attr("disabled" , true);
    }
    else {
        $("#name").attr("disabled" , false);
    }
}

function connect() {

    if(!validation()){
        console.log("실패");
        return
    }

    var socket = new SockJS('/ws?token='+sessionStorage.getItem('socket-token'));
    stompClient = Stomp.over(socket);
    stompClient.connect(headers, function (frame) {
        setConnected(true);
        subscription = stompClient.subscribe('/sub/' + currentRoom, function (chat) {
            if(JSON.parse(chat.body).type == 'CHAT') {
                showChat(JSON.parse(chat.body).sender + " : " + JSON.parse(chat.body).message);
            }
        });
    });

}

function validation() {

    if($("#name").val() === "") {
        alert("이름을 입력해주세요");
        return false;
    }

    if(currentRoom === null) {
        alert("방을 선택해주세요");
        return false;
    }

    return true;
}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(function(empty){}, headers);
    }
    setConnected(false);
}

function send() {
    var name = $("#name").val();
    var content = $("#content").val();
    stompClient.send("/pub/chat/message" , headers, JSON.stringify({message: content, type:'CHAT'}));
    $("#content").val("");
}

function access(){
    stompClient.send("/pub/chat/member" , headers, JSON.stringify({type:'ACCESS'}));
}

function showChat(message) {
    $("#chats").append("<tr><td colspan='2'>" + message + "</td></tr>");
}

function login(){
    var access = $("#access-token").val();
    sessionStorage.setItem("access-token", "Bearer "+access);
}

function upgrade(){
    fetch("http://localhost:8080/user/socket-token", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": sessionStorage.getItem("access-token"),
        }
    })
        .then(res => res.json())
        .then(json =>{
            sessionStorage.setItem("socket-token", json.body.socket_token);
            console.log(sessionStorage.getItem("socket-token"));
        });
}

$(function () {
    $("form").on('submit', function (e) {e.preventDefault();});
    $("#connect").click(function() { connect(); });
    $("#disconnect").click(function() { disconnect(); });
    $("#send").click(function() { send(); });
    $("a[href=\\#]").click(function() {setRoom($(this))})
    $("#access").click(function (){ access(); })
    $("#login").click(function (){ login(); })
    $("#upgrade").click(function (){ upgrade(); })
});