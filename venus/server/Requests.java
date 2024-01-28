package venus.server;

public class Requests {
    
    public final static String WINNER = "wnr";
    public final static String TIE = "tie";
    
    public final static String LOGIN_USER = "user-login";
    public final static String LOGIN_FAILED = "user-login-failed";
    public final static String LOGIN_SUCCESS = "user-login-succes";
    public final static String LOGIN_ALREADY_ONLINE = "user-already-online";
    public final static String LOGOUT = "logout";
    
    public final static String UPDATE_INFO = "update-info"; 
    
    public final static String CREATE_USER = "create-user";
    public final static String USER_CREATED = "user-created";
    
    public final static String VALIDATE_USER_NAME = "validate-user-name";
    public final static String USER_NAME_INVALID = "invalid-user-name";
    public final static String USER_NAME_VALID = "valid-user-name";
    
    public final static String PUBLIC_KEY_REQUEST = "public-key-please";
    public final static String PUBLIC_KEY = "public-key";
    public final static String AES_KEY_REQUEST = "aes-key-please";
    public final static String AES_KEY = "aes-key";
    
    public final static String GREET = "hello";
    
    public final static String ONLINE_LIST_CHANGED = "online-list-changed";
    public final static String UPDATE_ONLINE_LIST = "update-online-list";
    public final static String REQUEST_ONLINE_LIST = "request-online-list";
    public final static String EOL = "end-of-list";
    
    public final static String USER_NAME_REQUEST = "user-name-please";
    public final static String WIN_COUNT_REQUEST = "win-count-please";
    public final static String GAME_COUNT_REQUEST = "game-count-please";
    
    public final static String CHALLENGE_USER = "challenge-user";
    public final static String CHALLENGE_FROM = "challenge-from";
    public final static String CHALLENGE_ACCEPT = "challenge-accept";
    public final static String CHALLENGE_DECLINED = "challenge-declined";
    public final static String USER_OFFLINE = "user-offline";
    
    public final static String CHALLENGES_UPDATE = "challenges-update";
    public final static String NO_CHALLENGES = "no-challenges";
    
    public final static String START_GAME = "start-game";
    public final static String USER_UNAVAILABLE = "user-unv";
    public final static String CLOSE_GAME = "close-game";
    public final static String GAME_DATA = "game-data";
    public final static String PLACE_COIN = "place-coin";
    public final static String NOT_YOUR_TURN = "not-your-turn";
    public final static String GAME_DISPLAY_DATA = "gdd";
    public final static String QUIT_GAME = "qtg";
}
