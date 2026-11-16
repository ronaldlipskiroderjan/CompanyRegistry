package br.com.Ronald.DesingPatternsProject.database.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationshipType {
    @JsonProperty("CLIENTE")
    CUSTOMER,

    @JsonProperty("FORNECEDOR")
    SUPPLIER,

    @JsonProperty("PARCEIRO")
    PARTNER,

    @JsonProperty("PROSPECT")
    PROSPECT
}
