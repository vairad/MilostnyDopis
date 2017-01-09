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
}