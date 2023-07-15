package pointer.Pointer_Spring.vote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class VoteDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "questionId를 입력해주세요.")
        private Long questionId;
        @NotNull(message = "userId를 입력해주세요.")
        private Long userId;
        @Size(min = 1, message = "votedUserIds는 최소 1개 이상이어야 합니다.")
        private List<Long> votedUserIds;
        @NotNull(message = "hint를 입력해주세요.")
        private String hint;

        @Builder
        public CreateRequest(Long questionId, Long userId, List<Long> votedUserIds, String hint) {
            this.questionId = questionId;
            this.userId = userId;
            this.votedUserIds = votedUserIds;
            this.hint = hint;
        }

    }

    @Getter
    @NoArgsConstructor
    public static class CreateResponse {
        private Long id;
        private Long questionId;
        private Long userId;
        private Long votedUserId;
        private String hint;

        @Builder
        public CreateResponse(Long id, Long questionId, Long userId, Long votedUserId, String hint) {
            this.id = id;
            this.questionId = questionId;
            this.userId = userId;
            this.votedUserId = votedUserId;
            this.hint = hint;
        }

    }

    @Getter
    @NoArgsConstructor
    public static class GetResponse {
        private String roomName;
        private String question;
        private GetMemberResponse targetUser;
        private List<GetMemberResponse> members;
        private int notNotedMemberCnt;
        private int notReadChatCnt;

        @Builder
        public GetResponse(String roomName, String question, GetMemberResponse targetUser, List<GetMemberResponse> members, int notNotedMemberCnt, int notReadChatCnt) {
            this.roomName = roomName;
            this.question = question;
            this.targetUser = targetUser;
            this.members = members;
            this.notNotedMemberCnt = notNotedMemberCnt;
            this.notReadChatCnt = notReadChatCnt;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetMemberResponse {
        private Long userId;
        private String userName;
        private Integer allVoteCnt;
        private Integer votedMemberCnt;

        @Builder
        public GetMemberResponse(Long userId, String userName, Integer allVoteCnt, Integer votedMemberCnt) {
            this.userId = userId;
            this.userName = userName;
            this.allVoteCnt = allVoteCnt;
            this.votedMemberCnt = votedMemberCnt;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetNotVotedMember {
        private Long userId;
        private String userName;

        @Builder
        public GetNotVotedMember(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetHintResponse {
        private List<String> hint;
        private int allVoteCnt;
        private int targetVotedCnt;
        private String createdAt;

        @Builder
        public GetHintResponse(List<String> hint, int allVoteCnt, int targetVotedCnt, String createdAt) {
            this.hint = hint;
            this.allVoteCnt = allVoteCnt;
            this.targetVotedCnt = targetVotedCnt;
            this.createdAt = createdAt;
        }

    }
}
