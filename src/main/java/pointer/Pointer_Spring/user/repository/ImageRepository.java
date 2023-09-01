package pointer.Pointer_Spring.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image save(Image image);
    Optional<Image> findByUserUserIdAndImageSortAndStatus(Long userUserId, Image.ImageType imageSort, int status);

    List<Image> findByUserUserIdAndStatus(Long userUserId, int status);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM image WHERE user_user_id = :userUserId", nativeQuery = true)
    void deleteAllByUserUserId(@Param("userUserId") Long userUserId);

    Optional<Image> findByUserAndImageSort(User requestUser, Image.ImageType imageType);
}
