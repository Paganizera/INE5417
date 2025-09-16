package ine5417;

import ine5417.commom.Constants;
import ine5417.commom.Endpoints;
import ine5417.controllers.CipherController;
import ine5417.records.BruteForceResult;
import ine5417.records.Ciphered;
import ine5417.records.Deciphered;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@OpenAPIDefinition(
        info = @Info(
                title = Constants.APPLICATION_NAME,
                version = Constants.APPLICATION_VERSION,
                ///TODO: change application description
                description = """
                        """
        )
)
public class Application extends SpringBootServletInitializer {
    private final CipherController cipherController;

    public Application(CipherController cipherController) {
        this.cipherController = cipherController;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Operation(description = "Cipher")
    @ApiResponse(responseCode = "200", description = "Successfully ciphered the content")
    @GetMapping(value = Endpoints.CIPHER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Ciphered cipher(@RequestParam("plaintext") String plaintext,
                           @RequestParam("cipher") String cipher,
                           @RequestParam("key") String key) throws BadRequestException {
        return cipherController.encrypt(plaintext, cipher, key);
    }

    @Operation(description = "Decipher")
    @ApiResponse(responseCode = "200", description = "Successfully deciphered the content")
    @GetMapping(value = Endpoints.DECIPHER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Deciphered decipher(@RequestParam("toDecrypt") String toDecrypt,
                               @RequestParam("cipher") String cipher,
                               @RequestParam("key") String key) throws BadRequestException {

        return cipherController.decrypt(toDecrypt, cipher, key);
    }

    @Operation(description = "BruteForceResult")
    @ApiResponse(responseCode = "200", description = "Successfully bruteforced the content")
    @GetMapping(value = Endpoints.BRUTEFORCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BruteForceResult bruteforce(@RequestParam("toDecrypt") String toDecrypt,
                                       @RequestParam("cipher") String cipher) throws BadRequestException {
        return cipherController.bruteforce(toDecrypt, cipher);
    }
}
