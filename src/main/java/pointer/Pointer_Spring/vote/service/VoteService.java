package pointer.Pointer_Spring.vote.service;

import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.vote.dto.VoteDto;

import java.util.List;

public interface VoteService {
    List<VoteDto.CreateResponse> createVote(UserPrincipal userPrincipal, VoteDto.CreateRequest dto);

    VoteDto.GetResponse getQuestionVoteCnt(UserPrincipal userPrincipal, Long roomId);

    List<VoteDto.GetNotVotedMember> getNotVotedMember(Long questionId);

    VoteDto.GetHintResponse getHintResponse(UserPrincipal userPrincipal, Long questionId);
}
