package com.ayoub;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum Coin {
    half(0.5),
    one(1.0),
    two(2.0),
    five(5.0),
    ten(10.0);

    private final double value;

    Coin(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static List<Coin> sortedDescending() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingDouble(Coin::getValue).reversed())
                .toList();
    }
}

