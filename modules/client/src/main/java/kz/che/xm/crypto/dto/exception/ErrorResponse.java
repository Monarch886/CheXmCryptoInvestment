package kz.che.xm.crypto.dto.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ErrorResponse(
        @Schema(description = "Error code")
        String error,
        @Schema(description = "Error message")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description) {
}
