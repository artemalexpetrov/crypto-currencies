package crypto.investments.importer.config;

import crypto.investments.importer.mapper.CurrencyPriceMapper;
import crypto.investments.importer.model.CurrencyPrice;
import crypto.investments.importer.persistency.CurrencyPriceRepository;
import crypto.investments.importer.service.ImportedResourceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configures SpringBatch job for .csv files importing
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableConfigurationProperties(ImportProperties.class)
public class ImportConfiguration {

    private static final String IMPORT_JOB_NAME = "crypto-currencies-import";
    private static final String IMPORTED_FILES_HANDLING_STEP = "imported-handling-step";
    private static final String IMPORT_STEP_NAME = "import-step";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ImportProperties properties;

    @Bean
    public Job importJob() {
        // TODO: add job parameters validation
        return new JobBuilder(IMPORT_JOB_NAME, jobRepository)
            .start(importStep())
            .next(importedFileMovingStep(null))
            .build();
    }

    @Bean
    @JobScope
    public Step importedFileMovingStep(ImportedResourceHandler resourceHandler) {
        return new StepBuilder(IMPORTED_FILES_HANDLING_STEP, jobRepository)
            .tasklet(resourceHandler, transactionManager)
            .build();
    }

    @Bean
    public Step importStep() {
        return new StepBuilder(IMPORT_STEP_NAME, jobRepository)
            .<CurrencyPrice, CurrencyPrice>chunk(properties.getChunkSize(), transactionManager)
            .reader(currencyValuesReader(null))
            .writer(currencyValuesWriter(null))
            .build();
    }

    @Bean
    public ItemWriter<CurrencyPrice> currencyValuesWriter(CurrencyPriceRepository repository) {
        // TODO: implement something more appropriate
        return repository::saveAll;
    }

    @Bean
    @JobScope
    public FlatFileItemReader<CurrencyPrice> currencyValuesReader(@Value("#{jobParameters['resource']}")Resource resource) {
        LineMapper<CurrencyPrice> lineMapper = importingLineMapper(null);

        FlatFileItemReader<CurrencyPrice> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(properties.getHeadersSize());
        itemReader.setEncoding(properties.getCharset().name());
        itemReader.setResource(resource);
        itemReader.setLineMapper(lineMapper);

        return itemReader;
    }

    @Bean
    public LineMapper<CurrencyPrice> importingLineMapper(CurrencyPriceMapper mapper) {
        DefaultLineMapper<CurrencyPrice> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(mapper);

        return lineMapper;
    }
}
