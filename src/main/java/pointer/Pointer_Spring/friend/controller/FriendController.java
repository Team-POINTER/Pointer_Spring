package pointer.Pointer_Spring.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.service.FriendService;
import pointer.Pointer_Spring.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/search") // 유저 검색
    public UserDto.UserListResponse getUserList(@RequestBody FriendDto.FindFriendDto dto,
                                                HttpServletRequest request) {
        return friendService.getUserList(dto, request);
    }

    @GetMapping("/friend") // 친구 목록 조회
    public FriendDto.FriendInfoListResponse getFriendList(@RequestBody FriendDto.FriendUserDto dto,
                                                          HttpServletRequest request) {
        return friendService.getFriendList(dto, request);
    }

    // 친구 관계 설정

    @PostMapping("/friend/request") // 친구 요청
    public Object requestFriend(@RequestBody FriendDto.RequestFriendDto dto,
                                HttpServletRequest request) {
        return friendService.requestFriend(dto, request);
    }

    @PostMapping("/friend/accept") // 친구 수락
    public Object acceptFriend(@RequestBody FriendDto.RequestFriendDto dto,
                               HttpServletRequest request) {
        return friendService.acceptFriend(dto, request);
    }

    @PutMapping("/friend/request") // 친구 요청 취소
    public Object cancelRequestFriend(@RequestBody FriendDto.RequestFriendDto dto,
                                      HttpServletRequest request) {
        return friendService.cancelRequest(dto, request);
    }

    // 취소와 거절 설정

    @PostMapping("/friend/cancel") // 친구 취소 : 관계 끊어짐
    public Object cancelFriend(@RequestBody FriendDto.RequestFriendDto dto,
                               HttpServletRequest request) {
        return friendService.cancelFriend(dto, request);
    }

    @PostMapping("/friend/refuse") // 친구 거절 : 알림 삭제
    public Object refuseFriend(@RequestBody FriendDto.RequestFriendDto dto,
                               HttpServletRequest request) {
        return friendService.refuseFriend(dto, request);
    }

    // 차단 : 상대의 차단전 마지막 상태 유지

    @GetMapping("/friend/block") // 차단 친구 조회
    public FriendDto.FriendInfoListResponse getRefuseFriendList(@RequestBody FriendDto.FriendUserDto dto,
                                                                HttpServletRequest request) {
        return friendService.getBlockFriendList(dto, request);
    }

    @PostMapping("/friend/block") // 차단
    public Object getBlockFriendList(@RequestBody FriendDto.RequestFriendDto dto,
                                     HttpServletRequest request) {
        return friendService.blockFriend(dto, request);
    }


    // 친구 초대 링크 : 50명 이하인 총인원 경우만
}
