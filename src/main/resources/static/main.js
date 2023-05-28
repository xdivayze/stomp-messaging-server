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

$("#yeter").hide();

function validate() {
    let name = $("#username").val();
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
    }).then(() => {
        let block = "<div class='input-group-prepend'>\n" +
            "        <span class='input-group-text' id='arobase'>@</span>\n" +
            "    </div>\n" +
            "    <input id='query-username' type='text' class='form-control' placeholder='Username' aria-label='Username' aria-describedby='arobase'>\n" +
            "    <button type='button' class='btn btn-outline btn-primary' id='query-button'>ADAM ARA </button>"
        $("#query-input-group").append(block);
    });
}

$("#connect").click(validate);

$("#query").on("click", "#query-button", function () {
    let url = "/api/get-users?" + new URLSearchParams({
        username: $("#query-username").val()
    });
    console.log(url);

    fetch(url).then(response => response.json()).then(response => {
        $("#query-table").remove();
        let count = 0;
        let static_block = "<table id='query-table' class='table table-dark table-hover'>\n" +
            "       <thead>\n" +
            "        <tr>\n" +
            "            <th scope='col'>#</th>\n" +
            "            <th scope='col'>KULLANICI ADI</th>\n" +
            "            <th scope='col'>UUID</th>\n" +
            "        </tr>\n" +
            "        </thead>\n" +
            "        <tbody id='query-table-body'>\n" +
            "\n" +
            "        </tbody>\n" +
            "        </thead>\n" +
            "    </table>"
        $("#query").append(static_block);
        $("#query-input-group").hide();
        let yeter = $("#yeter");

        yeter.click(function () {
            yeter.hide();
            $("#query-input-group").show();
            $("#query-table").remove();
        });
        yeter.show();
        response.forEach(user => {
            $("#query-table-body").append("<tr><th scope='row'>" + count + "</th><td>" + user.username + "</td><td>" + `${user.id}` + "</td></tr>");
            count++;
        });
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