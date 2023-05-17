package pointer.Pointer_Spring.room.controller;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomMemberDto;
import pointer.Pointer_Spring.room.response.ResponseRoom;
import pointer.Pointer_Spring.room.service.RoomService;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public RoomDto.ListResponse getRoomList(HttpServletRequest request) {
        return roomService.getRoomList(request);
    }

    @GetMapping("/{room-id}")
    public RoomDto.DetailResponse getRoom(@PathVariable("room-id") Long roomId,
        HttpServletRequest request) {
        return roomService.getRoom(roomId, request);
    }

    @PostMapping("create")
    public ResponseRoom createRoom(@RequestBody RoomDto.CreateRequest dto,
                                   HttpServletRequest request) {//원래 return RoomDto.CreateResponse
        return roomService.createRoom(dto, request);
    }

    @PostMapping("/verify/room-name")
    public Object updateRoomName(@RequestBody RoomMemberDto.ModifyRoomNmRequest dto, HttpServletRequest request) {
        System.out.println(dto.getPrivateRoomNm());
        return roomService.updateRoomNm(dto);
    }

    @PostMapping("/{room-id}/members")
    public RoomDto.InviteResponse inviteMembers(@RequestBody RoomDto.InviteRequest dto,
        HttpServletRequest request) {
        return roomService.inviteMembers(dto, request);
    }

    // 초대 링크 조회
//    @GetMapping("/{room-id}/invitation")
//    public ResponseEntity<Object> createInvitation(@PathVariable("room-id") Long roomId) {
//        return new ResponseEntity<>(roomService.findLink(roomId), HttpStatus.OK);
//    }

    // 링크를 통한 진입
//    @GetMapping("/invitation/{invitation}")
//    public ResponseEntity<Object> getRoom(@PathVariable String invitation) {
//        return new ResponseEntity<>(roomService.findRoom(invitation), HttpStatus.OK);
//    }
}
