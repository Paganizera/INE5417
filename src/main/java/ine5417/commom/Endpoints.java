package ine5417.commom;

public final class Endpoints {
    public static final String CREATE_CHANNEL = "/create_channel";
    public static final String CHANNEL = "/{channel}";
    public static final String CIPHER = CHANNEL + "/cipher";
    public static final String DECIPHER = CHANNEL + "/decipher";
    public static final String BRUTEFORCE = CHANNEL + "/bruteforce";
    public static final String LIST_CIPHERS = "/list_ciphers";
}
