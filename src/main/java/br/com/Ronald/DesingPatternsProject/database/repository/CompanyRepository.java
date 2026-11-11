package br.com.Ronald.DesingPatternsProject.database.repository;

import br.com.Ronald.DesingPatternsProject.database.enums.RegistrationStatus;
import br.com.Ronald.DesingPatternsProject.database.model.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    List<CompanyEntity> findByRegistrationStatus(RegistrationStatus registrationStatus);

    boolean existsByCnpj(String cnpj);
}
