package br.abner.rolim.spring.boot.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultOperations;

import javax.annotation.PostConstruct;

@Component
public class VaultWarmUpInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(VaultWarmUpInitializer.class.getName());

    private final VaultOperations vaultOperations;

    private final String vaultWarmUpPath;

    public VaultWarmUpInitializer(VaultOperations vaultOperations, @Value("${vault.app.write.path}") String vaultWarmUpPath) {
        this.vaultOperations = vaultOperations;
        this.vaultWarmUpPath = vaultWarmUpPath;
    }

    @PostConstruct
    public void init() {

        LOG.info("Executing vault warm up...");
        try {
            vaultOperations.write(vaultWarmUpPath, new WriteObject("WarmUp"));
            LOG.info("Vault warm up successful.");
        } catch (Exception e) {
            String msg = String.format("Error to write on vault path [%s]", vaultWarmUpPath);
            LOG.error(msg, e);
            throw new BeanInitializationException("Vault warm up failed. Please check the health of vault.", e);
        }
    }
}
