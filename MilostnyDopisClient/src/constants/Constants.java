package constants;

import java.util.Random;

public class Constants{
    public static final int NICKNAME_MIN_LENGTH = 3;
    public static final int NICKNAME_MAX_LENGTH = 40;
    // TODO: 31.12.16 move message offsets to constant class
    public static final int OFFSET_ = 3;


    public static final int CARD_USE_CLICK_COUNT = 1;

    public static final double TRANSLATE_DURATION = 1;

    public static final Random random = new Random();

    public static final String USERS_FILE = "users.xml";

    public static final int MINW_APP = 500;
    public static final int MINH_APP = 500;

    public static final int MINW_GAME = 500;
    public static final int MINH_GAME = 500;

    public static final int PREFW_GAME = 500;
    public static final int PREFH_GAME = 500;

    // CONTROLLER
    public static final int MIN_POSSIBLE_ROUNDS = 1;
    public static final int MAX_POSSIBLE_ROUNDS = 20;

    public static final int MIN_POSSIBLE_PLAYERS = 2;
    public static final int MAX_POSSIBLE_PLAYERS = 4;

    public static int RECONNECT_TIMEOUT_MS = 15000;
}