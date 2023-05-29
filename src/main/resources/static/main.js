var stompClient = null;
let name = null;
let real_username = null;

//TODO OPTIMIZE ACCORDING TO SERVER
//TODO ADD MESSAGE HISTORY
//TODO ADD CHATROOM FEATURE
//TODO ADD FRIENDS ONLINE
//TODO SHOW FRIENDS IN A TD TR
//TODO ADD LOGIN AND REGISTER
//TODO TRANSFER LOGIN TO ANOTHER PAGE

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
            window.open("/chatroom/" + $(this).text(), "_blank");
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

function connect() {
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/" + name + "/private", function (response) {
            let message = JSON.parse(response.body).message;
            let sender = JSON.parse(response.body).senderName;
            console.log(message);
            $("#messages").append(("<tr class='house'><td>" + sender + " : " + message + "</td></tr>"));
        });
    });
}

function sendMSG(text, to) {
    $("#content").val("");
    stompClient.send("/app/private-message", {}, JSON.stringify({
        message: text,
        receiverName: to,
        senderName: name
    }));
    $("#messages").append(("<tr class='from'><td>" + name + ' : ' + text + "</td></tr>"));
}

connect();
$("#submit").click(function () {
    sendMSG($("#content").val(), $("#to").val());
});