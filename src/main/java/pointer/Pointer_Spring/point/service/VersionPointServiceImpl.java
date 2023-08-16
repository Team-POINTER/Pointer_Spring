package pointer.Pointer_Spring.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.point.domain.VersionPoint;
import pointer.Pointer_Spring.point.dto.VersionPointDto;
import pointer.Pointer_Spring.point.repository.VersionPointerRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VersionPointServiceImpl implements VersionPointService {

    private final VersionPointerRepository versionPointerRepository;

    /*@Override
    public Object saveVersionPoint(VersionPointDto.SaveVersionPointDto dto) {

        // 중복된 버전
        Optional<VersionPoint> version = versionPointerRepository.findByVersion(dto.getVersion());

        VersionPoint versionPoint = VersionPoint.builder()
                .version(dto.getVersion())
                .point(dto.getPoint())
                .phrase(dto.getPhrase())
                .build();

        if (version.isPresent()) {
            VersionPoint now = versionPoint;
            versionPointerRepository.save(now);
        } else {
            versionPointerRepository.save(versionPoint);
        }

        return new VersionPointResponse(ExceptionCode.SAVE_POINT_VERSION);
    }*/

    @Override
    public Object findVersionPoint() {
        Optional<VersionPoint> version = versionPointerRepository.findVersionPointWithMaxVersion();
        if (version.isEmpty()) {
            return new CustomException(ExceptionCode.INVALID_POINT_VERSION);
        }

        return new VersionPointDto.VersionPointResponse(ExceptionCode.FIND_POINT_VERSION_OK,
                version.get().getPoint(), version.get().getPhrase());
    }
}
