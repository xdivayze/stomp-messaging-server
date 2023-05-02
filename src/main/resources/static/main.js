var stompClient = null;
const name = prompt("ad soyad tc kredi kartı");

function connect() {
    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe("/user/"+name+"/private", function (response) {
            let message = JSON.parse(response.body).message;
            let sender = JSON.parse(response.body).senderName;
            console.log(message);
            $("#messages").append(("<tr class='house'><td>" + sender +" : " + message + "</td></tr>"));
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
    $("#messages").append(("<tr class='from'><td>"+ name + ' : ' + text + "</td></tr>"));
}
connect();
$("#submit").click(function () {
    sendMSG($("#content").val(), $("#to").val());
});