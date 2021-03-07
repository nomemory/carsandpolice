package net.andreinc.carsandpolice.job;

import net.andreinc.carsandpolice.model.generators.VehiclesGenerator;
import net.andreinc.carsandpolice.service.CarsAndPoliceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CarMovementsProducerJob {

    @Autowired
    CarsAndPoliceService service;

    @Autowired
    VehiclesGenerator vehicleMocks;

    private AtomicBoolean onoff = new AtomicBoolean(true);


    public void toggle() {
        boolean temp;
        do {
            temp = onoff.get();
        } while(!onoff.compareAndSet(temp, !temp));
        System.out.println(onoff.get());
    }

    public boolean isOn() {
        return onoff.get();
    }

    @Scheduled(fixedDelay = 1000)
    public void emitPersonalCarEvents() {
        if (isOn()) {
            vehicleMocks.getPersonalCars().forEach(c -> {
                vehicleMocks.moveCarInGrid(c);
                service.inserPersonalCar(c);
            });
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void emitPoliceCarEvents() {
        if (isOn()) {
            vehicleMocks.getPoliceCars().forEach(pc -> {
                vehicleMocks.moveCarInGrid(pc);
                service.insertPoliceCar(pc);
            });
        }
    }
}
