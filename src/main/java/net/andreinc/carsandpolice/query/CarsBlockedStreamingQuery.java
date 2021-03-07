package net.andreinc.carsandpolice.query;

import net.andreinc.carsandpolice.query.utils.KsqlDbStreamingQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class CarsBlockedStreamingQuery {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private KsqlDbStreamingQuery ksqlDbStreamingQuery;

    @PostConstruct
    public void carsBlocked() {
        ksqlDbStreamingQuery.query("select * from carsBlocked emit changes;", (row) -> {
            Map<String, Object> objectMap = row.asObject().getMap();
            simpMessagingTemplate.convertAndSend("/topic/carsblocked", objectMap);
        });
    }
}
