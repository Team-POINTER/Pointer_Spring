package pointer.Pointer_Spring.auth.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.User.service.UserService;
import pointer.Pointer_Spring.auth.dto.KakaoDto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:config.properties")
public class KakaoAuthService {

    @Autowired
    private UserService userService;

    @Value("${kakao.restAPI}")
    private String restApiKey;

    @Value("${kakao.redirectURI}")
    private String redirectUri;

    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST 요청

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+restApiKey); // REST_API_KEY
            sb.append("&redirect_uri="+redirectUri); // REDIRECT_URI
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            //System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            access_Token = jsonObject.get("access_token").getAsString();
            refresh_Token = jsonObject.get("refresh_token").getAsString();

            //System.out.println("access token : " + access_Token);
            //System.out.println("refresh token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("유효하지 않은 코드");
        }
        return access_Token;
    }

    public KakaoDto getKakaoUser(String token) { //throws BaseException
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = conn.getResponseCode();
            //System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            br.close();
            // System.out.println("response body : " + result);

            JsonObject element = JsonParser.parseString(result).getAsJsonObject();
            String id = element.get("id").getAsString();
            boolean hasEmail = element.get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();

            String email = "";
            if (hasEmail) {
                email = element.get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            //System.out.println("email : " + email);
            String nickname = element.get("properties").getAsJsonObject().get("nickname").getAsString();
            //System.out.println("nickname : " + nickname);

            return KakaoDto.builder()
                    .id(id)
                    .email(email)
                    .nickname(nickname)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰");
        }
    }

    @Transactional
    public boolean login(String code) {
        try {
            String accessToken = getKakaoAccessToken(code);
            KakaoDto kakaoDto = getKakaoUser(accessToken); // kakaoDto에 담기
            userService.saveKakaoUser(kakaoDto);

            // 토큰 생성
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}