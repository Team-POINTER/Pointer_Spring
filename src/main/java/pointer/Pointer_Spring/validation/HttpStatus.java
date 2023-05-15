package pointer.Pointer_Spring.validation;

import lombok.Getter;

@Getter
public enum HttpStatus {
    SUCCESS(200),
    CREATED(201),
    UNAUTHORIZED(401),
    NOT_FOUND_VALUE(404),
    DUPLICATED_VALUE(409),
    INVALID_ACCESS(403);

    public int value;

    HttpStatus(int value) {
        this.value = value;
    }

    스프링 validation을 쓰면 method not aur~error -> 이 예외를 exeoption controller에서 다 처리를 하게 되고
            유효성 검사 말고 exception class는 추가해야할 수 있다.
}
