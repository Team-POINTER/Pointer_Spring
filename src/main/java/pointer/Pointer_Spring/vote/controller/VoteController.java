package pointer.Pointer_Spring.vote.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.vote.dto.VoteDto;
import pointer.Pointer_Spring.vote.service.VoteServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/votes")
@CrossOrigin(origins = "http://localhost:3000")
public class VoteController {

    private final VoteServiceImpl voteService;

    public VoteController(VoteServiceImpl voteService) {
        this.voteService = voteService;
    }

    // 투표하기
    @PostMapping()
    public BaseResponse<List<VoteDto.CreateResponse>> createVote(@Valid @RequestBody VoteDto.CreateRequest dto) {
        return new BaseResponse<>(voteService.createVote(dto));
    }

    // 지목화면 결과 조회
    @GetMapping("/{userId}/{questionId}")
    public BaseResponse<VoteDto.GetResponse> getVotes(@PathVariable Long userId, @PathVariable Long questionId) {
        return new BaseResponse<>(voteService.getQuestionVoteCnt(userId, questionId));
    }

    // 힌트보기
    @GetMapping("/hint/{userId}/{questionId}")
    public BaseResponse<VoteDto.GetHintResponse> getHintResponse(@PathVariable Long userId, @PathVariable Long questionId) {
        return new BaseResponse<>(voteService.getHintResponse(userId, questionId));
    }

    // 지목하지 않은 사람 조회
    @GetMapping("/not-noted/{questionId}")
    public BaseResponse<List<VoteDto.GetNotVotedMember>> getNotVotedMember(@PathVariable Long questionId) {
        return new BaseResponse<>(voteService.getNotVotedMember(questionId));
    }



}
