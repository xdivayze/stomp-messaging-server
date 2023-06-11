//declarations

export let table_primary = `<div id="table-primary" class="border border-primary">
<div id="input-group-message-main" style="display: none;" class="input-group mb-3">
    <div class="input-group-prepend">
        <span class="input-group-text" id="basic-addon1"></span>
    </div>
    <input id="input-group-text-msg" type="text" class="form-control" placeholder="mesaj" aria-label="Username" aria-describedby="basic-addon1">
    <button id="send-message-btn" class="btn btn-primary">enter de var :)</button>
</div>
<table class="table " id="message-table"></table>
</div>`;

export let chatroom_opt = `<div id="temp-opt">
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

export function define_new_room() {
  $("#new-room").click(function () {
    $("#rooms-list").empty();

    new_room(real_username)
      .then((response) => response.json())
      .then((response) => {
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

function new_room(real_username) {
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
