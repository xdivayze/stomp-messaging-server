var stompClient = null;
let name = null;
let real_username = null;

//TODO RETRIEVE MESSAGES FROM UUID

$(document).ready(function () {
    connect();
});

$("#chatroom-options").hide();
$("#temp-opt").hide();

$("#test-12").click(function () {
    fetch("api/chatroom?" + new URLSearchParams({
        id: "test-12"
    })
    ).then(response => response.text()).then(response => {
        window.open("/api/chatroom", "_blank");
    })
});

function register(username, pass) {
    return fetch("/api/new-user", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: pass
        }),
    });
}

function new_room(real_username) {
    return fetch("/api/create-chatroom", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            you: real_username,
        }),
    });
}

$("#new-room").click(function () {
    $("#rooms-list").empty();

    new_room(real_username).then(response => response.json()).then(response => {
        console.log(response);
        $("#rooms-list").append("<a class='list-group-item list-group-item-action list-group-item-success'>" + response.new + "</a>");
        response.old_array.forEach(item => {
            let customIL = "<a class='list-group-item list-group-item-action list-group-item-dark'>" + item + "</a>"
            $("#rooms-list").append(customIL)
        });
        $(".list-group-item").click(function () {
            showChatRoom($(this).text());
        });
    });
});

function showChatRoom(id2) {
    let id = id2.toString().trimEnd();
    id = id.trimStart();

    let message_inp = $("#input-group-message-main");
    $("#basic-addon1").text(id);
    console.log(id);
    message_inp.show();
    let chatroom = new ChatRoom(id);
    $("#temp-opt").hide();

    stompClient.subscribe('/topic/chatroom/' + id, function (message) {
        let payload = JSON.parse(message.body);
        let randomColor = Math.floor(Math.random() * 16777215).toString(16);
        let username = payload.senderName;
        if (!chatroom.userColorPairs.has(username)) {
            chatroom.userColorPairs.set(username, randomColor);
        }
        console.log(chatroom);
        console.log(chatroom.userColorPairs.get(username));
        $("#message-table").prepend(`<tr><td style="background-color: #${chatroom.userColorPairs.get(username)}" >${username} : ${payload.message}</td></tr>`);
    });
}

function ChatRoom(id) {
    this.userColorPairs = new Map();
    this.messages = [];
    this.id = id;
}

$("#input-group-text-msg").on("keypress", function (e) {
    if (e.which === 13) {
        sendMessage();
    }
});

$("#send-uuid-chat-btn").click(function () {
    enterChatroom($("#chat-uuid-input").val());
});

function enterChatroom(id) {
    id = id.toString().trim();
    fetch("/api/chatroom?" + new URLSearchParams({
        id: id
    })
    ).then(response => response.text()).then(response => {
        if (response === "true") {
            showChatRoom(id);
        } else {
            alert("mal");
        }
    });
}

$("#connect").click(function () {
    register($("#username").val(), $("#pass").val()).then(response => {
        if (response.status === 200) {
            real_username = $("#username").val();
            $("#login").hide();
            response.text().then(response => {
                name = response;
                console.log(name);
            });
            $("#temp-opt").show();
            $("#chatroom-options").show();
        } else {
            alert("mal");
        }
    });
});

$("#toggle-btn-chat").click(() => {
    $("#temp-opt").toggle();
});

function sendMessage() {
    stompClient.send("/app/chat", {}, JSON.stringify({
        'senderName': name,
        'message': $("#input-group-text-msg").val(),
        'chatroomID': $("#basic-addon1").text()
    }));
    $("#input-group-text-msg").val("");
}

$("#send-message-btn").click(function () {
    sendMessage();
});

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        // Subscribe to the chatroom and handle incoming messages
    });
}
