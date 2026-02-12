package db.migration;

import kz.che.xm.crypto.model.CryptoRateModel;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.lang.Long.parseLong;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static kz.che.xm.crypto.type.CurrencyType.valueOf;

/**
 * Flyway Java migration that initializes the {@code crypto_rate} table with price data from CSV files.
 * <p>
 * Data source:
 * <ul>
 *   <li>Directory: taken from {@code APP_DATA_DIR} environment variable, defaults to {@code /data}</li>
 *   <li>File list: {@code files.list} in the data directory, containing one CSV filename per line</li>
 *   <li>CSV header (optional): {@code timestamp,symbol,price}</li>
 * </ul>
 * <p>
 * CSV format (per row):
 * <pre>{@code
 * timestamp,symbol,price
 * 1640995200000,BTC,46813.12
 * }</pre>
 * <p>
 * Insert strategy:
 * <ul>
 *   <li>Uses batch inserts into {@code crypto_rate(ts, currency, rate)}</li>
 *   <li>Applies {@code ON CONFLICT (currency, ts) DO NOTHING} to make the migration idempotent</li>
 * </ul>
 * <p>
 * Concurrency:
 * <ul>
 *   <li>Reads and parses CSV files concurrently using virtual threads</li>
 *   <li>Per-file parsing produces {@link CryptoRateModel} records which are then inserted in batches</li>
 * </ul>
 */
public class V2__init_prices extends BaseJavaMigration {
    private static final String SQL = """
            INSERT INTO crypto_rate(ts, currency, rate) 
            VALUES (?, ?, ?) ON CONFLICT (currency, ts) DO NOTHING
            """;
    private static final String HEADER = "timestamp,symbol,price";
    private static final String FILES_LIST_NAME = "files.list";

    /**
     * Base directory for input files. Defaults to {@code /data} when {@code APP_DATA_DIR} is not set.
     */
    private final String dataPath = System.getenv().getOrDefault("APP_DATA_DIR", "/data");

    @Override
    public void migrate(Context context) throws Exception {
        List<String> files = getFiles();
        try (PreparedStatement statement = context.getConnection().prepareStatement(SQL);
             // File I/O (reading CSVs) is a blocking operation.
             // We parse files concurrently using virtual threads to keep the implementation simple and scalable.
             ExecutorService executor = newVirtualThreadPerTaskExecutor()) {

            List<Future<List<CryptoRateModel>>> futures = new ArrayList<>(files.size());
            for (String file : files) {
                futures.add(executor.submit(() -> buildRates(file)));
            }
            for (var future : futures) {
                List<CryptoRateModel> part = future.get();
                for (CryptoRateModel rate : part) {
                    statement.setLong(1, rate.getTs());
                    statement.setString(2, rate.getCurrency().name());
                    statement.setBigDecimal(3, rate.getRate());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
    }

    private List<String> getFiles() throws IOException {
        Path listPath = Paths.get(dataPath, FILES_LIST_NAME);
        return Files.readAllLines(listPath, StandardCharsets.UTF_8);
    }

    private List<CryptoRateModel> buildRates(String file) {
        Path path = Paths.get(dataPath, file);
        List<CryptoRateModel> rates = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (!headerSkipped) {
                    headerSkipped = true;
                    if (line.equals(HEADER)) {
                        continue;
                    }
                }

                String[] parts = line.split(",");
                long timeStamp = parseLong(parts[0]);
                String cur = parts[1].toUpperCase();
                BigDecimal rate = new BigDecimal(parts[2]);

                rates.add(CryptoRateModel.builder()
                        .ts(timeStamp)
                        .currency(valueOf(cur))
                        .rate(rate)
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception while migration.");
        }
        return rates;
    }
}