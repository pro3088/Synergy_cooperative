package com.synergy.synergy_cooperative.config;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.synergy.synergy_cooperative")
@EnableJpaRepositories("com.synergy.synergy_cooperative")
@EnableTransactionManagement
public class DomainConfig {

}
