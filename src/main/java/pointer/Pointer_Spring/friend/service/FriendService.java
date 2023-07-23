package pointer.Pointer_Spring.friend.service;

import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.response.ResponseFriend;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;

public interface FriendService {

    UserDto.UserListResponse getUserList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto, HttpServletRequest request);
    FriendDto.FriendInfoListResponse getFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto, HttpServletRequest request);

    FriendDto.FriendInfoListResponse getBlockFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto, HttpServletRequest request);
    ResponseFriend requestFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend acceptFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend cancelRequest(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend refuseFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend cancelFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend blockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto, HttpServletRequest request);
}
