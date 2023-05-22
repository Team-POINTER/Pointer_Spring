package pointer.Pointer_Spring.friend.service;

import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.response.ResponseFriend;

import javax.servlet.http.HttpServletRequest;

public interface FriendService {

    FriendDto.FriendListResponse getUserList(FriendDto.FindFriendDto dto, HttpServletRequest request);
    FriendDto.FriendInfoListResponse getFriendList(FriendDto.FriendUserDto dto, HttpServletRequest request);
    FriendDto.FriendInfoListResponse getBlockFriendList(FriendDto.FriendUserDto dto, HttpServletRequest request);
    ResponseFriend requestFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend acceptFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend cancelRequest(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend refuseFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend cancelFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend blockFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
}
