package net.andreinc.carsandpolice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.KsqlObject;
import net.andreinc.carsandpolice.model.PersonalCar;
import net.andreinc.carsandpolice.model.PoliceCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CarsAndPoliceService {

    @Value("${stream.carlocations}")
    private String carLocationsStream;

    @Value("${stream.policelocations}")
    private String policeLocationsStream;

    @Autowired
    private Client client;

    @Autowired
    private ObjectMapper objectMapper;

    public void insertPersonalCar(PersonalCar personalCar) {
        Map<String, Object> map = objectMapper.convertValue(personalCar, Map.class);
        KsqlObject insert = new KsqlObject(map);
        try {
            client.insertInto(carLocationsStream, insert).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void insertPoliceCar(PoliceCar policeCar) {
        Map<String, Object> map = objectMapper.convertValue(policeCar, Map.class);
        KsqlObject insert = new KsqlObject(map);
        try {
            client.insertInto(policeLocationsStream, insert).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
