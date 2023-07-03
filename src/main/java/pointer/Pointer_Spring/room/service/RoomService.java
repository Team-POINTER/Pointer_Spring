package pointer.Pointer_Spring.room.service;

import javax.servlet.http.HttpServletRequest;

import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.DetailResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.ListResponse;
import pointer.Pointer_Spring.room.dto.RoomMemberDto;
import pointer.Pointer_Spring.room.response.ResponseMemberRoom;
import pointer.Pointer_Spring.room.response.ResponseRoom;

import java.util.List;

public interface RoomService {
    ListResponse getRoomList(RoomDto.FindRoomRequest dto, String kwd, HttpServletRequest request);

    DetailResponse getRoom(Long roomId, HttpServletRequest request);
//    DetailResponse getRooms(user user, HttpServletRequest request);//검색까지
//    void modifyRoomInfo(Long roomId);

    ResponseRoom createRoom(CreateRequest dto, HttpServletRequest request);
    ResponseMemberRoom updateRoomNm(RoomMemberDto.ModifyRoomNmRequest modifyRoomNmRequestDto);
    ResponseRoom exitRoom(Long roomId, RoomDto.ExitRequest dto);

    //InviteResponse inviteMembers(InviteRequest dto, HttpServletRequest request);
    List<RoomDto.RoomMemberResopnose> getInviteMembers(Long roomId);
    ResponseRoom inviteMembers(InviteRequest dto, HttpServletRequest request);
    List<RoomDto.IsInviteMember> isInviteMembersList(Long userId, Long roomId, Integer currentPage, int pageSize, String kwd, HttpServletRequest request);

    //Object findLink(Long roomId);
}
