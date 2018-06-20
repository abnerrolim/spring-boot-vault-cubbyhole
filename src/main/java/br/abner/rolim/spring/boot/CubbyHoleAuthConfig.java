package br.abner.rolim.spring.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthenticationOptions;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.support.VaultToken;

import java.net.URI;

@Configuration
public class CubbyHoleAuthConfig extends AbstractVaultConfiguration {

    @Value("${vault.uri}")
    private String vaultUri;

    @Value("${vault.token}")
    private String vaultToken;


    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.from(URI.create(vaultUri));
    }

    @Override
    public ClientAuthentication clientAuthentication() {

        CubbyholeAuthenticationOptions options = CubbyholeAuthenticationOptions
                .builder()
                .initialToken(VaultToken.of(vaultToken))
                .wrapped()
                .build();

        return new CubbyholeAuthentication(options, restOperations());
    }

}
