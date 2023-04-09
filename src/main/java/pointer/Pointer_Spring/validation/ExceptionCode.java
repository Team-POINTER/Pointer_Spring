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

    LOGIN_OK(SUCCESS, "B000", "로그인 성공"),
    LOGIN_NOT_FOUND_ID(NOT_FOUND_VALUE, "B001", "로그인 실패"),
    LOGIN_NOT_FOUND_PW(NOT_FOUND_VALUE, "B002", "로그인 실패"),
    LOGOUT_OK(SUCCESS, "B003", "로그아웃 성공"),
    LOGOUT_STATE(UNAUTHORIZED, "B004", "로그아웃 상태"),

    /**
     * 회원정보
     */
    USER_GET_OK(SUCCESS, "C000", "회원정보 있음"),
    USER_NOT_FOUND(NOT_FOUND_VALUE, "C001", "회원정보 없음"),

    USER_UPDATE_OK(SUCCESS, "D000", "회원정보 수정 성공"),
    USER_UPDATE_INVALID(NOT_FOUND_VALUE, "D001", "회원정보 수정 실패"),

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
    EXPIRED_TOKEN(INVALID_ACCESS, "G000","expired access-token"),
    MALFORMED_TOKEN(INVALID_ACCESS, "G001","incorrect access-token"),
    UNAUTHORIZED_TOKEN(INVALID_ACCESS, "G002", "invalid access-token"),

    REISSUE_TOKEN(SUCCESS, "H000", "reissued token"),
    INVALID_REFRESH_TOKEN(INVALID_ACCESS, "H001", "non-existent refresh-token"),
    //OLD_REFRESH(INVALID_ACCESS,"H002", "올바르지 않은 refresh 토큰입니다"),
    //NO_REFRESH(INVALID_ACCESS, "H003", "유효하지 않는 refresh 토큰입니다"),
    //NO_ACCESS(INVALID_ACCESS, "H004", "유효하지 않은 refresh 토큰입니다"),

    /**
     * 잘못된 ExceptionCode
     */
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
