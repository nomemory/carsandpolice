package net.andreinc.carsandpolice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PersonalCar extends Car {
    private String color;
    private Boolean hasPapers;
}
