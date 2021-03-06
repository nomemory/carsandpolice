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
            var json = JSON.parse(response.body)
            $("#events").append('<li>' + json.CARPROFILEID + " was blocked by " + json.POLICEPROFILEID + '</li>')
        });

        stomp.subscribe('/topic/policestops', function(response) {
            var json = JSON.parse(response.body)
            $("#events").append('<li>' + json.CARPROFILEID + " was stopped by " + json.POLICEPROFILEID + '</li>')
        });
    });

    drawCars();
})
