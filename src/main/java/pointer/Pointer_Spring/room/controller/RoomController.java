package pointer.Pointer_Spring.room.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomMemberDto;
import pointer.Pointer_Spring.room.response.ResponseRoom;
import pointer.Pointer_Spring.room.service.RoomService;
import pointer.Pointer_Spring.swagger.SwaggerTestDto;

import java.util.List;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping//jwt로 하면 get으로 바꾸고 requestbody 없애기
    public ResponseRoom getRoomList(@RequestBody RoomDto.FindRoomRequest dto, @RequestParam(required = false) String kwd, HttpServletRequest request) {
        return roomService.getRoomList(dto, kwd, request);
    }

    @GetMapping("/{room-id}")
    public ResponseRoom getRoom(@PathVariable("room-id") Long roomId,
        HttpServletRequest request) {
        return roomService.getRoom(roomId, request);
    }

    @PostMapping("create")
    public ResponseRoom createRoom(@RequestBody RoomDto.CreateRequest dto,
                                   HttpServletRequest request) {//원래 return RoomDto.CreateResponse
        return roomService.createRoom(dto, request);
    }

    @PatchMapping("/verify/room-name")
    @ApiOperation(value="룸 이름 수정", notes="개별적으로 룸 이름을 수정합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = SwaggerTestDto.class))),
//            @ApiResponse(responseCode = "404", description = "500과 동일")
//    })
    public Object updateRoomName(@RequestBody RoomMemberDto.ModifyRoomNmRequest dto, HttpServletRequest request) {
        return roomService.updateRoomNm(dto);
    }

    @PostMapping("/invite/members")
    public ResponseRoom inviteMembers(@RequestBody RoomDto.InviteRequest dto, //원래 return 값 - RoomDto.InviteResponse
        HttpServletRequest request) {
        return roomService.inviteMembers(dto, request);
    }

    @GetMapping("/paging/friend/invitation")
    public Object invitationFriendList(@RequestParam Long userId, Long roomId, Integer currentPage, int pageSize, String kwd, HttpServletRequest request) {
        return roomService.isInviteMembersList(userId, roomId, currentPage, pageSize,kwd, request); //여기 kwd는 Nickname에 있는가 없는가
    }

    @GetMapping("/get/{room-id}/members")
    public ResponseRoom getInviteMembers(@PathVariable("room-id") Long roomId){
        return roomService.getInviteMembers(roomId);
    }

    @PostMapping("/{room-id}/exit")
    public Object exitRoom(@PathVariable(name = "room-id") Long roomId, @RequestBody RoomDto.ExitRequest dto, HttpServletRequest request) {
        return roomService.exitRoom(roomId, dto);
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
