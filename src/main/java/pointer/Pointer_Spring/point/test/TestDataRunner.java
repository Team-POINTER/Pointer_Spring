package pointer.Pointer_Spring.point.test;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pointer.Pointer_Spring.point.domain.VersionPoint;
import pointer.Pointer_Spring.point.repository.VersionPointerRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final VersionPointerRepository versionPointerRepository;

    @Override
    public void run(ApplicationArguments args) {

        Optional<VersionPoint> version = versionPointerRepository.findByVersion("0.0.0");
        System.out.println("version = " + version);

        if (version.isEmpty()) {
            VersionPoint versionPoint = VersionPoint.builder()
                    .version("0.0.0")
                    .point(0)
                    .phrase("무료 버전 입니다!")
                    .build();
            versionPointerRepository.save(versionPoint);
        }
    }
}
