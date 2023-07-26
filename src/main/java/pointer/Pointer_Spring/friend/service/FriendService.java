package pointer.Pointer_Spring.friend.service;

import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.response.ResponseFriend;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;

public interface FriendService {

    UserDto.UserListResponse getUserList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto);
    FriendDto.FriendInfoListResponse getUserFriendList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto);
    FriendDto.FriendInfoListResponse getUserBlockFriendList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto);

    ResponseFriend requestFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend acceptFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend cancelRequest(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend refuseFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend cancelFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);

    ResponseFriend blockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend cancelBlockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);

    //FriendDto.FriendInfoListResponse getFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto);
    //FriendDto.FriendInfoListResponse getBlockFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto);

}