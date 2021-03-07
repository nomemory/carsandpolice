package net.andreinc.carsandpolice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class Car {

    private String profileId;

    @JsonIgnore
    private int x;

    @JsonIgnore
    private int y;

    private Direction direction;

    public void move() {
        this.x+=direction.getX();
        this.y+=direction.getY();
    }

    public void changeDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonProperty("location")
    public String locationAsString() {
        return x + " " + y;
    }
}
