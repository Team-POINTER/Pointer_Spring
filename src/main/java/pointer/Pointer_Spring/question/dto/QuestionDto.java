package pointer.Pointer_Spring.question.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

import java.util.List;

public class QuestionDto extends BaseEntity {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private Long roomId;
        private Long userId;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class CreateResponse {
        private Long questionId;
        private String content;

        @Builder
        public CreateResponse(Long questionId, String content) {
            this.questionId = questionId;
            this.content = content;
        }

    }

    @Getter
    @NoArgsConstructor
    public static class GetCurrentResponse {
        private String roomName;
        private Long questionId;
        private boolean isVoted;
        private String content;
        private List<GetMemberResponse> members;

        @Builder
        public GetCurrentResponse(String roomName, Long questionId, boolean isVoted, String content, List<GetMemberResponse> members) {
            this.roomName = roomName;
            this.questionId = questionId;
            this.isVoted = isVoted;
            this.content = content;
            this.members = members;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetMemberResponse {
        private Long userId;
        private String nickname;

        @Builder
        public GetMemberResponse(Long userId, String nickname) {
            this.userId = userId;
            this.nickname = nickname;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GetResponse {
        private String roomName;
        private Long questionId;
        private String question;
        private Integer allVoteCnt;
        private Integer votedMemberCnt;
        private String createdAt;

        @Builder
        public GetResponse(String roomName, Long questionId, String question, Integer allVoteCnt, Integer votedMemberCnt, String createdAt) {
            this.roomName = roomName;
            this.questionId = questionId;
            this.question = question;
            this.allVoteCnt = allVoteCnt;
            this.votedMemberCnt = votedMemberCnt;
            this.createdAt = createdAt;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class ModifyRequest {
        private String content;

        @Builder
        public ModifyRequest(String content) {
            this.content = content;
        }
    }
}
