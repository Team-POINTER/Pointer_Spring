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
import pointer.Pointer_Spring.room.dto.RoomDto.*;
import pointer.Pointer_Spring.room.dto.RoomMemberDto.*;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.room.response.ResponseMemberRoom;
import pointer.Pointer_Spring.room.response.ResponseRoom;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;


@Service
@Transactional
@RequiredArgsConstructor//룸 입장 = 조회<질문 투표수 등등까지> / 룸 전체 조회(정렬), 초대 / RoomResponse 결과 고치기(updateRoomNm) / 이후 링크로 초대<웹 초대> /
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

    public DetailResponse getRoom(Long roomId, HttpServletRequest request) {//질문, 투표 등까지 같이 가져오기
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(
            () -> {
                throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
            }
        );
        return new DetailResponse(foundRoom);
    }

    //초대 로직, 채팅방도 추가
    //validation 정해지면 ( 룸 이름 중복 가능)
    public ResponseRoom createRoom(CreateRequest createRoomDto, HttpServletRequest request) {
        isValidRoomNmLength(createRoomDto.getRoomNm());

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
    public ResponseMemberRoom updateRoomNm(ModifyRoomNmRequest modifyRoomNmRequestDto){//validation정해지면 ( 룸 이름 중복 가능)
        RoomMember roomMember = roomMemberRepository
                .findByRoom_RoomIdAndUser_UserIdAndStatus(modifyRoomNmRequestDto.getRoomId(), modifyRoomNmRequestDto.getUserId(), 1)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROOMMEMBER_NOT_EXIST));

        String privateRoomNm = modifyRoomNmRequestDto.getPrivateRoomNm();
        String roomNm = privateRoomNm == null || privateRoomNm.isEmpty() || isValidRoomNmLength(privateRoomNm)?
                roomRepository.findById(modifyRoomNmRequestDto.getRoomId()).get().getName() : privateRoomNm;

        roomMember.updatePrivateRoomNm(roomNm);
        return new ResponseMemberRoom(ExceptionCode.ROOMNAME_VERIFY_OK);
    }

    private boolean isValidRoomNmLength(String roomNm) {
        if (roomNm.length() <= 15 && roomNm.length() >= 1) {
            return true;
        } else {
            throw new CustomException(ExceptionCode.ROOM_NAME_INVALID);
        }
    }



    @Override
    public ResponseRoom exitRoom(Long roomId, ExitRequest exitRequestDto){
        User foundUser = userRepository.findById(exitRequestDto.getId()).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_FOUND));
        RoomMember roomMember = roomMemberRepository.findByRoom_RoomIdAndUser_UserIdAndStatus(roomId, foundUser.getUserId(), 1).orElseThrow(()->new CustomException(ExceptionCode.ROOMMEMBER_NOT_EXIST));

        Room room = roomRepository.findById(roomId).orElseThrow(()->new CustomException(ExceptionCode.ROOM_NOT_FOUND));
        room.updateMemberNum(room.getMemberNum()-1);

        roomMember.setStatus(0);
        return new ResponseRoom(ExceptionCode.ROOM_EXIT_SUCCESS);
    }

    public ResponseRoom inviteMembers(InviteRequest inviteDto, HttpServletRequest request) {
        Room foundRoom = roomRepository.findById(inviteDto.getRoomId()).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
                }
        );

        Integer totalRoomMemberNum = foundRoom.getMemberNum() + inviteDto.getFriendIdList().size();
        if (totalRoomMemberNum>50) {//초대 가능 인원 수 제한
            throw new CustomException(ExceptionCode.ROOM_CREATE_OVER_LIMIT);
        }
        foundRoom.updateMemberNum(totalRoomMemberNum);


        //RoomMember저장
        List<RoomMember> roomMembers = inviteDto.getFriendIdList().stream()
                .map((friendId) ->
                        new RoomMember(foundRoom, userRepository.findById(friendId).orElseThrow(
                                () -> new CustomException(ExceptionCode.USER_NOT_FOUND)) )
                ).collect(Collectors.toList());
        roomMemberRepository.saveAllAndFlush(roomMembers);
        List<RoomMember> roomMemberList = roomMemberRepository.findAllByRoom(foundRoom);
//        List<RoomDto.InviteMember> invitedMemberList = roomMemberList.stream()
//                .map(RoomDto.InviteMember::new).toList();

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        //return new InviteResponse(accessToken, refreshToken, invitedMemberList);
        return new ResponseRoom(ExceptionCode.ROOM_NAME_INVITATION,foundRoom,roomMemberList);
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
