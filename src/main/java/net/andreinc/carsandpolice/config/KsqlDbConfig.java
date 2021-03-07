package net.andreinc.carsandpolice.config;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * This class is responsible with the configuration of the ksqldb client.
 */
@Component
public class KsqlDbConfig {

    @Value("${ksqldb.host}")
    public String ksqlDbHost;

    @Value("${ksqldb.port}")
    public Integer ksqlDbPort;

    @Autowired
    Client client;

    @Bean
    public Client ksqlDbClient() {
        ClientOptions options = ClientOptions.create()
                .setHost(ksqlDbHost)
                .setPort(ksqlDbPort);
        Client client = Client.create(options);
        return client;
    }
}
