package pointer.Pointer_Spring.common.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.domain.User.Type;
import pointer.Pointer_Spring.User.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    UserRepository userRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user1 = User.builder().id("test1").email("test1@naver.com").nickname("혜인").type(Type.KAKAO).build();
        User user2 = User.builder().id("test2").email("test2@naver.com").nickname("현정").type(Type.APPLE).build();
        User user3 = User.builder().id("test3").email("test3@naver.com").nickname("초명").type(Type.KAKAO).build();

        User[] userArr = {user1, user2, user3};
        List<User> userList = new ArrayList<>(Arrays.asList(userArr));

        userRepository.saveAll(userList);
    }
}
