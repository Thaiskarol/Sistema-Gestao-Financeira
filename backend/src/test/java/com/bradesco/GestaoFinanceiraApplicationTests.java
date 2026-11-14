package com.bradesco;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Este teste usa H2 em memoria. As migrations (Flyway) usam sintaxe exclusiva
// do PostgreSQL (ex.: DO $$ na V4.1), entao o Flyway fica desligado aqui e o
// Hibernate cria o schema a partir das entidades. As migrations sao validadas
// no PostgreSQL real.
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.flyway.enabled=false"
})
class GestaoFinanceiraApplicationTests {

    @Test
    void contextLoads() {
    }
}