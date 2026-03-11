package com.example.gamelibrary.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @Schema(description = "Username", example = "jdoe")
    @NotBlank
    @Size(max = 50)
    private String username;

    @Schema(description = "Email", example = "jdoe@example.com")
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @Schema(description = "Game ids in user's library", example = "[1, 2, 3]")
    private Set<Long> libraryGameIds;

    @Schema(description = "Game ids in user's wishlist", example = "[4, 5]")
    private Set<Long> wishlistGameIds;
}
