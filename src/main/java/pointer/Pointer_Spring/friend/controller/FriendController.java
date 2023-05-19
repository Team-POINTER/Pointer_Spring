package pointer.Pointer_Spring.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.service.FriendService;

import javax.servlet.http.HttpServletRequest;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/search") // 유저 검색
    public FriendDto.FriendListResponse getUserList(@RequestBody FriendDto.FindFriendDto dto,
                                                    HttpServletRequest request) {
        return friendService.getUserList(dto, request);
    }

    @GetMapping("/friend") // 친구 목록 조회
    public FriendDto.FriendInfoListResponse getFriendList(@RequestBody FriendDto.FriendUserDto dto,
                                                        HttpServletRequest request) {
        return friendService.getFriendList(dto, request);
    }

    @PostMapping("/friend") // 요청 및 수락
    public Object requestFriend(@RequestBody FriendDto.RequestFriendDto dto,
                                HttpServletRequest request) {
        return friendService.requestFriend(dto, request);
    }

    @PutMapping("/friend")
    public Object cancelFriend(@RequestBody FriendDto.RequestFriendDto dto,
                               HttpServletRequest request) {
        return friendService.cancelFriend(dto, request);
    }

    @DeleteMapping("/friend")
    public Object refuseFriend(@RequestBody FriendDto.RequestFriendDto dto,
                                HttpServletRequest request) {
        return friendService.refuseFriend(dto, request);
    }


    // 친구 초대 링크 : 50명 이하인 총인원 경우만
}
