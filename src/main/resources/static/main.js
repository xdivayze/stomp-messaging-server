var stompClient = null;
const name = null;
var userUUID = null;
//TODO OPTIMIZE ACCORDING TO SERVER
//TODO ADD MESSAGE HISTORY
//TODO ADD CHATROOM FEATURE
//TODO ADD FRIENDS ONLINE
//TODO ADD FRIENDSHIP
//TODO ADD LOGIN AND REGISTER
//TODO TRANSFER LOGIN TO ANOTHER PAGE

function validate() {
    let name = $("#name").val();
    if (name === "") {
        alert("Please enter a name");
        return false;
    }
    let password = $("#pass").val();
    if (password === "") {
        alert("Please enter a password");
        return false;
    }
    let json = {
        username: name,
        password: password,
    }

    fetch("/api/get-user", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(json),
    }).then(userUUID = response => response.text()).then(userUUID => {
        console.log(userUUID);
    }).then(() => {
        $("#login").hide();
    });

}

$("#connect").click(validate);

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