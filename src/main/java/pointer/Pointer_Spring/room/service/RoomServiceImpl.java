package pointer.Pointer_Spring.room.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.repository.UserRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.DetailResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.ListResponse;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;

    public ListResponse getRoomList(HttpServletRequest request) {
        List<Room> roomList = roomRepository.findAll();
        List<RoomDto.ListRoom> roomListDto = roomList.stream().map(RoomDto.ListRoom::new).toList();

        return new ListResponse(roomListDto);
    }

    public DetailResponse getRoom(Long roomId, HttpServletRequest request) {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(
            () -> {
                throw new RuntimeException("방을 조회할 수 없습니다.");
            }
        );
        return new DetailResponse(foundRoom);
    }

    public CreateResponse createRoom(CreateRequest dto, HttpServletRequest request) {
        User foundUser = userRepository.findById(dto.getUserId()).orElseThrow(
            () -> {
                throw new RuntimeException("유저를 조회할 수 없습니다.");
            }
        );
        Room createdRoom = new Room(foundUser, dto.getRoomNm());
        roomRepository.save(createdRoom);

        Room savedRoom = roomRepository.findById(createdRoom.getRoomId()).orElseThrow(
            () -> {
                throw new RuntimeException("방 생성에 실패했습니다.");
            }
        );

        DetailResponse detailResponse = new DetailResponse(savedRoom);
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        return new CreateResponse(accessToken, refreshToken, detailResponse);
    }

    public InviteResponse inviteMembers(InviteRequest dto, HttpServletRequest request) {
        Room foundRoom = roomRepository.findById(dto.getRoomId()).orElseThrow(
            () -> {
                throw new RuntimeException("방 조회에 실패했습니다.");
            }
        );

        for (Long userId : dto.getUserIdArr()) {
            User foundUser = userRepository.findById(userId).orElseThrow(
                () -> {
                    throw new RuntimeException("유저를 조회할 수 없습니다.");
                }
            );
            RoomMember newRoomMember = new RoomMember(foundRoom, foundUser);
            roomMemberRepository.save(newRoomMember);
        }

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        List<RoomMember> roomMemberList = roomMemberRepository.findAllByRoom(foundRoom);
        List<RoomDto.InviteMember> invitedMemberList = roomMemberList.stream()
            .map(RoomDto.InviteMember::new).toList();
        return new InviteResponse(accessToken, refreshToken, invitedMemberList);
    }

}
