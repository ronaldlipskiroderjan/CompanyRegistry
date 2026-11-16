package br.com.Ronald.DesingPatternsProject.dtos.Request;

import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyRequestDTO(
        @NotBlank(message = "cnpj is required")
        String cnpj,

        @NotNull(message = "relationshipType is required")
        RelationshipType relationshipType
) {
}
