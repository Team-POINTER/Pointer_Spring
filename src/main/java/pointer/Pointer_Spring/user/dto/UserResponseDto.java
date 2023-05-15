package pointer.Pointer_Spring.user.dto;

import pointer.Pointer_Spring.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String email;

    public static UserResponseDto of(User user) {
        return new UserResponseDto(user.getEmail());
    }
}
