package br.com.Ronald.DesingPatternsProject.database.model;

import br.com.Ronald.DesingPatternsProject.database.converter.RelationshipTypeConverter;
import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "razao_social", nullable = false)
    private String corporateName;

    @Column(name = "nome_fantasia", nullable = false)
    private String tradeName;

    @Column(name = "municipio", nullable = false)
    private String city;

    @Column(name = "uf", nullable = false)
    private String state;

    @Column(name = "situacao_cadastral", nullable = false)
    private String registrationStatus;

    @Convert(converter = RelationshipTypeConverter.class)
    @Column(name = "tipo_relacionamento", nullable = false)
    private RelationshipType relationshipType;
}
