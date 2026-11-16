package br.com.Ronald.DesingPatternsProject.service;

import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyPatchRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {

    List<CompanyResponseDTO> findAll();

    CompanyResponseDTO findByCnpj(String cnpj) throws Exception;

    List<CompanyResponseDTO> findByRelationshipType(RelationshipType relationshipType);

    void create(CompanyRequestDTO request) throws Exception;

    void updateRelationshipType(String cnpj, CompanyPatchRequestDTO request) throws Exception;

    void delete(String cnpj) throws Exception;
}
