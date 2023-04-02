package pointer.Pointer_Spring.security;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class JwtFilterTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGenerate() {
        Map<String, Object> claimMap = Map.of("mEmail", "ABCDE");
        String jwtStr = jwtUtil.generateToken(claimMap, 1);
        System.out.println(jwtStr);
    }

}