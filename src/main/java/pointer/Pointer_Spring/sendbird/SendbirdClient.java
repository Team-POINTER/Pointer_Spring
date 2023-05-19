package pointer.Pointer_Spring.sendbird;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "sendbird", url = "https://api-${sendbird.app}.sendbird.com/v3")
public interface SendbirdClient {

    @PostMapping("/users")
    ResponseEntity<Map<String, Object>> createUser(
        @RequestHeader("Api-Token") String apiToken,
        @RequestBody Map<String, String> body);

    @PostMapping("/users/{user_id}/token")
    ResponseEntity<Map<String, Object>> getAccessToken(
        @PathVariable("user_id") String userId,
        @RequestHeader("Api-Token") String apiToken,
        @RequestBody Map<String, String> body);

}