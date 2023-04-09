package pointer.Pointer_Spring.room.controller;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pointer.Pointer_Spring.room.dto.RoomDto;
import pointer.Pointer_Spring.room.service.RoomService;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public RoomDto.ListResponse getRoomList(HttpServletRequest request) {
        return roomService.getRoomList(request);
    }

    @GetMapping("/{room-id}")
    public RoomDto.DetailResponse getRoom(@PathVariable("room-id") Long roomId,
        HttpServletRequest request) {
        return roomService.getRoom(roomId, request);
    }

    @PostMapping
    public RoomDto.CreateResponse createRoom(@RequestBody RoomDto.CreateRequest dto,
        HttpServletRequest request) {
        return roomService.createRoom(dto, request);
    }

    @PostMapping("/{room-id}/members")
    public RoomDto.InviteResponse inviteMembers(@RequestBody RoomDto.InviteRequest dto,
        HttpServletRequest request) {
        return roomService.inviteMembers(dto, request);
    }

}
