package pointer.Pointer_Spring.alarm.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KakaoPushNotiService {
    private final UserRepository userRepository;
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

    public KakaoPushNotiService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void registKakaoToken(UserPrincipal userPrincipal, AlarmDto.KakaoTokenRequest request) {
        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .build();

        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("v2", "push", "register")
                            .build())
                    .body(BodyInserters
                            .fromFormData("uuid", String.valueOf(userPrincipal.getId()))
                            .with("device_id", request.getDeviceId())
                            .with("push_type", request.getPushType())
                            .with("push_token", request.getPushToken())
                    )
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        // WebClientResponseException은 서버로부터 오류 응답을 받은 경우 발생하는 예외
                        String errorMessage = ex.getResponseBodyAsString();
                        int statusCode = ex.getRawStatusCode();
                        log.info("Push 알림 토큰 등록 오류");
                        log.info("상태 코드: {}", statusCode);
                        log.info("오류 메시지: {}" , errorMessage);
                    })
                    .doOnError(Throwable.class, ex -> {
                        // 그 외 예상치 못한 예외가 발생한 경우 처리할 작업
                        log.info("Push 알림 토큰 등록 중 예상치 못한 오류 발생!");
                        ex.printStackTrace();
                    })
                    .subscribe();
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(ExceptionCode.KAKAO_TOKEN_REGISTER_FAIL);
        }

        Optional<User> o = userRepository.findByUserIdAndStatus(userPrincipal.getId(),1);
        if(!o.isEmpty()) {
            User user = o.get();
            user.setDeviceId(request.getDeviceId());
            user.setPushToken(request.getPushToken());
            user.setApnsEnv(request.getApnsEnv());
            userRepository.save(user);
        }
    }


    public void deRegisterKakaoToken(Long userId, AlarmDto.KakaoTokenDeRegisterRequest request) {
        WebClient webClient = WebClient.builder()
                .baseUrl(kakaoUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                .build();

        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("v2", "push", "deregister")
                            .build())
                    .body(BodyInserters
                            .fromFormData("uuid", String.valueOf(userId))
                            .with("push_type", request.getPushType())
                            .with("push_token", request.getPushToken())
                    )
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        // WebClientResponseException은 서버로부터 오류 응답을 받은 경우 발생하는 예외
                        String errorMessage = ex.getResponseBodyAsString();
                        int statusCode = ex.getRawStatusCode();
                        log.info("Push 알림 토큰 삭제 오류");
                        log.info("상태 코드: {}", statusCode);
                        log.info("오류 메시지: {}" , errorMessage);
                    })
                    .doOnError(Throwable.class, ex -> {
                        // 그 외 예상치 못한 예외가 발생한 경우 처리할 작업
                        log.info("Push 알림 토큰 등록 중 예상치 못한 오류 발생!");
                        ex.printStackTrace();
                    })
                    .subscribe();
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
                .doOnSuccess(response -> {
                    // 요청이 성공적으로 완료된 경우 처리할 작업
                    System.out.println("Push 알림을 성공적으로 보냈습니다.");
                    System.out.println("응답 메시지: " + response);
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    // WebClientResponseException은 서버로부터 오류 응답을 받은 경우 발생하는 예외
                    String errorMessage = ex.getResponseBodyAsString();
                    int statusCode = ex.getRawStatusCode();
                    log.info("Push 알림 전송 중 오류 발생!");
                    log.info("상태 코드: {}", statusCode);
                    log.info("오류 메시지: {}" , errorMessage);
                })
                .doOnError(Throwable.class, ex -> {
                    // 그 외 예상치 못한 예외가 발생한 경우 처리할 작업
                    log.info("Push 알림 전송 중 예상치 못한 오류 발생!");
                    ex.printStackTrace();
                })
                .subscribe();
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
