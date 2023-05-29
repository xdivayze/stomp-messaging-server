var stompClient = null;
let name = null;
let real_username = null;

//TODO SHOW MESSAGES ON THE SCREEN
//TODO RETRIEVE MESSAGES FROM UUID

$(document).ready(function () {
    connect();
});


$("#chatroom-options").hide();

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
            let message_inp = $("#input-group-message-main");
            $("#basic-addon1").text($(this).text());
            console.log($(this).text());
            message_inp.show();

            stompClient.subscribe('/topic/chatroom/' + $(this).text(), function (message) {
                console.log("Received message: " + message.body);
            });
        });
    });
});



$("#connect").click(function () {
    register($("#username").val(), $("#pass").val()).then(response => {
        if (response.status === 200) {
            real_username = $("#username").val();
            $("#login").hide();
            response.text().then(response => {
                name = response;
                console.log(name);
            });
            $("#chatroom-options").show();
        } else {
            alert("mal");
        }
    });
});

function sendMessage() {
    stompClient.send("/app/chat", {}, JSON.stringify({
        'senderName': name,
        'message': $("#input-group-text-msg").val(),
        'chatroomID': $("#basic-addon1").text()
    }));
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
