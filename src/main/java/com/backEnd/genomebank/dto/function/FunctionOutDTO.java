package com.backEnd.genomebank.dto.function;

import com.backEnd.genomebank.entities.Function.Category;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FunctionOutDTO {
    private Long id;
    private String code;
    private String name;
    private Category category;
    private String description;
    private LocalDateTime createdAt;
}