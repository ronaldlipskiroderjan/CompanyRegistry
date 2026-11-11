package br.com.Ronald.DesingPatternsProject.dtos.Response;

import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CompanyResponseDTO(
        @JsonProperty("cnpj")
        String cnpj,

        @JsonProperty("razaoSocial")
        String corporateName,

        @JsonProperty("nomeFantasia")
        String tradeName,

        @JsonProperty("municipio")
        String city,

        @JsonProperty("uf")
        String state,

        @JsonProperty("situacaoCadastral")
        String registrationStatus,

        @JsonProperty("tipoRelacionamento")
        RelationshipType relationshipType
) {
}
