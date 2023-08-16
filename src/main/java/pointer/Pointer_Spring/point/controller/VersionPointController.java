package pointer.Pointer_Spring.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.point.dto.VersionPointDto;
import pointer.Pointer_Spring.point.service.VersionPointService;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class VersionPointController {
    private final VersionPointService versionPointService;

    /*// 저장
    @PostMapping("/version")
    public ResponseEntity<Object> savePost(@RequestBody VersionPointDto.SaveVersionPointDto dto) {
        return new ResponseEntity<>(versionPointService.saveVersionPoint(dto), HttpStatus.OK);
    }*/

    // 조회
    @GetMapping
    public Object getVersionPoint() {
        return  new ResponseEntity<>(versionPointService.findVersionPoint(), HttpStatus.OK);
    }
}
