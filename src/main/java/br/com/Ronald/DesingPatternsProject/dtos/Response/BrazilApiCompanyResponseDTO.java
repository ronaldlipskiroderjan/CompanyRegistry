package br.com.Ronald.DesingPatternsProject.dtos.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BrazilApiCompanyResponseDTO(

        @JsonProperty("cnpj")
        String cnpj,

        @JsonProperty("razao_social")
        String corporateName,

        @JsonProperty("nome_fantasia")
        String tradeName,

        @JsonProperty("descricao_situacao_cadastral")
        String registrationStatus,

        @JsonProperty("cep")
        String postalCode,

        @JsonProperty("descricao_tipo_logradouro")
        String streetType,

        @JsonProperty("logradouro")
        String street,

        @JsonProperty("numero")
        String number,

        @JsonProperty("complemento")
        String addressDetails,

        @JsonProperty("bairro")
        String neighborhood,

        @JsonProperty("municipio")
        String city,

        @JsonProperty("uf")
        String state,

        @JsonProperty("ddd_telefone_1")
        String phone,

        String email
) {
}
