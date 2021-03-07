package net.andreinc.carsandpolice.query.utils;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.Row;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class KsqlDbStreamingQuery {

    @Autowired
    protected Client client;

    public void query(String query, Consumer<Row> rowConsumer) {
        client.streamQuery(query)
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
                            rowConsumer.accept(row);
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
