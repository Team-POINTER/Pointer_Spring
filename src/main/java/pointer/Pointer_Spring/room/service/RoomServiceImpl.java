package pointer.Pointer_Spring.room.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.repository.UserRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.DetailResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.*;
import pointer.Pointer_Spring.room.dto.RoomMemberDto.*;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.room.response.ResponseInvitation;
import pointer.Pointer_Spring.room.response.ResponseMemberRoom;
import pointer.Pointer_Spring.room.response.ResponseNoRoom;
import pointer.Pointer_Spring.room.response.ResponseRoom;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;


@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    //private final PasswordEncoder passwordEncoder;

    public ListResponse getRoomList(HttpServletRequest request) {//검색 추가, 정렬
        List<Room> roomList = roomRepository.findAll();
        List<RoomDto.ListRoom> roomListDto = roomList.stream().map(RoomDto.ListRoom::new).toList();

        return new ListResponse(roomListDto);
    }

    public DetailResponse getRoom(Long roomId, HttpServletRequest request) {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(
            () -> {
                throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
            }
        );
        return new DetailResponse(foundRoom);
    }

    //초대 로직, 채팅방도 추가
    //validation 정해지면 (최소 1자, 최대 15자, 룸 이름 중복 가능), 방 제한
    public ResponseRoom createRoom(CreateRequest createRoomDto, HttpServletRequest request) {
        User foundUser = userRepository.findById(createRoomDto.getUserId()).orElseThrow(//이후 토큰으로 변경 필요
            () -> {
                throw new CustomException(ExceptionCode.USER_NOT_FOUND);
            }
        );
        Integer roomLimit = foundUser.getRoomLimit();
        if(roomLimit > 30){//방 생성 가능 횟수 제한
            return new ResponseRoom(ExceptionCode.ROOM_CREATE_OVER_LIMIT);
        }

        Room createdRoom = new Room(foundUser.getUserId(), createRoomDto.getRoomNm());
        roomRepository.saveAndFlush(createdRoom);
        Room savedRoom = roomRepository.findById(createdRoom.getRoomId()).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.ROOM_CREATE_FAIL);
                }
        );
        RoomMember roomMember = new RoomMember(savedRoom, foundUser);
        roomMemberRepository.save(roomMember);

        foundUser.updateRoomLimit(roomLimit + 1); //user의 roomlimit + 1

        //createdRoom.setInvitation(createLink(createdRoom));

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";//?

        //createResponseDto 추가해야 하는지와 DetailResponse 이 필요할지 고민
        return new ResponseRoom(ExceptionCode.ROOM_CREATE_SUCCESS, savedRoom);
    }

    @Override
    public ResponseMemberRoom updateRoomNm(ModifyRoomNmRequest modifyRoomNmRequestDto){//validation정해지면 (최소 1자, 최대 15자, 룸 이름 중복 가능)
        RoomMember roomMember = roomMemberRepository
                .findByRoom_RoomIdAndUser_UserIdAndStatus(modifyRoomNmRequestDto.getRoomId(), modifyRoomNmRequestDto.getUserId(), 1)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROOMMEMBER_NOT_EXIST));

        roomMember.updateRoomMember(modifyRoomNmRequestDto.getPrivateRoomNm());
        return new ResponseMemberRoom(ExceptionCode.ROOMNAME_VERIFY_OK);
    }

    @Override
    public ResponseRoom exitRoom(ExitRequest exitRequestDto){
        User foundUser = userRepository.findById(exitRequestDto.getId()).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_FOUND));
        RoomMember roomMember = roomMemberRepository.findByRoom_RoomIdAndUser_UserIdAndStatus(exitRequestDto.getRoomId(), foundUser.getUserId(), 1).orElseThrow(()->new CustomException(ExceptionCode.ROOM_NOT_FOUND));
        roomMember.setStatus(0);
        return new ResponseRoom(ExceptionCode.ROOM_EXIT_SUCCESS);
    }

    public InviteResponse inviteMembers(InviteRequest dto, HttpServletRequest request) {
        Room foundRoom = roomRepository.findById(dto.getRoomId()).orElseThrow(
                () -> {
                    throw new RuntimeException("방 조회에 실패했습니다.");
                }
        );

//        if (createRoomDto.getFriendsIdList().size()>50) {//초대 가능 인원 수 제한
//            return new ResponseRoom(ExceptionCode.ROOM_CREATE_OVER_LIMIT);
//        }

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

//        //RoomMember저장
//        List<RoomMember> roomMembers = createRoomDto.getFriendsIdList().stream()
//                .map((friendId) -> new RoomMember(createdRoom, userRepository.findById(friendId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND)) ))
//                .collect(Collectors.toList());
//        roomMemberRepository.saveAll(roomMembers);
//
//        //RoomMember 다 저장됐는지 확인 로직 추후 고려 고민
//        List<RoomMember> savedRoomMembers = createRoomDto.getFriendsIdList().stream()
//                .map((friendId) -> roomMemberRepository.findByUserId(friendId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND)) )
//                .collect(Collectors.toList());

        List<RoomMember> roomMemberList = roomMemberRepository.findAllByRoom(foundRoom);
        List<RoomDto.InviteMember> invitedMemberList = roomMemberList.stream()
                .map(RoomDto.InviteMember::new).toList();
        return new InviteResponse(accessToken, refreshToken, invitedMemberList);
    }

//    public String createLink(Room room) {
//        return room.getCode();
//                //room.getRoomId() + passwordEncoder.encode(room.getName()); // 임시로 암호화
//    }

//    @Override
//    public Object findLink(Long roomId) {
//        Optional<Room> room = roomRepository.findById(roomId);
//        if (room.isEmpty()) {
//            return new ResponseNoRoom(ExceptionCode.INVITATION_NOT_FOUND);
//        }
//        return new ResponseNoRoom(ExceptionCode.INVITATION_NOT_FOUND);//new ResponseInvitation(ExceptionCode.INVITATION_GET_OK, room.get().getInvitation());
//    }

}
