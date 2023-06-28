package pointer.Pointer_Spring.question.service;

import pointer.Pointer_Spring.question.dto.QuestionDto;

import javax.transaction.Transactional;
import java.util.List;

public interface QuestionService {
    QuestionDto.CreateResponse createQuestion(QuestionDto.CreateRequest request);

    QuestionDto.GetCurrentResponse getCurrentQuestion(Long userId, Long roomId);

    List<QuestionDto.GetResponse> getQuestions(Long userId, Long roomId);

    void modifyQuestion(Long userId, Long questionId, QuestionDto.ModifyRequest request);

    void deleteQuestion(Long userId, Long questionId);
}
