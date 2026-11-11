package br.com.Ronald.DesingPatternsProject.service.impl;

import br.com.Ronald.DesingPatternsProject.Exception.AlreadyExistsException;
import br.com.Ronald.DesingPatternsProject.Exception.NotFoundException;
import br.com.Ronald.DesingPatternsProject.database.model.CompanyEntity;
import br.com.Ronald.DesingPatternsProject.database.repository.CompanyRepository;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.BrazilApiCompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.service.BrazilApiClient;
import br.com.Ronald.DesingPatternsProject.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final BrazilApiClient brazilApiClient;

    @Override
    public List<CompanyResponseDTO> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public CompanyResponseDTO findByCnpj(String cnpj) throws Exception {
        return companyRepository.findById(cnpj)
                .map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Company not found!"));
    }

    @Override
    public void create(CompanyRequestDTO request) throws Exception {
        if (companyRepository.existsByCnpj(request.cnpj())) {
            throw new AlreadyExistsException("Company already registered in the system...");
        }

        BrazilApiCompanyResponseDTO companyData = brazilApiClient.findCompanyByCnpj(
                request.cnpj().replaceAll("[^0-9A-Za-z]", "")
        );

        companyRepository.save(CompanyEntity.builder()
                .cnpj(companyData.cnpj())
                .corporateName(companyData.corporateName())
                .tradeName(companyData.tradeName())
                .city(companyData.city())
                .state(companyData.state())
                .registrationStatus(companyData.registrationStatus())
                .relationshipType(request.relationshipType())
                .build()
        );
    }

    @Override
    public void updateRelationshipType(CompanyRequestDTO request) throws Exception {
        CompanyEntity company = companyRepository.findById(request.cnpj())
                .orElseThrow(() -> new NotFoundException("Company not found!"));
        company.setRelationshipType(request.relationshipType());
        companyRepository.save(company);
    }

    @Override
    public void delete(String cnpj) throws Exception {
        CompanyEntity company = companyRepository.findById(cnpj)
                .orElseThrow(() -> new NotFoundException("Company not found!"));
        companyRepository.delete(company);
    }

    private CompanyResponseDTO toDTO(CompanyEntity company) {
        return new CompanyResponseDTO(
                company.getCnpj(),
                company.getCorporateName(),
                company.getTradeName(),
                company.getCity(),
                company.getState(),
                company.getRegistrationStatus(),
                company.getRelationshipType()
        );
    }
}
