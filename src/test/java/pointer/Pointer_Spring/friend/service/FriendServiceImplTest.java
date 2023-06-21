package pointer.Pointer_Spring.friend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;


@SpringBootTest
@Transactional
class FriendServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @AfterEach
    private void after() {
        em.clear();
    }

    void saveUsers() {
        User user1 = User.builder()
                .id("100")
                .email("email1")
                .name("name1")
                .type(User.SignupType.KAKAO)
                .build();

        User user2 = User.builder()
                .id("101")
                .email("email2")
                .name("name2")
                .type(User.SignupType.KAKAO)
                .build();

        User user3 = User.builder()
                .id("102")
                .email("email2")
                .name("name2")
                .type(User.SignupType.KAKAO)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @Test
    void getUserList() {
        saveUsers();
        assertEquals(userRepository.findAll().size(), 3);
    }

    @Test
    void getBlockFriendList() {

    }

    @Test
    void getFriendList() {
    }

    @Test
    void requestFriend() {
    }

    @Test
    void acceptFriend() {
    }

    @Test
    void refuseFriend() {
    }

    @Test
    void blockFriend() {
    }

    @Test
    void cancelRequest() {
    }

    @Test
    void cancelFriend() {
    }
}