package pointer.Pointer_Spring.validation;

public class Regex {
    //전반적인 정규표현식
    public static final String PATTERN_NUMBER = "^[\\\\d]*$"; // 숫자만 허용하는 정규식
    public static final String PATTERN_CHAR ="^[\\w]*$"; //문자열만 허용하는 정규식 - 공백 미 허용
    public static final String PATTERN_NO_SPACE = "^[\\\\S]*$"; //공백, 탭이 아닌 경우를 허용하는 정규식
    //Member

    //@Email로 대체 가능
    public static final String email = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
    public static final String pw = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}";//"비밀번호 8~16자 영문 대 소문자, 숫자, 특수문자
    public static final String id = "^[a-z]{1}[a-z0-9]{5,10}+$"; // 영문 숫자 조합 6~10자리

    //BaseEntity
    public static final String date = "^[\\d]{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";//ex) 2022-08-03 (YYYY-MM-DD)

    //Credit card
    public static final String creditCardNumber = "^([\\d]{4}[- ]){3}[\\d]{4}|[\\d]{16}$";

    //UUID
    private static final String UUID = "^[a-zA-Z0-9+/]*={0,2}$";

//    @URL(message = "URL should be valid")
//    private String url;
}

//정규식으로 안하고 사용하면 되는 것들
//null -> @NotNull, @Nullable
//빈 문자열 -> isEmpty()
