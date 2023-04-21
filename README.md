# Crypto Investment

Crypto Investment is a cryptocurrency recommendation service for developers. The service helps developers
choose which cryptocurrency to invest in. Since the developers may not have much
knowledge about cryptocurrencies, the service will act as a guide and make recommendations on which cryptocurrencies to
choose.

## Running the service locally

```shell
git clone git@github.com:artemalexpetrov/crypto-currencies.git
cd crypto-currencies
docker-compose -f docker-compose-prod.yaml --env-file docker.env up -d
```

Open http://127.0.0.1:8080/docs/ui to explore available REST API endpoints.

## Configuration

There are some of available configuration properties are available in the `docker.env` file:

| Property                    | Default Value | Description                                                      |
|-----------------------------|---------------|------------------------------------------------------------------|
| IMPORTER_PORT               | 8085          | Host port to map container that running the importer application |
| IMPORTER_MANAGEMENT_PORT    | 8086          | Management port for the Importer application                     |
| IMPORT_DATA_HOST_DIRECTORY  | .data         | Directory that contains CSV files to import                      |
| API_PORT                    | 8080          | Host port to map container that running the API application      |
| API_MANAGEMENT_PORT         | 8081          | Management port for the API application                          |
| API_MAX_REQUESTS_PER_MINUTE | 60            | Requests rate limit                                              |
| OPENAPI_URL                 | /docs/api     | A URI to download docs in OpenAPI format                         |
| SWAGGER_URL                 | /docs/ui      | A URI to browse Swagger docs                                     |

## Components

To ensure responsibility segregation and scalability, the application was implemented as two separate modules:
`crypto.investments.api` and `crypto.investments.importer`. Each of these modules is a standalone application.

| Component                   | Description                                                                                                                          |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| crypto.investments.api      | A SpringBoot application that provides access to imported currencies via REST API                                                    |
| crypto.investments.importer | A StringBoot application that imports currency values from CSV files. Utilized SpringBatch to support huge files and import recovery |
| Database                    | PostgreSQL Timescale. This DB was chosen to efficiently work with time-series                                                        |
| Redis                       | For caching and rate-limiting                                                                                                        |

## Known issues & limitations

- The `crypto.investments.importer` moves imported files into `<data>/.imported` directory and remembers a name of
  imported file. Once a new file with same name is added into `<data>` directory importer ignores it. Temporary solution
  is to use timestamp in currency data filenames. Later it will be fixed with files checksums.

- The `crypto.investments.api` caches data fetched from a database. Cache eviction happens every N seconds. Actually
  the `crypto.investments.importer` should evict cache after import or send an event so other component can react on it
  and clear cache.

- Integration tests coverage should be improved  