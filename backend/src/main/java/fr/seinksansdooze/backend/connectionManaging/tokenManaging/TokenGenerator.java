package fr.seinksansdooze.backend.connectionManaging.tokenManaging;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator implements ITokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe


    public String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}
