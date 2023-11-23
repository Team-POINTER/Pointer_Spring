package pointer.Pointer_Spring.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.service.FriendService;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;

@Controller
@ResponseBody
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class FriendController {

    private final FriendService friendService;

    // 조회 및 검색

    /*@PostMapping("/search") // 유저 검색
    public UserDto.UserListResponse getUserList(@CurrentUser UserPrincipal userPrincipal,
                                                @RequestBody FriendDto.FindFriendDto dto) {
        return friendService.getUserList(userPrincipal, dto);
    }*/

    @GetMapping("/user/search") // 유저 검색 : 관계 포함
    public UserDto.UserInfoListResponse getUserInfoList(@CurrentUser UserPrincipal userPrincipal,
                                                        @RequestParam String keyword,
                                                        @RequestParam int lastPage) {
        return friendService.getUserInfoList(userPrincipal, keyword, lastPage);
    }

    @GetMapping("/friends/search") // 친구 중 검색 : 기준 userId 추가
    public FriendDto.FriendInfoListResponse getUserFriendList(@CurrentUser UserPrincipal userPrincipal,
                                                              @RequestParam Long userId,
                                                              @RequestParam String keyword,
                                                              @RequestParam int lastPage) {
        return friendService.getUserFriendList(userPrincipal, userId, keyword, lastPage);
    }


    @GetMapping("/friends/block/search") // 차단친구 중 검색
    public FriendDto.FriendInfoListResponse getUserBlockFriendList(@CurrentUser UserPrincipal userPrincipal,
                                                                   @RequestParam String keyword,
                                                                   @RequestParam int lastPage) {
        return friendService.getUserBlockFriendList(userPrincipal, keyword, lastPage);
    }

    // 친구 관계 설정

    @PostMapping("/friends/request") // 친구 요청
    public Object requestFriend(@CurrentUser UserPrincipal userPrincipal,
                                @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.requestFriend(userPrincipal, dto);
    }

    @PostMapping("/friends/accept") // 친구 수락
    public Object acceptFriend(@CurrentUser UserPrincipal userPrincipal,
                               @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.acceptFriend(userPrincipal, dto);
    }

    // 취소와 거절 설정

    @PutMapping("/friends/request") // 친구 요청 취소
    public Object cancelRequestFriend(@CurrentUser UserPrincipal userPrincipal,
                                      @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.cancelRequest(userPrincipal, dto);
    }

    @PutMapping("/friends") // 친구 취소 : 관계 끊어짐
    public Object cancelFriend(@CurrentUser UserPrincipal userPrincipal,
                               @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.cancelFriend(userPrincipal, dto);
    }

    @PostMapping("/friends/refuse") // 친구 거절 : 알림 삭제
    public Object refuseFriend(@CurrentUser UserPrincipal userPrincipal,
                               @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.refuseFriend(userPrincipal, dto);
    }


    // 차단 : 상대의 차단전 마지막 상태 유지

    @PostMapping("/friends/block") // 차단
    public Object getBlockFriendList(@CurrentUser UserPrincipal userPrincipal,
                                     @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.blockFriend(userPrincipal, dto);
    }

    @PutMapping("/friends/block") // 차단 해제
    public Object cancelBlockFriendList(@CurrentUser UserPrincipal userPrincipal,
                                        @RequestBody FriendDto.RequestFriendDto dto) {
        return friendService.cancelBlockFriend(userPrincipal, dto);
    }

}
