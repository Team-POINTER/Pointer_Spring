package pointer.Pointer_Spring.vote.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import pointer.Pointer_Spring.common.response.BaseResponse;
import pointer.Pointer_Spring.security.CurrentUser;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.vote.dto.VoteDto;
import pointer.Pointer_Spring.vote.service.VoteServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/votes")
//@CrossOrigin(origins = "http://localhost:3000")
public class VoteController {

    private final VoteServiceImpl voteService;

    public VoteController(VoteServiceImpl voteService) {
        this.voteService = voteService;
    }

    // 투표하기
    @PostMapping()
    public BaseResponse<List<VoteDto.CreateResponse>> createVote(
            @CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody VoteDto.CreateRequest dto) {
        return new BaseResponse<>(voteService.createVote(userPrincipal, dto));
    }

    // 투표 여부 확인하기
    @GetMapping("/check//{questionId}")
    public BaseResponse<VoteDto.CheckResponse> isVote(
            @CurrentUser UserPrincipal userPrincipal, @PathVariable Long questionId) {
        return new BaseResponse<>(voteService.isVote(userPrincipal, questionId));
    }

    // 지목화면 결과 조회
    @GetMapping("/{questionId}")
    public BaseResponse<VoteDto.GetResponse> getVotes(
            @CurrentUser UserPrincipal userPrincipal, @PathVariable Long questionId) {
        return new BaseResponse<>(voteService.getQuestionVoteCnt(userPrincipal, questionId));
    }

    // 힌트보기
    @GetMapping("/hint/{questionId}")
    public BaseResponse<VoteDto.GetHintResponse> getHintResponse(
            @CurrentUser UserPrincipal userPrincipal, @PathVariable Long questionId) {
        return new BaseResponse<>(voteService.getHintResponse(userPrincipal, questionId));
    }

    // 지목하지 않은 사람 조회
    @GetMapping("/not-noted/{questionId}")
    public BaseResponse<List<VoteDto.GetNotVotedMember>> getNotVotedMember(@PathVariable Long questionId) {
        return new BaseResponse<>(voteService.getNotVotedMember(questionId));
    }

    @PostMapping("delete/hint")
    public BaseResponse<Void> deleteHint(@CurrentUser UserPrincipal userPrincipal, @RequestBody VoteDto.DeleteHintRequest deleteHintRequest) {
        return voteService.deleteHint(userPrincipal, deleteHintRequest);
    }

}
