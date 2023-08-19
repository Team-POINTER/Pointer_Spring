package pointer.Pointer_Spring.friend.service;

import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.response.ResponseFriend;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;

public interface FriendService {

    //UserDto.UserListResponse getUserList(UserPrincipal userPrincipal, FriendDto.FindFriendDto dto);
    UserDto.UserInfoListResponse getUserInfoList(UserPrincipal userPrincipal, String keyword, int lastPage);
    FriendDto.FriendInfoListResponse getUserFriendList(UserPrincipal userPrincipal, Long targetId, String keyword, int lastPage);
    FriendDto.FriendInfoListResponse getUserBlockFriendList(UserPrincipal userPrincipal, String keyword, int lastPage);

    ResponseFriend requestFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend acceptFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend cancelRequest(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend refuseFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend cancelFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);

    ResponseFriend blockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);
    ResponseFriend cancelBlockFriend(UserPrincipal userPrincipal, FriendDto.RequestFriendDto dto);

    //FriendDto.FriendInfoListResponse getFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto);
    //FriendDto.FriendInfoListResponse getBlockFriendList(UserPrincipal userPrincipal, FriendDto.FriendUserDto dto);

    FriendDto.RoomFriendListResponse getRoomFriendList(Long roomId, UserPrincipal userPrincipal, String keyword, int lastPage);

}