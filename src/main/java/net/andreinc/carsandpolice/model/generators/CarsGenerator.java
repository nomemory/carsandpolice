package net.andreinc.carsandpolice.model.generators;

import lombok.Getter;
import net.andreinc.carsandpolice.model.Direction;
import net.andreinc.carsandpolice.model.PersonalCar;
import net.andreinc.carsandpolice.model.PoliceCar;
import net.andreinc.carsandpolice.model.Car;
import net.andreinc.mockneat.abstraction.MockUnitString;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Set.of;
import static net.andreinc.carsandpolice.model.Direction.*;
import static net.andreinc.mockneat.unit.objects.Filler.filler;
import static net.andreinc.mockneat.unit.objects.From.from;
import static net.andreinc.mockneat.unit.seq.IntSeq.intSeq;
import static net.andreinc.mockneat.unit.text.Formatter.fmt;
import static net.andreinc.mockneat.unit.types.Bools.bools;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Names.names;

@Component
public class CarsGenerator {

    @Value("${gridSize}")
    private int gridSize;

    @Value("${step}")
    private int step;

    @Value("${personal.cars}")
    private int personalCarsNumber;

    @Value("${police.cars}")
    private int policeCarsNumber;

    @Value("${movement}")
    private int movement;

    @Getter
    private List<String> colors = List.of("Red", "Green");

    @Getter
    private MockUnitString personalCarsIds;

    @Getter
    private MockUnitString policeCarsIds;

    @Getter
    private List<PersonalCar> personalCars = Collections.emptyList();

    @Getter
    private List<PoliceCar> policeCars = Collections.emptyList();

    @Getter
    private List<Pair<Integer, Integer>> roadsCoordinates = Collections.emptyList();

    public List<PersonalCar> somePersonalCars() {
        return from(personalCars).list(ints().range(0, personalCarsNumber)).get();
    }

    public List<PoliceCar> somePoliceCars() {
        return from(policeCars).list(ints().range(0, policeCarsNumber)).get();
    }

    public void moveCarInGrid(Car car) {
        int x = car.getX();
        int y = car.getY();
        // Is intersection of roads
        if (x%step == 0 && y%step == 0) {
            List<Direction> directions = getPossibleDirectionsInIntersection(x, y);
            directions.remove(inverse(car.getDirection()));
            car.setDirection(from(directions).get());
        }
        car.move(movement);
    }

    public List<Direction> getPossibleDirectionsInIntersection(int x, int y) {
        Set<Direction> possibleDirections = new HashSet<>(of(NORTH, SOUTH, EAST, WEST));
        if (x==0) possibleDirections.remove(EAST);
        if (y==0) possibleDirections.remove(NORTH);
        if (x==gridSize) possibleDirections.remove(WEST);
        if (y==gridSize) possibleDirections.remove(SOUTH);
        return new ArrayList<>(possibleDirections);
    }

    public List<Direction> getPossibleDirections(int x, int y) {
        if (x%step==0 && y%step==0) {
            return getPossibleDirectionsInIntersection(x, y);
        }
        if (x%step==0)
            return List.of(NORTH, SOUTH);
        if (y%step==0)
            return List.of(EAST, WEST);
        return List.of(EAST, WEST, NORTH, SOUTH);
    }

    @PostConstruct
    public void init() {
        initRoadCoordinates();
        initPersonalCars();
        initPoliceCars();
    }

    private void initRoadCoordinates() {
        this.roadsCoordinates = new ArrayList<>();
        for(int i = 0; i < gridSize; i+=step) {
            for(int j = 0; j < gridSize; j+=movement) {
                roadsCoordinates.add(Pair.of(i, j));
                roadsCoordinates.add(Pair.of(j, i));
            }
        }
    }

    private void initPersonalCars() {
        personalCarsIds =
                fmt("#{name} #{seq}")
                        .param("name", names().full())
                        .param("seq", intSeq());

        this.personalCars =
                filler(() -> new PersonalCar())
                        .setter(PersonalCar::setProfileId, personalCarsIds)
                        .setter(PersonalCar::setColor, from(colors))
                        .setter(PersonalCar::setHasPapers, bools().probability(75.0))
                        .map(obj -> {
                            Pair<Integer, Integer> pos = from(roadsCoordinates).get();
                            int x = pos.getLeft();
                            int y = pos.getRight();
                            obj.setX(x);
                            obj.setY(y);
                            Direction direction = from(getPossibleDirections(x, y)).get();
                            obj.setDirection(direction);
                            return obj;
                        })
                        .list(personalCarsNumber)
                        .get();
    }

    private void initPoliceCars() {
        policeCarsIds =
                fmt("Police #{seq}")
                        .param("seq", intSeq());

        policeCars =
                filler(() -> new PoliceCar())
                        .setter(PoliceCar::setProfileId, policeCarsIds)
                        .setter(PoliceCar::setDirection, from(Direction.class))
                        .map(obj -> {
                            Pair<Integer, Integer> pos = from(roadsCoordinates).get();
                            int x = pos.getLeft();
                            int y = pos.getRight();
                            obj.setX(x);
                            obj.setY(y);
                            Direction direction = from(getPossibleDirections(x, y)).get();
                            obj.setDirection(direction);
                            return obj;
                        })
                        .list(policeCarsNumber)
                        .get();
    }
}
