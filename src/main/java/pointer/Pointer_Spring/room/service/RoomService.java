package pointer.Pointer_Spring.room.service;

import javax.servlet.http.HttpServletRequest;

import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.DetailResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.ListResponse;
import pointer.Pointer_Spring.room.dto.RoomMemberDto;
import pointer.Pointer_Spring.room.response.ResponseMemberRoom;
import pointer.Pointer_Spring.room.response.ResponseRoom;

public interface RoomService {
    ListResponse getRoomList(HttpServletRequest request);

    DetailResponse getRoom(Long roomId, HttpServletRequest request);
//    DetailResponse getRooms(User user, HttpServletRequest request);//검색까지
//    void modifyRoomInfo(Long roomId);
//    void exitRoom(Long roomId);
//    void entryRoom(Long roomId);

    ResponseRoom createRoom(CreateRequest dto, HttpServletRequest request);
    ResponseMemberRoom updateRoomNm(RoomMemberDto.ModifyRoomNmRequest modifyRoomNmRequestDto);
    ResponseRoom exitRoom(RoomDto.ExitRequest dto);

    InviteResponse inviteMembers(InviteRequest dto, HttpServletRequest request);

    //Object findLink(Long roomId);
}
