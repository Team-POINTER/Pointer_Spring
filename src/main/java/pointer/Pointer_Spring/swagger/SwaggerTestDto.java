package pointer.Pointer_Spring.swagger;


import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "SwaggerTestDto", description = "swagger test dto")
public class SwaggerTestDto {
    @Schema(description = "id", defaultValue = "", allowableValues = {}, example = "1")
    private Long id;

    @Schema(description = "유저 이름", defaultValue = "", allowableValues = {}, example = "user1")
    private String name;
}