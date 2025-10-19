package com.backEnd.genomebank.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class GeneFunctionId implements Serializable {
    private Long geneId;
    private Long functionId;
}
