package pointer.Pointer_Spring.vote.service;

import pointer.Pointer_Spring.vote.dto.VoteDto;

import java.util.List;

public interface VoteService {
    List<VoteDto.CreateResponse> createVote(VoteDto.CreateRequest dto);

    VoteDto.GetResponse getQuestionVoteCnt(Long userId, Long roomId);

    List<VoteDto.GetNotVotedMember> getNotVotedMember(Long questionId);

    VoteDto.GetHintResponse getHintResponse(Long userId, Long questionId);
}
