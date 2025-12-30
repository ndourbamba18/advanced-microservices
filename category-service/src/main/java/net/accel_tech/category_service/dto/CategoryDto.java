package net.accel_tech.category_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createdAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updatedAt;

}
