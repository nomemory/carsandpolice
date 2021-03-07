package net.andreinc.carsandpolice.query;

import net.andreinc.carsandpolice.query.utils.KsqlDbStreamingQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class PoliceStopsStreamingQuery {

    @Autowired
    private KsqlDbStreamingQuery ksqlDbStreamingQuery;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    public void policeStops() throws ExecutionException, InterruptedException {
        ksqlDbStreamingQuery.query("select * from policeStops emit changes;", (row)->{
            Map<String, Object> objectMap = row.asObject().getMap();
            simpMessagingTemplate.convertAndSend("/topic/policestops", objectMap);

        });
    }
}

