package pointer.Pointer_Spring.question.controller;

import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.question.dto.QuestionDto;
import pointer.Pointer_Spring.question.service.QuestionServiceImpl;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/questions")
//@CrossOrigin(origins = "http://localhost:3000")
public class QuestionController {

    private final QuestionServiceImpl questionService;

    public QuestionController(QuestionServiceImpl questionService) {
        this.questionService = questionService;
    }

    /**
     * 질문 생성
     * @param request
     * @return
     */
    @PostMapping
    public BaseResponse<QuestionDto.CreateResponse> createQuestion(@CurrentUser UserPrincipal userPrincipal,  @Valid @RequestBody QuestionDto.CreateRequest request) {
        return new BaseResponse<>(questionService.createQuestion(userPrincipal, request));
    }

    // 첫 질문 조회
    @GetMapping("current/{roomId}")
    public BaseResponse<QuestionDto.GetCurrentResponse> getFirstQuestion(
            @CurrentUser UserPrincipal userPrincipal, @PathVariable Long roomId) {
        return new BaseResponse<>(questionService.getCurrentQuestion(userPrincipal, roomId));
    }

    // 질문 전체 조회
    @GetMapping("/{roomId}")
    public BaseResponse<List<QuestionDto.GetResponse>> getQuestions(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long roomId) {
        return new BaseResponse<>(questionService.getQuestions(userPrincipal, roomId));
    }


    // 질문 수정
    @PatchMapping("/{questionId}")
    public BaseResponse<Void> modifyQuestion(
            @CurrentUser UserPrincipal userPrincipal, @PathVariable Long questionId, @RequestBody QuestionDto.ModifyRequest request) {
        questionService.modifyQuestion(userPrincipal, questionId, request);
        return new BaseResponse<>();
    }

    // 질문 삭제
    @DeleteMapping("/{questionId}")
    public BaseResponse<Void> deleteQuestion(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long questionId) {
        questionService.deleteQuestion(userPrincipal, questionId);
        return new BaseResponse<>();
    }

}
