package pointer.Pointer_Spring.kakao;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private OauthService oauthService;

    @ResponseBody
    @GetMapping("/kakao") // Redirect URI : /oauth/kakao
    public void kakaoCallback(@RequestParam String code) {
        String accessToken = oauthService.getKakaoAccessToken(code);
        oauthService.getKakaoUser(accessToken);
    }

}
