package pointer.Pointer_Spring.friend.service;

import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.friend.response.ResponseFriend;
import pointer.Pointer_Spring.room.dto.RoomDto;

import javax.servlet.http.HttpServletRequest;

public interface FriendService {

    FriendDto.FriendListResponse getUserList(FriendDto.FindFriendDto dto, HttpServletRequest request);
    FriendDto.FriendInfoListResponse getFriendList(FriendDto.FriendUserDto dto, HttpServletRequest request);
    ResponseFriend requestFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend refuseFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
    ResponseFriend cancelFriend(FriendDto.RequestFriendDto dto, HttpServletRequest request);
}
