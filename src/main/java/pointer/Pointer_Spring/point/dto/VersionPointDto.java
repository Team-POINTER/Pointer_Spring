package pointer.Pointer_Spring.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Getter
@Setter
@NoArgsConstructor
public class VersionPointDto {

    @Getter
    @Setter
    public static class SaveVersionPointDto {

        private String version;
        private int point;
        private String phrase;

    }


    @Getter
    public static class VersionPointResponse extends ResponseType {

        private int point;
        private String phrase;

        public VersionPointResponse(ExceptionCode exceptionCode) {
            super(exceptionCode);
        }

        public VersionPointResponse(ExceptionCode exceptionCode, int point, String phrase) {
            super(exceptionCode);
            this.point = point;
            this.phrase = phrase;
        }
    }

    @Getter
    public static class PointResponse extends ResponseType {

        private int point;

        public PointResponse(ExceptionCode exceptionCode, int point) {
            super(exceptionCode);
            this.point = point;
        }
    }
}
