package br.com.Ronald.DesingPatternsProject.service;

import br.com.Ronald.DesingPatternsProject.dtos.Response.BrazilApiCompanyResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "brazilApi",
        url = "https://brasilapi.com.br/api"
)
public interface BrazilApiClient {

    @GetMapping("/cnpj/v1/{cnpj}")
    BrazilApiCompanyResponseDTO findCompanyByCnpj(
            @PathVariable("cnpj") String cnpj
    );
}
