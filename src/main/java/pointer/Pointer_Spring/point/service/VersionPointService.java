package pointer.Pointer_Spring.point.service;

import pointer.Pointer_Spring.point.dto.VersionPointDto;
import pointer.Pointer_Spring.security.UserPrincipal;

public interface VersionPointService {
    //Object saveVersionPoint(VersionPointDto.SaveVersionPointDto dto);
    Object findVersionPoint();
    Object usePoint(UserPrincipal userPrincipal, int point);
}
