package br.com.Ronald.DesingPatternsProject.controller;

import br.com.Ronald.DesingPatternsProject.Exception.AlreadyExistsException;
import br.com.Ronald.DesingPatternsProject.Exception.NotFoundException;
import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.handler.GlobalExceptionHandler;
import br.com.Ronald.DesingPatternsProject.service.CompanyService;
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
    private CompanyService companyService;

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
                .andExpect(jsonPath("$[0].tipoRelacionamento").value("CLIENTE"));
    }

    @Test
    void shouldFindCompanyByCnpj() throws Exception {
        when(companyService.findByCnpj(CNPJ)).thenReturn(companyResponse());

        mockMvc.perform(get("/v1/companies/{cnpj}", CNPJ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnpj").value(CNPJ))
                .andExpect(jsonPath("$.municipio").value("Sao Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    void shouldReturnNotFoundWhenCompanyDoesNotExist() throws Exception {
        when(companyService.findByCnpj(CNPJ))
                .thenThrow(new NotFoundException("Company not found!"));

        mockMvc.perform(get("/v1/companies/{cnpj}", CNPJ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Company not found!"))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldCreateCompany() throws Exception {
        mockMvc.perform(post("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("CLIENTE")))
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
                .andExpect(jsonPath("$.message").value("tipoRelacionamento is required"))
                .andExpect(jsonPath("$.code").value(400));

        verifyNoInteractions(companyService);
    }

    @Test
    void shouldReturnConflictWhenCompanyAlreadyExists() throws Exception {
        doThrow(new AlreadyExistsException("Company already registered in the system..."))
                .when(companyService).create(any(CompanyRequestDTO.class));

        mockMvc.perform(post("/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("CLIENTE")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Company already registered in the system..."))
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldUpdateRelationshipType() throws Exception {
        mockMvc.perform(patch("/v1/companies/relationship-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("FORNECEDOR")))
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(companyService).updateRelationshipType(
                new CompanyRequestDTO(CNPJ, RelationshipType.SUPPLIER)
        );
    }

    @Test
    void shouldReturnBadRequestWhenRelationshipTypeIsNullOnUpdate() throws Exception {
        mockMvc.perform(patch("/v1/companies/relationship-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cnpj": "%s",
                                  "tipoRelacionamento": null
                                }
                                """.formatted(CNPJ)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("tipoRelacionamento is required"))
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

    private String requestJson(String relationshipType) {
        return """
                {
                  "cnpj": "%s",
                  "tipoRelacionamento": "%s"
                }
                """.formatted(CNPJ, relationshipType);
    }
}
