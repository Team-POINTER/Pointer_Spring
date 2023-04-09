package pointer.Pointer_Spring.room.service;

import javax.servlet.http.HttpServletRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.CreateResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.DetailResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteRequest;
import pointer.Pointer_Spring.room.dto.RoomDto.InviteResponse;
import pointer.Pointer_Spring.room.dto.RoomDto.ListResponse;

public interface RoomService {

    ListResponse getRoomList(HttpServletRequest request);

    DetailResponse getRoom(Long roomId, HttpServletRequest request);

    CreateResponse createRoom(CreateRequest dto, HttpServletRequest request);

    InviteResponse inviteMembers(InviteRequest dto, HttpServletRequest request);
}
