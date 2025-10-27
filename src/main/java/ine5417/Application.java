package ine5417;

import ine5417.commom.Constants;
import ine5417.commom.Endpoints;
import ine5417.controllers.CipherController;
import ine5417.database.Channel;
import ine5417.database.ChannelService;
import ine5417.records.BruteForceResult;
import ine5417.records.Ciphered;
import ine5417.records.CreateChannelRequest;
import ine5417.records.Deciphered;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final ChannelService  channelService;
    public Application(CipherController cipherController, ChannelService channelService) {
        this.cipherController = cipherController;
        this.channelService = channelService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Operation(summary = "Cria um canal de comunicação")
    @ApiResponse(responseCode = "200", description = "channel gerado com sucesso")
    @Tag(name = "Apa")
    @PostMapping(value = Endpoints.CREATE_CHANNEL, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Channel createApplication(
            @Valid @RequestBody CreateChannelRequest request) {
        return channelService.createChannel(request.name(), request.description(), request.email());
    }

    @Operation(description = "Cipher")
    @ApiResponse(responseCode = "200", description = "Successfully ciphered the content")
    @GetMapping(value = Endpoints.CIPHER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Ciphered cipher(@PathVariable Channel channel,
                           @RequestParam("plaintext") String plaintext,
                           @RequestParam("cipher") String cipher,
                           @RequestParam("key") String key) throws BadRequestException {
        return cipherController.encrypt(plaintext, cipher, key);
    }

    @Operation(description = "Decipher")
    @ApiResponse(responseCode = "200", description = "Successfully deciphered the content")
    @GetMapping(value = Endpoints.DECIPHER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Deciphered decipher(@PathVariable Channel channel,
                               @RequestParam("toDecrypt") String toDecrypt,
                               @RequestParam("cipher") String cipher,
                               @RequestParam("key") String key) throws BadRequestException {

        return cipherController.decrypt(toDecrypt, cipher, key);
    }

    @Operation(description = "BruteForceResult")
    @ApiResponse(responseCode = "200", description = "Successfully bruteforced the content")
    @GetMapping(value = Endpoints.BRUTEFORCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BruteForceResult bruteforce(@PathVariable Channel channel,
                                       @RequestParam("toDecrypt") String toDecrypt,
                                       @RequestParam("cipher") String cipher) throws BadRequestException {
        return cipherController.bruteforce(toDecrypt, cipher);
    }

    @Operation(description = "ListCiphers")
    @ApiResponse(responseCode = "200", description = "Successfully listed ciphers")
    @GetMapping(value = Endpoints.LIST_CIPHERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> bruteforce() {
        return cipherController.listCiphers();
    }
}
