package pointer.Pointer_Spring.swagger;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerTestDto {
    @ApiModelProperty(value="id", name = "id", example="1", dataType = "Long", required=true)
    private Long id;

    @ApiModelProperty(value="name", example="예시")
    private String name;
}