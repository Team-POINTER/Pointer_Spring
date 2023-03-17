package pointer.Pointer_Spring.swagger;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//Controller
@RestController

public class SwaggerController {
    @GetMapping(value = "/hello")
    @ApiOperation(value = "hello, world api", notes = "hello world swagger check",  authorizations = {@Authorization(value = "JWT") })
    public String hellowWorld(){
        return "hello, world";
    }

    @ApiOperation(value = "test", notes = "테스트입니다")//요청 URL에 매핑된 API 에 대한 설명
    @GetMapping(value = "/board")
    public Map<String, String> selectBoard(@ApiParam(value = "샘플번호", required = true, example = "1")
                                           @RequestParam String no) {

        Map<String, String> result = new HashMap<>();
        result.put("test title", "테스트");
        result.put("test contents", "테스트 내용");
        return  result;
    }

    @ApiOperation(value="동작", notes="설명")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = SwaggerTestDto.class))),
            @ApiResponse(responseCode = "404", description = "500과 동일")
    })
    @PostMapping("/ex/")
    public SwaggerTestDto exampleMethod(@RequestBody SwaggerTestDto swaggerTestDto) {
        return new SwaggerTestDto(swaggerTestDto.getId(), swaggerTestDto.getName());
    }



}


