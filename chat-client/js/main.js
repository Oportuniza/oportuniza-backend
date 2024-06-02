import * as apiUtil from "./apiUtil.js";

var usernamePage = document.querySelector("#username-page");
var chatPage = document.querySelector("#chat-page");
var usernameForm = document.querySelector("#usernameForm");
var messageForm = document.querySelector("#messageForm");
var messageInput = document.querySelector("#message");
var messageArea = document.querySelector("#messageArea");
var connectingElement = document.querySelector(".connecting");

var stompClient = null;
var username = null;
var password = null;

var colors = [
    "#2196F3",
    "#32c787",
    "#00BCD4",
    "#ff5652",
    "#ffc107",
    "#ff85af",
    "#FF9800",
    "#39bbb0",
    "#fcba03",
    "#fc0303",
    "#de5454",
    "#b9de54",
    "#54ded7",
    "#54ded7",
    "#1358d6",
    "#d611c6",
];

function connect(event) {
    username = document.querySelector("#name").value.trim();
    password = document.querySelector("#password").value;
    if (username) {
        if (password === "hello") {
            usernamePage.classList.add("hidden");
            chatPage.classList.remove("hidden");

            var socket = new SockJS("http://localhost:8888/websocket");
            stompClient = Stomp.over(socket);

            stompClient.connect({}, onConnected, onError);
        } else {
            let mes = document.getElementById("mes");
            mes.innerText = "Wrong password";
        }
    }
    event.preventDefault();
}

function onConnected() {
    username = document.querySelector("#name").value.trim();
    console.log("/user/" + username + "/queue/messages");

    stompClient.subscribe(
        "/user/" + username + "/queue/messages",
        onMessageReceived
    );

    connectingElement.classList.add("hidden");
}

function onError(error) {
    connectingElement.textContent =
        "Could not connect to WebSocket! Please refresh the page and try again or contact your administrator.";
    connectingElement.style.color = "red";
}

function send(event) {
    var messageContent = messageInput.value.trim();
    var receiver = messageContent.split(" ")[0];

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            receiver: receiver,
            content: messageContent.substring(receiver.length + 1),
            timestamp: new Date(),
        };

        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        messageInput.value = "";
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    console.log(payload);
    var notification = JSON.parse(payload.body);

    var messageElement = document.createElement("li");

    apiUtil.findChatMessage(notification.id).then((message) => {
        messageElement.classList.add("chat-message");

        // Create avatar element
        var avatarElement = document.createElement("i");
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style["background-color"] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);

        // Create username element
        var usernameElement = document.createElement("span");
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);

        // Update username element style
        usernameElement.style["color"] = getAvatarColor(message.sender);

        // Create message text element
        var messageTextElement = document.createElement("p");
        var messageText = document.createTextNode(message.content); // Assuming message.content contains the actual message content
        messageTextElement.appendChild(messageText);
        messageElement.appendChild(messageTextElement);

        // Append the message element to the message area
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;

        // Float the message to the right if it's from the current user
        if (message.sender === username) {
            messageElement.classList.add("own-message");
        }
    });
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener("submit", connect, true);
messageForm.addEventListener("submit", send, true);
