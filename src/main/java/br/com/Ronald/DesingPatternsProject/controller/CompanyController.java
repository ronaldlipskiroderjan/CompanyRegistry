package br.com.Ronald.DesingPatternsProject.controller;

import br.com.Ronald.DesingPatternsProject.database.enums.RelationshipType;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyPatchRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Request.CompanyRequestDTO;
import br.com.Ronald.DesingPatternsProject.dtos.Response.CompanyResponseDTO;
import br.com.Ronald.DesingPatternsProject.service.impl.CompanyServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyServiceImpl companyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CompanyRequestDTO request) throws Exception {
        companyService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyResponseDTO> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyResponseDTO findByCnpj(@PathVariable("id") String cnpj) throws Exception {
        return companyService.findByCnpj(cnpj);
    }

    @GetMapping("/relationshipType/{relationshipType}")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyResponseDTO> findByRelationshipType(@PathVariable RelationshipType relationshipType) {
        return companyService.findByRelationshipType(relationshipType);
    }

    @PatchMapping("id/{id}/relationship")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateRelationshipType(@PathVariable("id") String cnpj, @Valid @RequestBody CompanyPatchRequestDTO request) throws Exception {
        companyService.updateRelationshipType(cnpj, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String cnpj) throws Exception {
        companyService.delete(cnpj);
    }
}
