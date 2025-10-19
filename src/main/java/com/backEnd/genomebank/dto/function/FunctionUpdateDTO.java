package com.backEnd.genomebank.dto.function;

import com.backEnd.genomebank.entities.Function.Category;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FunctionUpdateDTO {
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private Category category;

    private String description;
}