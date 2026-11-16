package br.com.Ronald.DesingPatternsProject.dtos.Request;

import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import jakarta.validation.constraints.NotNull;

public record CompanyPatchRequestDTO(
        @NotNull(message = "relationshipType is required")
        RelationshipType relationship
) {
}
