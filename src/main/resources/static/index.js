//function setConnected(connected) {
//    $("#connect").prop("disabled", connected);
//    $("#disconnect").prop("disabled", !connected);
//    $("#send").prop("disabled", !connected);
//    if (connected) {
//        $("#conversation").show();
//    } else {
//        $("#conversation").hide();
//    }
//    $('#output').val('');
//    $("#responses").html("");
//}
//
//function connect() {
//
//}
//
//function disconnect() {
//    if (stomp !== null) {
//        stomp.disconnect(function() {
//            setConnected(false);
//            console.log("Client disconnected");
//        });
//        stomp = null;
//    }
//}
//
//function send() {
//    const output = $("#output").val();
//    console.log("Client sends: " + output);
//    stomp.send("/app/request", output, {});
//}
//
//function log(response, clazz) {
//    const input = response.body;
//    console.log("Client received: " + input);
////    $("#responses").append("<tr class='" + clazz + "'><td>" + input + "</td></tr>");
//}
//
//$(function () {
//
//    $("form").on('submit', function (e) {
//        e.preventDefault();
//    });
//
//    $("#connect").click(function () {
//        connect();
//    });
//
//    $("#disconnect").click(function () {
//        disconnect();
//    });
//
//    $("#send").click(function () {
//        send();
//    });
//
//    $("#toggle").click(function(){
//        $.post("/emit/toggle", function(data){
//            console.log("Streaming toggle")
//        });
//    });
//
//});

var framesPerSecond = 1;
var cars = new Map()

function drawCars() {

    var policeCar = new Image()
    policeCar.src = "policecar.png"

    var personalCar = new Image()
    personalCar.src = "personalcar.png"

    var canvas = document.getElementById("grid")
    var ctx = canvas.getContext("2d")

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawGrid()

    cars.forEach(function(value, key, map) {
        if (value.police == true) {
            ctx.drawImage(policeCar, value.x -10, value.y - 10, 20, 20)
        } else {
            ctx.drawImage(personalCar, value.x -10, value.y - 10, 20, 20)
        }
    })

    setTimeout(function() {
        window.requestAnimationFrame(drawCars)
    }, 1000 / framesPerSecond)
}

function drawGrid() {
    var canvas = document.getElementById("grid")
    var ctx = canvas.getContext("2d")
    var gridSize = canvas.height
    var step = canvas.step

    ctx.save()
    ctx.strokeStyle = 'gray' // line colors
    ctx.fillStyle = 'black' // text color
    ctx.lineWidth = 2

    for(let x = 0; x < gridSize; x+=step) {
        ctx.beginPath()
        ctx.moveTo(x, 0)
        ctx.lineTo(x, gridSize)
        ctx.stroke()
    }

    // draw horizontal from Y to Width
    for (let y = 0; y < gridSize; y += step) {
      ctx.beginPath()
      ctx.moveTo(0, y)
      ctx.lineTo(gridSize, y)
      ctx.stroke()
    }

    ctx.restore()
}

document.addEventListener("DOMContentLoaded", function() {
    var stomp = webstomp.over(new SockJS('/websocket-sockjs-stomp'));
    stomp.connect({}, function (frame) {

        stomp.subscribe('/app/subscribe', function (response) {
            var json = JSON.parse(response.body)
            var canvas = document.getElementById("grid")
            canvas.width = json.gridSize;
            canvas.height = json.gridSize;
            canvas.step = json.step;
        });

        const subscription = stomp.subscribe('/queue/responses', function (response) {
        });

        stomp.subscribe('/queue/errors', function (response) {
        });

        stomp.subscribe('/topic/carlocations', function (response) {
            var body = response.body
            var json = JSON.parse(response.body)
            var loc = json["LOCATION"].split(" ")
            json.x = parseInt(loc[0])
            json.y = parseInt(loc[1])
            cars.set(json["PROFILEID"], json)
        });

        stomp.subscribe('/topic/policelocations', function(response) {
            var body = response.body
            var json = JSON.parse(response.body)
            var loc = json["LOCATION"].split(" ")
            json.police = true
            json.x = parseInt(loc[0])
            json.y = parseInt(loc[1])
            cars.set(json["PROFILEID"], json)
        });

        stomp.subscribe('/topic/carsblocked', function(response) {
            $("#events").append('<li>' + response.body + '</li>')
        });

        stomp.subscribe('/topic/policestops', function(response) {
            $("#events").append('<li>' + response.body + '</li>')
        });
    });

    drawCars();
})
