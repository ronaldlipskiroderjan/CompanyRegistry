package br.com.Ronald.DesingPatternsProject.service.impl;

import br.com.Ronald.DesingPatternsProject.Exception.AlreadyExistsException;
import br.com.Ronald.DesingPatternsProject.Exception.NotFoundException;
import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import br.com.Ronald.DesingPatternsProject.database.model.CompanyEntity;
import br.com.Ronald.DesingPatternsProject.database.repository.CompanyRepository;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.BrazilApiCompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.service.BrazilApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    private static final String CNPJ = "12345678000190";

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private BrazilApiClient brazilApiClient;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    void shouldListAllCompaniesAsResponseDtos() {
        CompanyEntity customer = company(CNPJ, RelationshipType.CUSTOMER);
        CompanyEntity supplier = company("98765432000110", RelationshipType.SUPPLIER);
        when(companyRepository.findAll()).thenReturn(List.of(customer, supplier));

        List<CompanyResponseDTO> result = companyService.findAll();

        assertThat(result).containsExactly(
                new CompanyResponseDTO(
                        CNPJ,
                        "Empresa Exemplo S.A.",
                        "Empresa Exemplo",
                        "Sao Paulo",
                        "SP",
                        "ATIVA",
                        RelationshipType.CUSTOMER
                ),
                new CompanyResponseDTO(
                        "98765432000110",
                        "Empresa Exemplo S.A.",
                        "Empresa Exemplo",
                        "Sao Paulo",
                        "SP",
                        "ATIVA",
                        RelationshipType.SUPPLIER
                )
        );
    }

    @Test
    void shouldFindCompanyByCnpj() throws Exception {
        when(companyRepository.findById(CNPJ))
                .thenReturn(Optional.of(company(CNPJ, RelationshipType.PARTNER)));

        CompanyResponseDTO result = companyService.findByCnpj(CNPJ);

        assertThat(result.cnpj()).isEqualTo(CNPJ);
        assertThat(result.relationshipType()).isEqualTo(RelationshipType.PARTNER);
    }

    @Test
    void shouldRejectFindWhenCompanyDoesNotExist() {
        when(companyRepository.findById(CNPJ)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findByCnpj(CNPJ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Company not found!");
    }

    @Test
    void shouldFetchExternalDataAndSaveCompany() throws Exception {
        String formattedCnpj = "12.345.678/0001-90";
        CompanyRequestDTO request = new CompanyRequestDTO(
                formattedCnpj,
                RelationshipType.CUSTOMER
        );
        when(companyRepository.existsByCnpj(formattedCnpj)).thenReturn(false);
        when(brazilApiClient.findCompanyByCnpj(CNPJ))
                .thenReturn(brazilApiResponse());

        companyService.create(request);

        ArgumentCaptor<CompanyEntity> companyCaptor = ArgumentCaptor.forClass(CompanyEntity.class);
        verify(companyRepository).save(companyCaptor.capture());

        CompanyEntity savedCompany = companyCaptor.getValue();
        assertThat(savedCompany.getCnpj()).isEqualTo(CNPJ);
        assertThat(savedCompany.getCorporateName()).isEqualTo("Empresa Exemplo S.A.");
        assertThat(savedCompany.getTradeName()).isEqualTo("Empresa Exemplo");
        assertThat(savedCompany.getCity()).isEqualTo("Sao Paulo");
        assertThat(savedCompany.getState()).isEqualTo("SP");
        assertThat(savedCompany.getRegistrationStatus()).isEqualTo("ATIVA");
        assertThat(savedCompany.getRelationshipType()).isEqualTo(RelationshipType.CUSTOMER);
        verify(brazilApiClient).findCompanyByCnpj(CNPJ);
    }

    @Test
    void shouldRejectInsertWhenCompanyAlreadyExists() {
        CompanyRequestDTO request = new CompanyRequestDTO(
                CNPJ,
                RelationshipType.CUSTOMER
        );
        when(companyRepository.existsByCnpj(CNPJ)).thenReturn(true);

        assertThatThrownBy(() -> companyService.create(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("Company already registered in the system...");

        verifyNoInteractions(brazilApiClient);
        verify(companyRepository, never()).save(any(CompanyEntity.class));
    }

    @Test
    void shouldUpdateRelationshipType() throws Exception {
        CompanyEntity company = company(CNPJ, RelationshipType.PROSPECT);
        when(companyRepository.findById(CNPJ)).thenReturn(Optional.of(company));
        CompanyRequestDTO request = new CompanyRequestDTO(
                CNPJ,
                RelationshipType.CUSTOMER
        );

        companyService.updateRelationshipType(request);

        assertThat(company.getRelationshipType()).isEqualTo(RelationshipType.CUSTOMER);
        verify(companyRepository).save(company);
    }

    @Test
    void shouldRejectUpdateWhenCompanyDoesNotExist() {
        when(companyRepository.findById(CNPJ)).thenReturn(Optional.empty());
        CompanyRequestDTO request = new CompanyRequestDTO(
                CNPJ,
                RelationshipType.CUSTOMER
        );

        assertThatThrownBy(() -> companyService.updateRelationshipType(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Company not found!");

        verify(companyRepository, never()).save(any(CompanyEntity.class));
    }

    @Test
    void shouldDeleteExistingCompany() throws Exception {
        CompanyEntity company = company(CNPJ, RelationshipType.CUSTOMER);
        when(companyRepository.findById(CNPJ)).thenReturn(Optional.of(company));

        companyService.delete(CNPJ);

        verify(companyRepository).delete(company);
    }

    @Test
    void shouldRejectDeleteWhenCompanyDoesNotExist() {
        when(companyRepository.findById(CNPJ)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.delete(CNPJ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Company not found!");

        verify(companyRepository, never()).delete(any(CompanyEntity.class));
    }

    private CompanyEntity company(String cnpj, RelationshipType relationshipType) {
        return CompanyEntity.builder()
                .cnpj(cnpj)
                .corporateName("Empresa Exemplo S.A.")
                .tradeName("Empresa Exemplo")
                .city("Sao Paulo")
                .state("SP")
                .registrationStatus("ATIVA")
                .relationshipType(relationshipType)
                .build();
    }

    private BrazilApiCompanyResponseDTO brazilApiResponse() {
        return new BrazilApiCompanyResponseDTO(
                CNPJ,
                "Empresa Exemplo S.A.",
                "Empresa Exemplo",
                "ATIVA",
                "01001000",
                "Rua",
                "Praca da Se",
                "100",
                null,
                "Se",
                "Sao Paulo",
                "SP",
                "1130000000",
                "contato@empresa.com"
        );
    }
}
