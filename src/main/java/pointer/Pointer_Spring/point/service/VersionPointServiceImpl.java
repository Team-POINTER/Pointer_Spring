package pointer.Pointer_Spring.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.point.domain.VersionPoint;
import pointer.Pointer_Spring.point.dto.VersionPointDto;
import pointer.Pointer_Spring.point.repository.VersionPointerRepository;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VersionPointServiceImpl implements VersionPointService {

    private final VersionPointerRepository versionPointerRepository;
    private final UserRepository userRepository;

    private final Integer STATUS = 1;
    private final Integer POINT = 0;


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
            version.get().setPhrase(dto.getPhrase());
            version.get().setPoint(dto.getPoint());
            versionPointerRepository.save(version.get());
        } else {
            versionPointerRepository.save(versionPoint);
        }

        return new VersionPointDto.VersionPointResponse(ExceptionCode.SAVE_POINT_VERSION, versionPoint.getPoint(), , versionPoint.getPhrase());
    }*/

    @Override
    public Object findVersionPoint() {
        Optional<VersionPoint> version = versionPointerRepository.findVersionPointWithMaxVersion();
        if (version.isEmpty()) {
            return new VersionPointDto.VersionPointResponse(ExceptionCode.INVALID_POINT_VERSION);
        }

        return new VersionPointDto.VersionPointResponse(ExceptionCode.FIND_POINT_VERSION_OK, version.get().getPoint());
    }

    @Override
    public Object usePoint(UserPrincipal userPrincipal, int point) {
        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();
        if (POINT.compareTo((int) (user.getPoint() - point)) > 0) { // 차감시, 음수
            return new VersionPointDto.PointResponse(ExceptionCode.POINT_CALC_FAIL, Math.toIntExact(user.getPoint()));
        }
        user.setPoint(user.getPoint() - point);
        userRepository.save(user);

        return new VersionPointDto.PointResponse(ExceptionCode.POINT_CALC_OK, Math.toIntExact(user.getPoint()));
    }
}
