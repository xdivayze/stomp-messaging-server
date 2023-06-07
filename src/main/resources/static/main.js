var stompClient = null;
let name = null;
let real_username = null;



//TODO RETRIEVE MESSAGES FROM UUID

$(document).ready(function () {
  connect();
});
let subscription = null;

let message_count = 0;
let seconds = 0;
let mpm = 0;
let now = new Date().getTime();
let started = false;



$("#test-12").click(function () {
  fetch(
    "api/chatroom?" +
      new URLSearchParams({
        id: "test-12",
      })
  )
    .then((response) => response.text())
    .then((response) => {
      window.open("/api/chatroom", "_blank");
    });
});

function register(username, pass) {
  return fetch("/api/new-user", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: username,
      password: pass,
    }),
  });
}

function new_room(real_username) {
  console.log("new room");
  return fetch("/api/create-chatroom", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      you: real_username,
    }),
  });
}

function showChatRoom(id2) {
  $("#table-prim-prep").empty();
  $("#table-prim-prep").append(table_primary);
  $("#input-group-text-msg").on("keypress", function (e) {
    if (e.which === 13) {
      sendMessage();
    }
  });
  
  

  let id = id2.toString().trimEnd();
  id = id.trimStart();

  let message_inp = $("#input-group-message-main");
  $("#basic-addon1").text(id);
  console.log(id);
  message_inp.show();
  let chatroom = new ChatRoom(id);
  $("#temp-opt").hide();
  
  subscription ? subscription.unsubscribe() : null;

  subscription = stompClient.subscribe("/topic/chatroom/" + id, function (message) {
    let payload = JSON.parse(message.body);
    let randomColor = Math.floor(Math.random() * 16777215).toString(16);
    let username = payload.senderName;
    if (!chatroom.userColorPairs.has(username)) {
      chatroom.userColorPairs.set(username, randomColor);
    }
    console.log(chatroom);
    console.log(chatroom.userColorPairs.get(username));
    $("#message-table").prepend(
      `<tr><td style="background-color: #${chatroom.userColorPairs.get(
        username
      )}" >${username} : ${payload.message}</td></tr>`
    );
  });
}

function ChatRoom(id) {
  this.userColorPairs = new Map();
  this.messages = [];
  this.id = id;
}



function enterChatroom(id) {
  id = id.toString().trim();
  fetch(
    "/api/chatroom?" +
      new URLSearchParams({
        id: id,
      })
  )
    .then((response) => response.text())
    .then((response) => {
      if (response === "true") {
        showChatRoom(id);
      } else {
        alert("mal");
      }
    });
}

$("#connect").click(function () {
  register($("#username").val(), $("#pass").val()).then((response) => {
    if (response.status === 200) {
      real_username = $("#username").val();
      $("#login").hide();
      response.text().then((response) => {
        name = response;
        console.log(name);
      });
      $("#chatroom-options").append(chatroom_opt);
      $("#send-uuid-chat-btn").click(function () {
        enterChatroom($("#chat-uuid-input").val());
      });
      
      $("#send-message-btn").click(function () {
        sendMessage();
      });
      $("#toggle-btn-chat").click(() => {
        $("#temp-opt").toggle();
      });
      define_new_room();
    } else {
      alert("mal");
    }
  });
});



function sendMessage() {
  let time_elapsed = new Date().getTime() - now;
  mpm = message_count / Math.round(time_elapsed / 1000);
  if (mpm < 0.8) {
    stompClient.send(
      "/app/chat",
      {},
      JSON.stringify({
        senderName: name,
        message: $("#input-group-text-msg").val(),
        chatroomID: $("#basic-addon1").text(),
      })
    );
    $("#input-group-text-msg").val("");
    message_count++;
  } else {
    alert("onur ananı sikeyim");
    setTimeout(() => {
      stompClient.send(
        "/app/chat",
        {},
        JSON.stringify({
          senderName: name,
          message:
            "ben bir orospu çocuğuyum adım da onur her gün kurtlar götümü sikiyor",
          chatroomID: $("#basic-addon1").text(),
        })
      );
      $("#input-group-text-msg").val("");
      message_count++;
    }, 1500);
  }
  console.log(mpm);
}

function connect() {
  var socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);
    // Subscribe to the chatroom and handle incoming messages
  });
}

function define_new_room() {
  $("#new-room").click(function () {
      $("#rooms-list").empty();

      new_room(real_username)
        .then((response) => response.json())
        .then((response) => {
          console.log(response);
          $("#rooms-list").append(
            "<a class='list-group-item list-group-item-action list-group-item-success'>" +
              response.new +
              "</a>"
          );
          response.old_array.forEach((item) => {
            let customIL =
              "<a class='list-group-item list-group-item-action list-group-item-dark'>" +
              item +
              "</a>";
            $("#rooms-list").append(customIL);
          });
          $(".list-group-item").click(function () {
            showChatRoom($(this).text());
          });
        });
    });
}

let table_primary = `
<div id="table-primary" class="border border-primary">
<div id="input-group-message-main" style="display: none;" class="input-group mb-3">
    <div class="input-group-prepend">
        <span class="input-group-text" id="basic-addon1"></span>
    </div>
    <input id="input-group-text-msg" type="text" class="form-control" placeholder="mesaj" aria-label="Username" aria-describedby="basic-addon1">
    <button id="send-message-btn" class="btn btn-primary">enter de var :)</button>
</div>
<table class="table " id="message-table"></table>
</div>`;

let chatroom_opt = `
  <div id="temp-opt">
      <button class="btn btn-danger" style="width:100%;" id="new-room" >YENİ ODA TALEP ET</button>

      <ul id="rooms-list" class="list-group">

      </ul>
      <div id="room-id-enter" class="input-group mb-3">
          <div class="input-group-prepend">
              <span class="input-group-text" id="room-id-addon">ODA ID</span>
          </div>
          <input id="chat-uuid-input" type="text" class="form-control" placeholder="xxxx-xxxx-xxxx-xxxx" aria-label="Username" aria-describedby="room-id-addon">
          <button id="send-uuid-chat-btn" class="btn btn-primary">enter de var :)</button>
      </div>
  </div>
  <button style="width: 100%;" id="toggle-btn-chat" class="mt-2 btn btn-danger">GÖSTER(ME)</button>`;