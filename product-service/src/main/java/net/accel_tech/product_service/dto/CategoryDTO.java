package net.accel_tech.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;

    public CategoryDTO(long l, String s) {
    }
}
