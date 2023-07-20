package pointer.Pointer_Spring.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.user.domain.Image;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image save(Image image);
    Optional<Image> findByUserUserIdAndImageSortAndStatus(Long userUserId, Image.ImageType imageSort, int status);
    Optional<Image> findByUserUserIdAndImageSort(Long userId, Image.ImageType imageSort);

    List<Image> findByUserUserIdAndStatus(Long userUserId, int status);
}
