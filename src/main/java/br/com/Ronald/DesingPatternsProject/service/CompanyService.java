package br.com.Ronald.DesingPatternsProject.service;

import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {

    List<CompanyResponseDTO> findAll();

    CompanyResponseDTO findByCnpj(String cnpj) throws Exception;

    void create(CompanyRequestDTO request) throws Exception;

    void updateRelationshipType(CompanyRequestDTO request) throws Exception;

    void delete(String cnpj) throws Exception;
}
