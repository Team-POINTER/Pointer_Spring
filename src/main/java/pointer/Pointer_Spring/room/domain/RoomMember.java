package pointer.Pointer_Spring.room.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.config.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "RoomMember")
@Table(name = "RoomMember", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueRoomAndUser", columnNames = {"Room_room_id", "User_user_id"})})
public class RoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roommember_id", unique = true)
    private Long roomMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Room_room_id")
    private Room room;

    @Column(name = "private_room_name")
    private String privateRoomNm;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_user_id")
    private User user;

    private Boolean vote;


    @Builder
    public RoomMember(Room room, User user) {
        this.room = room;
        this.user = user;
        this.privateRoomNm = room.getName();
        this.vote = false;
    }

    public void updatePrivateRoomNm (String privateRoomNm) {
        this.privateRoomNm = privateRoomNm;
    }
}
