package br.com.Ronald.DesingPatternsProject.controller;

import br.com.Ronald.DesingPatternsProject.Exception.AlreadyExistsException;
import br.com.Ronald.DesingPatternsProject.Exception.NotFoundException;
import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyPatchRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.handler.GlobalExceptionHandler;
import br.com.Ronald.DesingPatternsProject.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    private static final String CNPJ = "12345678000190";

    @Mock
    private CompanyServiceImpl companyService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new CompanyController(companyService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldListCompanies() throws Exception {
        when(companyService.findAll()).thenReturn(List.of(companyResponse()));

        mockMvc.perform(get("/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cnpj").value(CNPJ))
                .andExpect(jsonPath("$[0].razaoSocial").value("Empresa Exemplo S.A."))
                .andExpect(jsonPath("$[0].nomeFantasia").value("Empresa Exemplo"))
                .andExpect(jsonPath("$[0].municipio").value("Sao Paulo"))
                .andExpect(jsonPath("$[0].uf").value("SP"))
                .andExpect(jsonPath("$[0].situacaoCadastral").value("ATIVA"))
                .andExpect(jsonPath("$[0].tipoRelacionamento").value("CLIENTE"));

        verify(companyService).findAll();
    }

    @Test
    void shouldFindCompanyByCnpj() throws Exception {
        when(companyService.findByCnpj(CNPJ)).thenReturn(companyResponse());

        mockMvc.perform(get("/v1/companies/id/{id}", CNPJ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value(CNPJ))
                .andExpect(jsonPath("$.razaoSocial").value("Empresa Exemplo S.A."))
                .andExpect(jsonPath("$.nomeFantasia").value("Empresa Exemplo"))
                .andExpect(jsonPath("$.municipio").value("Sao Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.situacaoCadastral").value("ATIVA"))
                .andExpect(jsonPath("$.tipoRelacionamento").value("CLIENTE"));

        verify(companyService).findByCnpj(CNPJ);
    }

    @Test
    void shouldReturnNotFoundWhenCompanyDoesNotExist() throws Exception {
        when(companyService.findByCnpj(CNPJ))
                .thenThrow(new NotFoundException("Company not found!"));

        mockMvc.perform(get("/v1/companies/id/{id}", CNPJ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Company not found!"))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldListCompaniesByRelationshipType() throws Exception {
        when(companyService.findByRelationshipType(RelationshipType.CUSTOMER))
                .thenReturn(List.of(companyResponse()));

        mockMvc.perform(get(
                        "/v1/companies/relationshipType/{relationshipType}",
                        RelationshipType.CUSTOMER
                ))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].cnpj").value(CNPJ))
                .andExpect(jsonPath("$[0].razaoSocial").value("Empresa Exemplo S.A."))
                .andExpect(jsonPath("$[0].nomeFantasia").value("Empresa Exemplo"))
                .andExpect(jsonPath("$[0].municipio").value("Sao Paulo"))
                .andExpect(jsonPath("$[0].uf").value("SP"))
                .andExpect(jsonPath("$[0].situacaoCadastral").value("ATIVA"))
                .andExpect(jsonPath("$[0].tipoRelacionamento").value("CLIENTE"));

        verify(companyService).findByRelationshipType(RelationshipType.CUSTOMER);
    }

    @Test
    void shouldCreateCompany() throws Exception {
        mockMvc.perform(post("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson("CLIENTE")))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        verify(companyService).create(
                new CompanyRequestDTO(CNPJ, RelationshipType.CUSTOMER)
        );
    }

    @Test
    void shouldReturnBadRequestWhenRelationshipTypeIsMissing() throws Exception {
        mockMvc.perform(post("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cnpj": "%s"
                                }
                                """.formatted(CNPJ)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("relationshipType is required"))
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(companyService);
    }

    @Test
    void shouldReturnBadRequestWhenCnpjIsMissing() throws Exception {
        mockMvc.perform(post("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "relationshipType": "CLIENTE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("cnpj is required"))
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(companyService);
    }

    @Test
    void shouldReturnConflictWhenCompanyAlreadyExists() throws Exception {
        doThrow(new AlreadyExistsException("Company already registered in the system..."))
                .when(companyService).create(any(CompanyRequestDTO.class));

        mockMvc.perform(post("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson("CLIENTE")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Company already registered in the system..."))
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldUpdateRelationshipType() throws Exception {
        mockMvc.perform(patch("/v1/companies/id/{id}/relationship", CNPJ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequestJson("FORNECEDOR")))
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(companyService).updateRelationshipType(
                CNPJ,
                new CompanyPatchRequestDTO(RelationshipType.SUPPLIER)
        );
    }

    @Test
    void shouldReturnBadRequestWhenRelationshipTypeIsNullOnUpdate() throws Exception {
        mockMvc.perform(patch("/v1/companies/id/{id}/relationship", CNPJ)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "relationship": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("relationshipType is required"))
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(companyService);
    }

    @Test
    void shouldDeleteCompany() throws Exception {
        mockMvc.perform(delete("/v1/companies/{cnpj}", CNPJ))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(companyService).delete(CNPJ);
    }

    private CompanyResponseDTO companyResponse() {
        return new CompanyResponseDTO(
                CNPJ,
                "Empresa Exemplo S.A.",
                "Empresa Exemplo",
                "Sao Paulo",
                "SP",
                "ATIVA",
                RelationshipType.CUSTOMER
        );
    }

    private String createRequestJson(String relationshipType) {
        return """
                {
                  "cnpj": "%s",
                  "relationshipType": "%s"
                }
                """.formatted(CNPJ, relationshipType);
    }

    private String patchRequestJson(String relationship) {
        return """
                {
                  "relationship": "%s"
                }
                """.formatted(relationship);
    }
}
