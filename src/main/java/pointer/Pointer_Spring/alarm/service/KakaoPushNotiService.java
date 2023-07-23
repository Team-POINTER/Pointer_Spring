package pointer.Pointer_Spring.alarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KakaoPushNotiService {
    @Value("${kakao.admin.key}")
    private String adminKey;
    @Value("${kakao.url}")
    private String kakaoUrl;
    @Value("${kakao.push.register.uri}")
    private String registerUri;
    @Value("${kakao.push.send.uri}")
    private String sendUri;
    @Value("${kakao.push.apns_env}")
    private String apnsEnv;


    public void registKakaoToken(Long userId, AlarmDto.KakaoTokenRequest request) {
        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .build();

        try {
            Integer code = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("v2", "push", "register")
                            .build())
                    .body(BodyInserters
                            .fromFormData("uuid", String.valueOf(userId))
                            .with("device_id", request.getDeviceId())
                            .with("push_type", request.getPushType())
                            .with("push_token", request.getPushToken())
                    )
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(ExceptionCode.KAKAO_TOKEN_REGISTER_FAIL);
        }
    }

    public void sendKakaoPush(List<String> uuids, AlarmDto.KakaoPushRequest request) {
        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .build();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        String jsonString = "";
            jsonString = gson.toJson(request);


        webClient.post()
                .uri(sendUri)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .body(BodyInserters.fromFormData("uuids", gson.toJson(uuids))
                        .with("push_message", jsonString)
                )
                .retrieve()
                .bodyToMono(Integer.class)
                .block();
//        Map<String, Object> formData = new HashMap<>();
//        formData.put("uuids", String.join(",", request.getUuids()));
//        formData.put("push_message", Map.of("for_apns", Map.of("message", request.getMessage())));
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String pushMessageJson;
//        try {
//            objectMapper.writeValueAsString(formData);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        webClient.post()
//                .uri(sendUri)
////                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
////                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
//                .bodyValue(BodyInserters.fromFormData()
//                        .with())
//                .retrieve()
//                .bodyToMono(Integer.class)
//                .block();

//        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//        formData.add("uuids", request.getUuids());
//
//        Map<String, Object> pushMessage = new HashMap<>();
//        pushMessage.put("for_apns", Map.of("message",request.getMessage()));
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String pushMessageJson;
//        formData.add("push_message", pushMessage);

//        try {
//            System.out.println(objectMapper.writeValueAsString(formData));
//        } catch (JsonProcessingException e) {
//            // Handle JSON serialization error
//            log.info(e.getMessage());
//            return;
//        }




//        try {
//            webClient.post()
//                    .uri(uriBuilder -> uriBuilder
//                            .pathSegment("v2", "push", "send")
//                            .build())
//                    .body(BodyInserters
//                            .fromMultipartData("uuids", request.getUuids())
//                            .with("push_message", request.getPushMessage())
//                            //.with("apns_env", apnsEnv)
//                    )
//                    .retrieve()
//                    .bodyToMono(Integer.class)
//                    .block();
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            throw new CustomException(ExceptionCode.KAKAO_PUSH_SEND_FAIL);
//        }

    }


}
