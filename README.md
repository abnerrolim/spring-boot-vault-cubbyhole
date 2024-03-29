# spring-boot-vault-cubbyhole
skeleton of spring-boot app with cubbyhole auth into [hashicorp vault](https://www.vaultproject.io/)

## Cubbyhole Auth
[Cubbyhole](https://www.vaultproject.io/api/secret/cubbyhole/index.html) is a vault concept to acquire some secret without need strong authentication. It's based into a previous authentication that generates a wrapped secret into the vault and provides a token that allows read (unwrap) that secret. Usually, this generated token can be used one time and/or has a short TTL.

Cubbyhole authentication uses this concept to provide a way to authenticate a backend without exposing any credentials on files or machine environment. Basically, a deployer with permission to generate tokens wraps the final token and inject the unwrap token into app env, so when that app comes up, it can use the unwrap token to obtain the final token. This final token usually has a restrictive policy allowing only what the app needs to ensure security.

The advantage is if this machine was hacked and env variables were exposed, only the access token will be read and this token will be restricted to its policy and Vault lifecycle, such as revocation, uses limit and TTL.

The drawback is if for any reason, the lifecycle of token fails, like refresh or unwrap, the application can't recover because it does not have credentials to re-login. So all flow need to be remade, the deployer needs to authenticate, wraps a token, and so on.

### Cubby Hole Auth Flow Abstraction
![Auth Flow](resources/vault_cubbyhole_flow.png?raw=true "Title")

## Dependencies
* Docker
* Docker compose
* maven 3.x
* java8

## Running
All commands should be made on project's root folder. 

Build the project
```bash
mvn install
```

vault docker ups
```bash
sudo docker-compose up
```

Run authentication and start script 
```bash
./auth-and-run-local.sh
```

Application will be ready http://localhost:8080.

## Details
1. The auth-and-run-local.sh script takes the role of deployer and generates a wrapped token with resources/app-policy.txt. 
2. Vault returns a unwrap token with ttl of 5min (be sure that your application start time is fast than that!) and with a usage counter of 1.
3. The script injects this unwrap token into VAULT_TOKEN env
4. Application uses VAULT_TOKEN env variable to acquire the wrapped token from Vault.

To ensure that the unwrap cycle will not be delayed, a VaultWarmUpIntializer was created with a @PostConstruct spring annotation and does a write on Vault starting the CubbiHole token exchange.

You can test if the application can write with current token (refresh cycle is ok) calling
http://localhost:8080/write/{any-word-that-you-want}

### Fixing Problems
TThe defaults configuration of spring-vault API do the token's refresh before 5 seconds of its expiration and this refresh job is scheduled with a thread pool's size of 1. This thread pool bean is the default bean, shared by other libs, which can lead to some situations that the refresh thread can be delayed and unable to execute before token's expiration.

Also, the spring-vault (at least until this version) logs all 4XX reponses of vault on lifecycle manager as debug, so if your log level is lesser than debug, you will never will see refreshs fails and only will take a permission error when calls Vault operations.

But you can override these beans on AbstractVaultConfiguration and set a bigger threadpool and so on. You also can configure your log lib to define debug level to spring-vault LifecycleAwareSessionManager class.

