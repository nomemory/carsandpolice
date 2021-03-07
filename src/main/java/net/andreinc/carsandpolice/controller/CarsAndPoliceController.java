package net.andreinc.carsandpolice.controller;

import io.confluent.ksql.api.client.Client;
import net.andreinc.carsandpolice.job.CarMovementsProducerJob;
import net.andreinc.carsandpolice.model.generators.CarsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

@Controller
public class CarsAndPoliceController {

    @Value("${gridSize}")
    private int gridSize;

    @Value("${step}")
    private int step;

    @Autowired
    private CarMovementsProducerJob job;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private Client client;

    @Autowired
    private MessageSendingOperations messageSendingOperations;

    @Autowired
    private CarsGenerator carsGenerator;

    @PostMapping("/emit/toggle")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> toggle() {
        job.toggle();
        return ResponseEntity.ok(Map.of("produce", job.isOn()));
    }

    /**
     * One time message sent to the clients
     * The message contains the grid size so they can draw the grid in the canvas
     */
    @SubscribeMapping("/subscribe")
    public Map<String, Object> sendOneTimeMessage() {
        return Map.of(
        "gridSize", gridSize,
        "step", step
        );
    }

    @MessageMapping("/request")
    @SendTo("/queue/responses")
    public String handleMessageWithExplicitResponse(String message) {
        System.out.println("Message with response:" + message);
        return "response to " + HtmlUtils.htmlEscape(message);
    }

//    @Scheduled(fixedDelay = 10000)
//    public void sendPeriodicMessages() {
//        String broadcast = String.format("server periodic message %s via the broker", LocalTime.now());
//        this.messageSendingOperations.convertAndSend("/topic/periodic", broadcast);
//    }
}
