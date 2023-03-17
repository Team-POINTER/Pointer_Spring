package pointer.Pointer_Spring.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pointer.Pointer_Spring.swagger.SwaggerTestDto;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;


import java.util.Arrays;
import java.util.List;


@Configuration

public class SwaggerConfig {

    @Bean
    public Docket api(TypeResolver typeResolver) {

        return new Docket(DocumentationType.OAS_30)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .additionalModels(typeResolver.resolve(SwaggerTestDto.class))
                .useDefaultResponseMessages(false)
                .groupName("test1Api")//api 여러개일 때 지정
                .select()
                .apis(RequestHandlerSelectors.basePackage("pointer.Pointer_Spring.swagger"))//pagkage명과 동일
                .paths(PathSelectors.any())//전체 API에 대한 문서를 Swagger를 통해 나타낼 수 있다.
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Backend API")
                .description("Backend API 문서")
                .version("1.0")
                .build();
    }

    //ApiKey 정의
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    // 인증 토큰 방식이 있을때만 사용.
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    // 인증 토큰 방식이 있을때만 사용.
    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}