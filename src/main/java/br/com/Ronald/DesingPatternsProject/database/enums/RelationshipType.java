package br.com.Ronald.DesingPatternsProject.database.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationshipType {
    @JsonProperty("CLIENTE")
    CUSTOMER("CLIENTE"),

    @JsonProperty("FORNECEDOR")
    SUPPLIER("FORNECEDOR"),

    @JsonProperty("PARCEIRO")
    PARTNER("PARCEIRO"),

    @JsonProperty("PROSPECT")
    PROSPECT("PROSPECT");

    private final String databaseValue;

    RelationshipType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String databaseValue() {
        return databaseValue;
    }

    public static RelationshipType fromDatabaseValue(String databaseValue) {
        for (RelationshipType relationshipType : values()) {
            if (relationshipType.databaseValue.equals(databaseValue)) {
                return relationshipType;
            }
        }
        throw new IllegalArgumentException("Unknown relationship type: " + databaseValue);
    }
}
