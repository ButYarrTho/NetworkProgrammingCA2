package protocols;

public class EmailUtilities {

    public static final String HOSTNAME       = "localhost";
    public static final int    PORT           = 15000;

    public static final String DELIMITER      = "__";
    public static final String LIST_DELIMITER = ",";
    public static final String SUBDELIMITER   = "::";
    public static final String TIMESTAMP      = "yyyy-MM-dd HH:mm";

    public static final String LOGIN           = "LOGIN";
    public static final String REGISTER        = "REGISTER";
    public static final String SEND            = "SEND";
    public static final String LIST_RECEIVED   = "LIST_RECEIVED";
    public static final String LIST_SENT       = "LIST_SENT";
    public static final String SEARCH_RECEIVED = "SEARCH_RECEIVED";
    public static final String SEARCH_SENT     = "SEARCH_SENT";
    public static final String READ            = "READ";
    public static final String DELETE          = "DELETE";
    public static final String LOGOUT          = "LOGOUT";
    public static final String EXIT            = "EXIT";

    public static final String LOGIN_SUCCESS       = "LOGIN_SUCCESS";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ALREADY_LOGGED_IN   = "ALREADY_LOGGED_IN";
    public static final String REGISTER_SUCCESS    = "REGISTER_SUCCESS";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String EMAIL_SENT          = "EMAIL_SENT";
    public static final String USER_NOT_FOUND      = "USER_NOT_FOUND";
    public static final String RECEIVED            = "RECEIVED";
    public static final String SENT                = "SENT";
    public static final String NO_EMAILS           = "NO_EMAILS";
    public static final String EMAIL_CONTENT       = "EMAIL_CONTENT";
    public static final String EMAIL_NOT_FOUND     = "EMAIL_NOT_FOUND";
    public static final String EMAIL_DELETED       = "EMAIL_DELETED";
    public static final String LOGOUT_SUCCESS      = "LOGOUT_SUCCESS";
    public static final String BYE                 = "BYE";
    public static final String INVALID_REQUEST     = "INVALID_REQUEST";
    public static final String NEW_EMAIL           = "NEW_EMAIL";
    public static final String UNAUTHENTICATED     = "UNAUTHENTICATED";

    private EmailUtilities() { }
}
