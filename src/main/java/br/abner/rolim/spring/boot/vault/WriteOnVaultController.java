package br.abner.rolim.spring.boot.vault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WriteOnVaultController {

    private final VaultOperations vaultOperations;
    private final String vaultWarmUpPath;


    public WriteOnVaultController(VaultOperations vaultOperations, @Value("${vault.app.write.path}") String vaultWarmUpPath) {
        this.vaultOperations = vaultOperations;
        this.vaultWarmUpPath = vaultWarmUpPath;
    }


    @GetMapping(value = "/write/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    public VaultResponse write(@PathVariable("word") String word) {
        return vaultOperations.write(vaultWarmUpPath, new WriteObject(word));
    }
}
