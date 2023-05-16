package pointer.Pointer_Spring.room.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.config.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "RoomMember")
public class RoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roommember_id", unique = true)
    private Long roomMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "private_room_name")
    private String privateRoomNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean vote;

    @Builder
    public RoomMember(Room room, User user) {
        this.room = room;
        this.user = user;
    }

    public void updateRoomMember (String privateRoomNm) {
        this.privateRoomNm = privateRoomNm;
    }
}
