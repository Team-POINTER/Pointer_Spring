package pointer.Pointer_Spring.user.dto;

import lombok.Data;

public class ImageDto {
    @Data
    public static class ImageUrlResponse {
        String profileImageUrl;
        String backgroundImageUrl;

        public ImageUrlResponse(String profileImageUrl, String backgroundImageUrl){
            this.profileImageUrl = profileImageUrl;
            this.backgroundImageUrl = backgroundImageUrl;
        }

    }
}
