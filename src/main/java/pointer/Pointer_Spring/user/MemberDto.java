package pointer.Pointer_Spring.user;

import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.validation.Regex;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MemberDto extends BaseEntity {

    @Pattern(regexp = Regex.email, message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = Regex.pw, message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    String pw;


    String id;


    String name;
}
