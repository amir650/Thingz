package shared;

import java.awt.*;
import java.util.Random;

public enum Utils {
    INSTANCE;

    public static final double AUTOMOTON_SPEED_NORMAL = .450;
    public static final double AUTOMOTON_SPEED_WHEN_RETURNING_FOOD = .15;
    public static final double NOTIFICATION_SENSETIVITY = 1;
    public static final Random R = new Random();
    public static final double VELOCITY_DAMPENING = .96;
    public static final int DEFAULT_ENERGY = 30;
    public static final Color nukeColor = Color.GRAY;
    public static final Color resourceColor = Color.CYAN;
    public static final int resourceSize = 7;
    public static final int numResourcesToDrop = 1000;
}
