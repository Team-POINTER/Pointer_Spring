package pointer.Pointer_Spring.security;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class JwtUtilTests {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGenerate() {
        Map<String, Object> claimMap = Map.of("mEmail", "ABCDE");
        String jwtStr = jwtUtil.generateToken(claimMap, 1);
        System.out.println(jwtStr);
    }

    @Test
    public void testAll() {
        String jwtStr = jwtUtil.generateToken(Map.of("mEmail", "AAA", "email", "aaa@gmail.com"), 1);
        System.out.println(jwtStr);

        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);
        System.out.println("mEmail: " + claim.get("mEmail"));
        System.out.println("EMAIL: " + claim.get("email"));
        
    }
}