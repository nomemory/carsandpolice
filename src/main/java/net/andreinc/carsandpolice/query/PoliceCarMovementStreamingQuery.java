package net.andreinc.carsandpolice.query;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class PoliceCarMovementStreamingQuery {

    @Autowired
    Client client;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    public void policeLocations() throws ExecutionException, InterruptedException {
        client.streamQuery("select * from policeLocations emit changes;")
                .thenAccept(streamedQueryResult -> {
                    streamedQueryResult.subscribe(new Subscriber<Row>() {
                        private Subscription subscription;

                        @Override
                        public void onSubscribe(Subscription s) {
                            this.subscription = s;
                            subscription.request(1);
                        }

                        @Override
                        public void onNext(Row row) {
                            Map<String, Object> objectMap = row.asObject().getMap();
                            simpMessagingTemplate.convertAndSend("/topic/policelocations", objectMap);
                            subscription.request(1);
                        }

                        @Override
                        public void onError(Throwable t) {
                            System.out.println(t);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
                });
    }
}
