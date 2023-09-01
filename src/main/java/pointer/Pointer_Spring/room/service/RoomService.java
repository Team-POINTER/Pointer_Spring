package pointer.Pointer_Spring.room.service;

import javax.servlet.http.HttpServletRequest;

import pointer.Pointer_Spring.friend.dto.FriendDto;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.DetailResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteRequest;
import pointer.Pointer_Spring.room.dto.RoomMemberDto;
import pointer.Pointer_Spring.room.response.ResponseMemberRoom;
import pointer.Pointer_Spring.room.response.ResponseRoom;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.UserDto;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface RoomService {
    ResponseRoom getRoomList(UserPrincipal userPrincipal, String kwd, HttpServletRequest request);

    ResponseRoom getRoom(Long targetUserId, Long roomId);
//    DetailResponse getRooms(user user, HttpServletRequest request);//검색까지
//    void modifyRoomInfo(Long roomId);

    ResponseRoom createRoom(UserPrincipal userPrincipal, CreateRequest dto, HttpServletRequest request);
    ResponseMemberRoom updateRoomNm(UserPrincipal userPrincipal, RoomMemberDto.ModifyRoomNmRequest modifyRoomNmRequestDto);
    ResponseRoom exitRoom(Long roomId, UserPrincipal userPrincipal);

    //InviteResponse inviteMembers(InviteRequest dto, HttpServletRequest request);
    ResponseRoom getInviteMembers(Long roomId);
    ResponseRoom inviteMembers(InviteRequest dto);
    ResponseRoom isInviteMembersList(UserPrincipal userPrincipal, Long roomId, Integer currentPage, int pageSize, String kwd, HttpServletRequest request);

    ResponseRoom findLink(Long userId, Long roomId) throws NoSuchAlgorithmException;

    ResponseRoom getRealIDCode(UserPrincipal userPrincipal, String code) throws NoSuchAlgorithmException;

}
