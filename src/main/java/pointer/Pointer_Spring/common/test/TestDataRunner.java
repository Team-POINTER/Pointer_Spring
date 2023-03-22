package pointer.Pointer_Spring.common.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.domain.User.Type;
import pointer.Pointer_Spring.User.repository.UserRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final RoomMemberRepository roomMemberRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user1 = User.builder().id("test1").email("test1@naver.com").nickname("혜인")
            .type(Type.KAKAO).build();
        User user2 = User.builder().id("test2").email("test2@naver.com").nickname("현정")
            .type(Type.APPLE).build();
        User user3 = User.builder().id("test3").email("test3@naver.com").nickname("초명")
            .type(Type.KAKAO).build();

        Room room1 = Room.builder().user(user3).name("닮은 동물 맞추기").build();
        Room room2 = Room.builder().user(user2).name("첫 인상 맞추기").build();

        User[] userArr = {user1, user2, user3};
        List<User> userList = new ArrayList<>(Arrays.asList(userArr));

        Room[] roomArr = {room1, room2};
        List<Room> roomList = new ArrayList<>(Arrays.asList(roomArr));

        userRepository.saveAll(userList);
        roomRepository.saveAll(roomList);

        RoomMember roomMember1 = new RoomMember(room1, user1);
        RoomMember roomMember2 = new RoomMember(room1, user2);
        RoomMember roomMember3 = new RoomMember(room1, user3);
        RoomMember roomMember4 = new RoomMember(room2, user2);

        RoomMember[] roomMemberArr = {roomMember1, roomMember2, roomMember3, roomMember4};
        List<RoomMember> roomMemberList = new ArrayList<>(Arrays.asList(roomMemberArr));

        roomMemberRepository.saveAll(roomMemberList);
    }
}
