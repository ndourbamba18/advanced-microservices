package net.accel_tech.product_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private Double price;
    @JsonProperty("image")
    private String image;
    @JsonProperty("description")
    private String description;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("categoryId")
    private Long categoryId;
    @JsonProperty("createdAt")
    private Date createdAt;
    @JsonProperty("updatedAt")
    private Date updatedAt;
}
