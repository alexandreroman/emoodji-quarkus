/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.emoodji.quarkus;

import io.agroal.api.AgroalDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Scanner;

@ApplicationScoped
public class PostgresEmoodjiRepository implements EmoodjiRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresEmoodjiRepository.class);
    private final DataSource ds;

    public PostgresEmoodjiRepository(AgroalDataSource ds) {
        this.ds = ds;
    }

    @PostConstruct
    void init() {
        LOGGER.info("Initializing PostgreSQL database schema");
        try (final var conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (final var in = getClass().getResourceAsStream("/schema-postgres.sql")) {
                final var scanner = new Scanner(in);
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    try (final var stmt = conn.prepareStatement(line)) {
                        stmt.executeUpdate();
                    }
                }
                conn.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PostgreSQL database schema", e);
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public int getCurrent() {
        try (final var conn = ds.getConnection()) {
            try (final var stmt = conn.prepareStatement("SELECT e.current FROM EMOODJI e WHERE e.id=0;")) {
                try (final var rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get current emoodji", e);
        }
        // Default value when no emoodji is set.
        return 0;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public int switchToNext() {
        try (final var conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            // Update the value in the current transaction.
            try (final var stmt = conn.prepareStatement("UPDATE EMOODJI SET current = nextval('EMOODJI_SEQ');")) {
                stmt.executeUpdate();
            }

            // Get current value.
            try (final var stmt = conn.prepareStatement("SELECT e.current FROM EMOODJI e WHERE e.id=0;")) {
                try (final var rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set next emoodji", e);
        }
        // This should not happen.
        throw new RuntimeException("Failed to set next emoodji");
    }

    @Override
    public String toString() {
        return "PostgreSQL Emoodji Repository";
    }
}
