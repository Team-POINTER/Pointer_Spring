package pointer.Pointer_Spring.question.service;

import pointer.Pointer_Spring.question.dto.QuestionDto;
import pointer.Pointer_Spring.security.UserPrincipal;

import javax.transaction.Transactional;
import java.util.List;

public interface QuestionService {
    QuestionDto.CreateResponse createQuestion(UserPrincipal userPrincipal, QuestionDto.CreateRequest request);

    QuestionDto.GetCurrentResponse getCurrentQuestion(UserPrincipal userPrincipal, Long roomId);

    List<QuestionDto.GetResponse> getQuestions(UserPrincipal userPrincipal, Long roomId);

    void modifyQuestion(UserPrincipal userPrincipal, Long questionId, QuestionDto.ModifyRequest request);

    void deleteQuestion(UserPrincipal userPrincipal, Long questionId);
}
