package net.andreinc.carsandpolice.query;

import net.andreinc.carsandpolice.query.utils.KsqlDbStreamingQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class PoliceCarMovementStreamingQuery {

    @Autowired
    private KsqlDbStreamingQuery ksqlDbStreamingQuery;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    public void policeLocations() {
        ksqlDbStreamingQuery.query("select * from policeLocations emit changes;", (row)->{
            Map<String, Object> objectMap = row.asObject().getMap();
            simpMessagingTemplate.convertAndSend("/topic/policelocations", objectMap);
        });
    }
}

