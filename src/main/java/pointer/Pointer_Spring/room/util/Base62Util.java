package pointer.Pointer_Spring.room.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class Base62Util {

    private final int BASE62 = 62;
    private final String BASE62_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String encodeToBase62(String uuid) {
        byte[] bytes = uuid.getBytes(StandardCharsets.UTF_8);
        byte[] encodedBytes = Base64.encodeBase64(bytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }


    public String decodeFromBase62(String base62Encoded) {
        byte[] bytes = base62Encoded.getBytes(StandardCharsets.UTF_8);
        byte[] decodedBytes = Base64.decodeBase64(bytes);
        String uuidString = new String(decodedBytes, StandardCharsets.UTF_8);
        return uuidString;
//        return UUID.fromString(uuidString);
    }

    public String longToUuidString(long value) {
        return new UUID(value, value).toString();
    }

    public String urlEncoder(String seq) throws NoSuchAlgorithmException {
        String encodeStr = encodeToBase62(seq);
        return encodeStr;
    }

    public String urlDecoder(String encodeStr) throws NoSuchAlgorithmException {
        return decodeFromBase62(encodeStr);
    }


}