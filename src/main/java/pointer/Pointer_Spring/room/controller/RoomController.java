package pointer.Pointer_Spring.room.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.service.FriendService;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomMemberDto;
import pointer.Pointer_Spring.room.response.ResponseRoom;
import pointer.Pointer_Spring.room.service.RoomService;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;

import java.security.NoSuchAlgorithmException;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/room")
//@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    private final RoomService roomService;
    private final FriendService friendService;

    @GetMapping//jwt로 하면 get으로 바꾸고 requestbody 없애기
    public ResponseRoom getRoomList(@CurrentUser UserPrincipal userPrincipal, @RequestParam(required = false) String kwd, HttpServletRequest request) {
        return roomService.getRoomList(userPrincipal, kwd, request);
    }

    @GetMapping("/{room-id}")
    public ResponseRoom getRoom(@CurrentUser UserPrincipal userPrincipal, @PathVariable("room-id") Long roomId) {
        return roomService.getRoom(userPrincipal.getId(), roomId);
    }

    @PostMapping("create")
    public ResponseRoom createRoom(@CurrentUser UserPrincipal userPrincipal, @RequestBody RoomDto.CreateRequest dto,
                                   HttpServletRequest request) {//원래 return RoomDto.CreateResponse
        return roomService.createRoom(userPrincipal, dto, request);
    }

    @PatchMapping("/verify/room-name")
    @ApiOperation(value="룸 이름 수정", notes="개별적으로 룸 이름을 수정합니다.")
    public Object updateRoomName(@CurrentUser UserPrincipal userPrincipal, @RequestBody RoomMemberDto.ModifyRoomNmRequest dto, HttpServletRequest request) {
        return roomService.updateRoomNm(userPrincipal, dto);
    }

    @PostMapping("/invite/members")
    public ResponseRoom inviteMembers(@RequestBody RoomDto.InviteRequest dto) {
        return roomService.inviteMembers(dto);
    }

    @GetMapping("/paging/friends/invitation")
    public Object invitationFriendList(@CurrentUser UserPrincipal userPrincipal, Long roomId, Integer currentPage, int pageSize, String kwd, HttpServletRequest request) {
        return roomService.isInviteMembersList(userPrincipal, roomId, currentPage, pageSize,kwd, request); //여기 kwd는 Nickname에 있는가 없는가
    }

    @GetMapping("/get/{room-id}/members")
    public ResponseRoom getInviteMembers(@PathVariable("room-id") Long roomId){
        return roomService.getInviteMembers(roomId);
    }

    @GetMapping("/{room-id}/exit")
    public Object exitRoom(@PathVariable(name = "room-id") Long roomId, @CurrentUser UserPrincipal userPrincipal, HttpServletRequest request) {
        return roomService.exitRoom(roomId, userPrincipal);
    }

    // 초대 가능 친구 목록
    @GetMapping("/{room-id}/friendss")
    public Object getRoomFriendList(@PathVariable(name = "room-id") Long roomId,
                                    @CurrentUser UserPrincipal userPrincipal,
                                    @RequestParam String keyword,
                                    @RequestParam int lastPage) {
        return friendService.getRoomFriendList(roomId, userPrincipal, keyword, lastPage);
    }

     //초대 링크 조회
    @GetMapping("/{room-id}/invitation")
    public ResponseRoom createInvitationCode(@CurrentUser UserPrincipal userPrincipal, @PathVariable("room-id") Long roomId) throws NoSuchAlgorithmException {
        return roomService.findLink(userPrincipal.getId(), roomId);
    }
    @GetMapping("/invitation/{code}")
    public ResponseRoom invite(@CurrentUser UserPrincipal userPrincipal, @PathVariable("code") String code) throws NoSuchAlgorithmException {
        return roomService.getRealIDCode(userPrincipal, code);
    }

     //링크를 통한 진입
//    @GetMapping("/invitation/{invitation}")
//    public ResponseEntity<Object> getRoom(@PathVariable String invitation) {
//        return new ResponseEntity<>(roomService.findRoom(invitation), HttpStatus.OK);
//    }
}
