package pointer.Pointer_Spring.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "image")
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", unique = true)
    private Long imageId;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private ImageType imageSort;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public enum ImageType {
        BACKGROUND, PROFILE
    }

    // builder
    @Builder
    public Image(String imageUrl, ImageType imageSort, User user) {
        this.imageUrl = imageUrl;
        this.imageSort = imageSort;
        this.user = user;
    }
}
