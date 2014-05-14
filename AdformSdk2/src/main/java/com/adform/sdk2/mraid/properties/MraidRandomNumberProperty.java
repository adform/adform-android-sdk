package com.adform.sdk2.mraid.properties;

import java.util.Random;

/**
 * Created by mariusm on 08/05/14.
 * A property that hold a randomly generated number. Number is always positive.
 */
public class MraidRandomNumberProperty extends MraidBaseProperty {
    private final int mRandomNumber;

    MraidRandomNumberProperty(int randomNumber) {
        mRandomNumber = randomNumber;
    }

    public static MraidRandomNumberProperty createWithRandomNumber() {
        Random r = new Random();
        int randomNumber = r.nextInt();
        return new MraidRandomNumberProperty(
                (randomNumber < 0)?(randomNumber*-1):randomNumber);
    }


    @Override
    public String toGet() {
        return "rnd="+ mRandomNumber;
    }
    @Override
    public String toJson() {
        return null;
    }
}
