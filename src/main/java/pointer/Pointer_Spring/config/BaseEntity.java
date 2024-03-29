package pointer.Pointer_Spring.config;


import lombok.*;
//원래 코드
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@Setter
@DynamicInsert
@NoArgsConstructor
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false, name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Column(name = "status", columnDefinition = "int default 1")
    private int status = 1; // 상속관계 자동 매핑으로 자동으로 insert됨

    public void delete() {
        this.status = 0;
    }

    // 삭제된 데이터 복구
    protected void restore() {
        this.status = 1;
    }
}
