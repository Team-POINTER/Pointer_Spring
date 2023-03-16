package pointer.Pointer_Spring.swagger;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//Controller
@RestController
public class SwaggerController {
    @GetMapping(value = "/hello")
    @ApiOperation(value = "hello, world api", notes = "hello world swagger check")
    public String hellowWorld(){
        return "hello, world";
    }

    @ApiOperation(value = "test", notes = "테스트입니다")//요청 URL에 매핑된 API 에 대한 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "page not found!")
    })
    @GetMapping(value = "/board")
    public Map<String, String> selectBoard(@ApiParam(value = "샘플번호", required = true, example = "1")
                                           @RequestParam String no) {

        Map<String, String> result = new HashMap<>();
        result.put("test title", "테스트");
        result.put("test contents", "테스트 내용");
        return  result;
    }

    @Operation(summary="요약", description="설명")
    @ApiResponse(code = 200, message="ok")
    @PostMapping("/ex/")
    public SwaggerTestDto exampleMethod() {
        return new SwaggerTestDto();
    }

}


