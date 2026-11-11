package br.com.Ronald.DesingPatternsProject.database.converter;

import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RelationshipTypeConverter implements AttributeConverter<RelationshipType, String> {

    @Override
    public String convertToDatabaseColumn(RelationshipType relationshipType) {
        return relationshipType == null ? null : relationshipType.databaseValue();
    }

    @Override
    public RelationshipType convertToEntityAttribute(String databaseValue) {
        return databaseValue == null ? null : RelationshipType.fromDatabaseValue(databaseValue);
    }
}
