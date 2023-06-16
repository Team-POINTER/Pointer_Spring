package pointer.Pointer_Spring.friend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "friend")
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "friend_name")
    private String friendName;

    @Column(name = "user_friend_id", nullable = false)
    private Long userFriendId;

    // relation
    public enum Relation {
        REFUSE, REQUEST, SUCCESS
    }

    @Column(nullable = false)
    private Relation relationship;

    public void setRelationship(Relation relationship) {
        this.relationship = relationship;
    }

    // builder
    @Builder
    public Friend(User user,String friendName, Long userFriendId, Relation relationship) {
        this.user = user;
        this.friendName = friendName;
        this.userFriendId = userFriendId;
        this.relationship = relationship;
    }

}