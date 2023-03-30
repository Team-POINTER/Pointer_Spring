package pointer.Pointer_Spring.User.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "TOKEN")
public class Token extends BaseEntity {

    @Id
    private String email;

    @Column(columnDefinition = "LONGTEXT")
    private String value;

    @Builder
    public Token(String email, String value) {
        this.email = email;
        this.value = value;
    }

    public Token updateValue(String token) {
        this.value = token;
        return this;
    }
}
