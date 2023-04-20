package crypto.investments.importer.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "import")
public class ImportProperties {

    /**
     * Location pattern of files to import.
     * Example: /path/to/files/*.csv
     */
    @NotBlank
    private String importPattern;

    /**
     * Location(directory) where files will be moved after import
     */
    @NotBlank
    private String importedFilesLocation;

    /**
     * Characters set of files
     */
    @NotNull
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * Indicates if a CSV file with currency values contains headers
     * that needed to be skipped during the import
     */
    @Min(0)
    private int headersSize = 0;

    /**
     * Max number of items to include in single chunk during the import (transaction)
     */
    @Min(1)
    @Max(5000)
    private int chunkSize = 100;

    /**
     * New files discovery polling rate in milliseconds
     */
    @Min(1000)
    private long pollingRate = 10000;
}
