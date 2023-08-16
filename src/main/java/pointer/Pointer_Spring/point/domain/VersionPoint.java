package pointer.Pointer_Spring.point.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "VersionPoint")
public class VersionPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_point_id")
    private Long id;

    @Column(name = "version", unique = true)
    private String version;

    @Column(name = "point")
    private int point;

    @Column(name = "phrase")
    private String phrase;

    @Builder
    public VersionPoint(String version, int point, String phrase) {
        this.version = version;
        this.point = point;
        this.phrase = phrase;
    }
}
