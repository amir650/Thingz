package shared;

import java.awt.*;
import java.util.Random;

public enum Utils {
    ;
    public static final double AUTOMATON_SPEED_NORMAL = .450;
    public static final double AUTOMATON_SPEED_WHEN_RETURNING_FOOD = .15;
    public static final double NOTIFICATION_SENSITIVITY = 1;
    public static final Random R = new Random();
    public static final double VELOCITY_DAMPENING = .96;
    public static final int DEFAULT_ENERGY = 30;
    public static final Color nukeColor = Color.GRAY;
    public static final Color resourceColor = Color.CYAN;
    public static final int resourceSize = 5;
    public static final int numResourcesToDrop = 1000;
    public static final int AUTOMATON_AGE_LIMIT = 3000;
    public static final int AUTOMATON_REMOVE_CORPSE_AGE = 3300;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final int MAX_HIVE_SIZE = 3000;
    public static final int DISTANCE_MULTIPLIER = 90;
    public static final BasicStroke wideStroke = new BasicStroke(1.5f);
    public static final BasicStroke stroke = new BasicStroke(1.5f);
}
