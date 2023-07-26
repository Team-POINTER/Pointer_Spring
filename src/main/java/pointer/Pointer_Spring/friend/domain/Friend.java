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
@Entity(name = "Friend")
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id", unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "friend_name", nullable = false)
    private String friendName;

    @Column(name = "user_friend_id", nullable = false)
    private Long userFriendId;

    // relation
    public enum Relation {
        BLOCK, REQUEST, REQUESTED, SUCCESS, REFUSE,
    }

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Relation relationship;

    public void setRelationship(Relation relationship) {
        this.relationship = relationship;
    }

    // builder
    @Builder
    public Friend(User user, User friend, Relation relationship) {
        this.user = user;
        this.friendName = friend.getName();
        this.userFriendId = friend.getUserId();
        this.relationship = relationship;
    }

}
