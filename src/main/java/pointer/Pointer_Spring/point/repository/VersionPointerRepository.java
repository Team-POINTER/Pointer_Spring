package pointer.Pointer_Spring.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.point.domain.VersionPoint;

import java.util.Optional;

@Repository
public interface VersionPointerRepository extends JpaRepository<VersionPoint, Long> {
    // 가장 최신 버전 가져옴
    @Query("SELECT e FROM VersionPoint e WHERE e.version = (SELECT MAX(e2.version) FROM VersionPoint e2)")
    Optional<VersionPoint> findVersionPointWithMaxVersion();

    Optional<VersionPoint> findByVersion(String version);

}

