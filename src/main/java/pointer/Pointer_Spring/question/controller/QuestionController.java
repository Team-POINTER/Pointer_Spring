package pointer.Pointer_Spring.question.controller;

import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.question.dto.QuestionDto;
import pointer.Pointer_Spring.question.service.QuestionServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "http://localhost:3000")
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
    public BaseResponse<QuestionDto.CreateResponse> createQuestion(@Valid @RequestBody QuestionDto.CreateRequest request) {
        return new BaseResponse<>(questionService.createQuestion(request));
    }

    // 첫 질문 조회
    @GetMapping("current/{userId}/{roomId}")
    public BaseResponse<QuestionDto.GetCurrentResponse> getFirstQuestion(
            @PathVariable Long userId, @PathVariable Long roomId) {
        return new BaseResponse<>(questionService.getCurrentQuestion(userId, roomId));
    }

    // 질문 전체 조회
    @GetMapping("{userId}/{roomId}")
    public BaseResponse<List<QuestionDto.GetResponse>> getQuestions(@PathVariable Long userId, @PathVariable Long roomId) {
        return new BaseResponse<>(questionService.getQuestions(userId, roomId));
    }


    // 질문 수정
    @PatchMapping("/{userId}/{questionId}")
    public BaseResponse<Void> modifyQuestion(
            @PathVariable Long userId, @PathVariable Long questionId, @RequestBody QuestionDto.ModifyRequest request) {
        questionService.modifyQuestion(userId, questionId, request);
        return new BaseResponse<>();
    }

    // 질문 삭제
    @DeleteMapping("/{userId}/{questionId}")
    public BaseResponse<Void> deleteQuestion(@PathVariable Long userId, @PathVariable Long questionId) {
        questionService.deleteQuestion(userId, questionId);
        return new BaseResponse<>();
    }

}
