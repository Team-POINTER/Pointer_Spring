package pointer.Pointer_Spring.sendbird;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendbirdService {

    private final SendbirdClient sendbirdClient;

    @Value("${sendbird.app}")
    private String APP_ID;

    @Value("${sendbird.api}")
    private String API_TOKEN;

    public Map<String, Object> createUser(String userId, String userNickname,
        String userAvatarUrl) {

        Map<String, String> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("nickname", userNickname);
        body.put("profile_url", userAvatarUrl);

        ResponseEntity<Map<String, Object>> response = sendbirdClient.createUser(API_TOKEN, body);

        if (response.getStatusCode().isError()) {
            log.error(
                "Error creating Sendbird user: " + response.getBody().get("message"));
            return null;
        } else {
            log.info("Sendbird user created: " + response.getBody());
            return response.getBody();
        }
    }

    public Map<String, Object> getAccessToken(String userId) {

        Map<String, String> body = new HashMap<>();

        ResponseEntity<Map<String, Object>> response = sendbirdClient.getAccessToken(userId,
            API_TOKEN,
            body);

        if (response.getStatusCode().isError()) {
            log.error(
                "Error getting Sendbird AccessToken: " + response.getBody().get("message"));
            return null;
        } else {
            log.info("Sendbird AccessToken issued: " + response.getBody());
            return response.getBody();
        }
    }
}
