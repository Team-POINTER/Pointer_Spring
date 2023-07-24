package pointer.Pointer_Spring.validation;

import java.util.Arrays;

import static pointer.Pointer_Spring.validation.HttpStatus.*;

public enum ExceptionCode {
    /**
     * 회원가입 및 로그인
     */
    SIGNUP_CREATED_OK(CREATED, "A000", "회원가입 성공"),
    SIGNUP_COMPLETE(CREATED, "A001", "이미 존재하는 회원"),
    SIGNUP_DUPLICATED_ID(DUPLICATED_VALUE, "A002", "ID 중복"),
    SIGNUP_DUPLICATED_USERNAME(DUPLICATED_VALUE, "A003", "USERNAME 중복"),
    USER_KAKAO_INVALID(NOT_FOUND_VALUE, "A004","카카오 소셜 로그인 실패"),
    SIGNUP_LIMITED_ID(NOT_FOUND_VALUE, "A005","제한된 회원"),
    SIGNUP_PERMANENT_LIMITED_ID(NOT_FOUND_VALUE, "A006","영구 제한된 회원"),
    RESIGN_OK(SUCCESS,"A007", "회원 탈퇴 성공"),

    LOGIN_OK(SUCCESS, "B000", "로그인 성공"),
    LOGIN_NOT_FOUND_ID(NOT_FOUND_VALUE, "B001", "로그인 실패"),
    LOGIN_NOT_FOUND_PW(NOT_FOUND_VALUE, "B002", "로그인 실패"),
    LOGOUT_OK(SUCCESS, "B003", "로그아웃 성공"),
    LOGOUT_INVALID(NOT_FOUND_VALUE, "B004", "로그아웃 실패"),

    /**
     * 회원정보
     */
    USER_GET_OK(SUCCESS, "C000", "회원정보 있음"),
    USER_NOT_FOUND(NOT_FOUND_VALUE, "C001", "회원정보 없음"),
    USER_FRIEND_NOT_FOUND(NOT_FOUND_VALUE, "C002", "상대 회원정보 없음"),

    USER_SAVE_ID_OK(SUCCESS, "C003", "ID 저장 성공"),
    USER_CHECK_ID_OK(SUCCESS, "C004", "ID 중복 확인 성공"),
    USER_NO_CHECK_ID(SUCCESS, "C005", "ID 중복 확인 없음"),
    USER_DUPLICATED_ID(DUPLICATED_VALUE, "C008", "중복된 아이디"),
    USER_EXCEED_ID(DUPLICATED_VALUE, "C009", "ID 생성 실패"),

    USER_AGREE_INVALID(INVALID_ACCESS, "C010", "약관에 동의하지 않은 사용자"),
    USER_AGREE_OK(SUCCESS, "C011", "약관 동의 성공"),

    USER_UPDATE_OK(SUCCESS, "D000", "회원정보 수정 성공"),
    IMAGE_GET_OK(SUCCESS, "D001", "사진 조회 성공"),
    USER_IMAGE_UPDATE_INVALID(NOT_FOUND_VALUE, "D002", "회원 사진 수정 실패"),
    BACKGROUND_IMAGE_UPDATE_INVALID(NOT_FOUND_VALUE, "D003", "배경 사진 수정 실패"),
    IMAGE_INVALID(INVALID_ACCESS, "D004", "유효하지 않은 파일"),
    IMAGE_NOT_FOUND(NOT_FOUND_VALUE, "D005", "존재하지 않는 이미지"),
    USER_IMAGE_UPDATE_SUCCESS(SUCCESS, "D006", "회원 사진 수정 성공"),
    BACKGROUND_IMAGE_UPDATE_SUCCESS(SUCCESS, "D007", "배경 사진 수정 성공"),


    /**
     * 채팅
     */
    CHATROOM_GET_OK(SUCCESS, "E000", "해당 채팅방 있음"),
    CHATROOM_NOT_FOUND(NOT_FOUND_VALUE, "E001", "채팅방을 찾을 수 없습니다."),

    /**
     * 권한여부
     */
    AUTHORITY_HAVE(SUCCESS, "F000", "수정/삭제 권한이 있습니다"),
    AUTHORITY_NOT_HAVE(NOT_FOUND_VALUE, "F001", "수정/삭제 권한이 없습니다."),

    /**
     *  토큰
     */
    INVALID_JWT_SIGNATURE(UNAUTHORIZED,"G000", "타당하지 않은 JWT 서명 오류"),
    INVALID_JWT_TOKEN(UNAUTHORIZED,"G001", "잘못된 JWT 토큰 오류"),
    EXPIRED_JWT_TOKEN(UNAUTHORIZED,"G002", "만료된 JWT 토큰 오류"),
    UNSUPPORTED_JWT_TOKEN(UNAUTHORIZED,"G003", "지원되지 않는 JWT 토큰 오류"),
    JWT_CLAIMS_STRING_IS_EMPTY(UNAUTHORIZED,"G004", "JWT 클레임 문자열이 비어 있음 오류"),
    TOKEN_SUCCESS(SUCCESS, "G005", "토큰 확인 성공"),


    REISSUE_TOKEN(SUCCESS, "H000", "reissued token"),
    INVALID_REFRESH_TOKEN(INVALID_ACCESS, "H001", "non-existent refresh-token"),
    //OLD_REFRESH(INVALID_ACCESS,"H002", "올바르지 않은 refresh 토큰입니다"),
    //NO_REFRESH(INVALID_ACCESS, "H003", "유효하지 않는 refresh 토큰입니다"),
    //NO_ACCESS(INVALID_ACCESS, "H004", "유효하지 않은 refresh 토큰입니다"),

    /**
     * 초대
     */
    INVITATION_GET_OK(SUCCESS, "I000", "초대 링크 조회 성공"),
    INVITATION_NOT_FOUND(SUCCESS, "I001", "초대 링크 접근 실패"),
    INVITATION_DUPLICATED_PERSON(DUPLICATED_VALUE, "I002", "이미 초대된 사용자"),

    /**
     * 친구
     */
    FRIEND_REQUEST_OK(SUCCESS, "J000", "친구 요청 성공"),
    FRIEND_REQUEST_NOT(INVALID_ACCESS, "J001", "친구 요청 실패"),
    FRIEND_ACCEPT_OK(SUCCESS, "J002", "친구 수락 성공"),
    FRIEND_ACCEPT_NOT(INVALID_ACCESS, "J003", "친구 수락 실패"),
    FRIEND_CANCEL_OK(SUCCESS, "J004", "친구 요청 취소"),
    FRIEND_REFUSE_OK(SUCCESS, "J005", "친구 거절 성공"),
    FRIEND_FIND_OK(SUCCESS, "J006", "친구 조회 성공"),
    USER_FIND_FRIEND_FAIL(SUCCESS, "J007", "유저 조회 실패"),
    FRIEND_BLOCK_OK(SUCCESS, "J008", "친구 차단 성공"),
    FRIEND_CANCEL_NOT(INVALID_ACCESS, "J009", "친구 취소 요청 실패"),
    FRIEND_REQUEST_CANCEL_OK(SUCCESS, "J010", "요청 취소 성공"),
    FRIEND_REQUEST_CANCEL_NOT(SUCCESS, "J011", "요청 취소 실패"),

    /**
     * room
     */
    ROOMNAME_VERIFY_OK(SUCCESS, "J000", "룸 이름 변경 성공"),
    ROOMMEMBER_NOT_EXIST(INVALID_ACCESS, "J001", "룸 멤버 존재하지 않음"),
    ROOM_CREATE_SUCCESS(CREATED, "J002", "룸 생성 성공"),
    ROOM_CREATE_FAIL(INVALID_ACCESS, "J003", "룸 생성 실패"),
    ROOM_FOUND_OK(SUCCESS, "J0010", "룸 조회 성공"),
    ROOM_NOT_FOUND(NOT_FOUND_VALUE, "J004", "룸 조회 실패"),
    ROOM_CREATE_OVER_LIMIT(INVALID_ACCESS, "J005", "룸 생성 가능 개수 초과"),
    ROOM_EXIT_SUCCESS(SUCCESS, "J006", "룸 나가기 성공"),
    ROOM_NAME_INVALID(INVALID_ACCESS, "J007", "형식에 맞지 않는 룸 이름"),
    ROOM_NAME_INVITATION(SUCCESS, "J008", "초대 성공"),
    ROOMMEMBER_DUPLICATE_VALUE(INVALID_ACCESS, "J009", "sql 룸 멤버 중복 에러"),
    ROOMMEMBER_GET_SUCCESS(SUCCESS, "J0011", "룸 멤버 조회 성공"),
    INVITATION_LIST_GET_SUCCESS(SUCCESS, "J0012", "초대 목록 조회 성공"),

    /**
     * 질문
     */
    QUESTION_CREATED_FAILED(INVALID_ACCESS, "K000", "질문 생성 실패"),
    CURRENT_QUESTION_NOT_FOUND(INVALID_ACCESS, "K001", "현재 질문이 없습니다."),
    QUESTION_NOT_FOUND(INVALID_ACCESS, "K002", "질문이 없습니다."),
    QUESTION_DELETE_NOT_AUTHENTICATED(INVALID_ACCESS, "K003", "질문 삭제 권한이 없습니다."),

    HINT_NOT_FOUND(INVALID_ACCESS, "K004", "힌트를 찾을 수 없습니다."),


    /**
     * 신고
     */
    REPORT_CREATE_SUCCESS(CREATED, "L000", "신고 생성 성공"),
    REPORT_NOT_FOUND(NOT_FOUND_VALUE, "L001", "신고 존재하지 않음"),
    REPORTED_USER(INVALID_ACCESS, "L002", "일시적 신고 처리된 유저"),
    ALREADY_REPORT(DUPLICATED_VALUE, "L003", "이미 처리된 신고"),
    REPORT_GET_SUCCESS(SUCCESS, "L004", "신고 조회 성공"),
    REPORT_HANDLE_SUCCESS(SUCCESS, "L005", "신고 처리 성공"),

    /**
     * 알림
     */

    ACTIVE_ALARM_NOT_FOUND(NOT_FOUND_VALUE, "L000", "활동 알림이 존재하지 않습니다"),
    KAKAO_TOKEN_REGISTER_FAIL(INVALID_ACCESS, "L001", "카카오 토큰 등록 실패"),
    KAKAO_PUSH_SEND_FAIL(INVALID_ACCESS, "L002", "카카오 푸시 전송 실패"),

    /**
     * 잘못된 ExceptionCode
     */
    INVALID_FORM(INVALID_ACCESS, "Z000", "형식에 어긋난 이름"),
    EMPTY(null, "", "");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ExceptionCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ExceptionCode findExceptionCodeByCode(String code) {
        return Arrays.stream(ExceptionCode.values())
                .filter(x -> x.getCode().equals(code))
                .findFirst()
                .orElse(EMPTY);
    }
}
