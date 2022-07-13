package uz.uzcard.service.rest.rate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzcard.dto.rest.rate.ExchangeRateDTO;
import uz.uzcard.entity.rest.rate.ExchangeRateEntity;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.exception.AppBadRequestException;
import uz.uzcard.json.rest.rate.ExchangeRateJSON;
import uz.uzcard.repository.ExchangeRateRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final RestTemplate restTemplate;

    @Value("${server.exchange.rate}")
    private String serverUrl;

    @Value("${exchange.rate.ruble}")
    private String ruble;

    @Value("${exchange.rate.dollar}")
    private String dollar;


    //    @Scheduled(initialDelay = 500, fixedRate = 1000 * 60 * 60 * 24)
//    @Scheduled(cron = "${cron.expression.daily}")
    @Scheduled(cron = "${cron.expression.1minutes}")
    public void checkExchangeRate() {
        if (!exchangeRateRepository.findByCreatedDate(LocalDate.now()).isEmpty()) {
            log.info("We Have Today's Exchange Rate");
            return;
        }

        StringBuilder today = new StringBuilder("/");
        today.append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        log.info("Preparation for Today's Exchange Rate {}", today);

        try {

            ResponseEntity<ExchangeRateJSON[]> rubleDTO = restTemplate.getForEntity(serverUrl + ruble + today, ExchangeRateJSON[].class);
            ResponseEntity<ExchangeRateJSON[]> dollarDTO = restTemplate.getForEntity(serverUrl + dollar + today, ExchangeRateJSON[].class);


            if (!Optional.ofNullable(rubleDTO.getBody()).isPresent()
                    || !Optional.ofNullable(dollarDTO.getBody()).isPresent()
                    || rubleDTO.getStatusCodeValue() != 200
                    || dollarDTO.getStatusCodeValue() != 200) {
                log.error("Unsuccessfully Response rubleDTO={} dollarDTO={}", rubleDTO, dollarDTO);
                return;
            }

            saveExchangeRate(rubleDTO.getBody()[0]);
            saveExchangeRate(dollarDTO.getBody()[0]);

        } catch (Exception e) {
            log.error("Bad Request exception={}", e.toString());
            e.printStackTrace();
        }

    }

    private void saveExchangeRate(ExchangeRateJSON dto) {
        if (!Optional.ofNullable(dto.Rate).isPresent()) {
            log.warn("Exchange Rate Json Incorrect! {}", dto);
            throw new AppBadRequestException("Server Didn't Work!");
        }

        StringBuilder rate = new StringBuilder();
        for (String split : dto.Rate.split("\\.")) {
            rate.append(split);
        }

        ExchangeRateEntity entity = new ExchangeRateEntity();
        entity.setRate(Long.parseLong(rate.toString()));
        entity.setType(BalanceType.valueOf(dto.Ccy));
        exchangeRateRepository.save(entity);
    }

    public ExchangeRateDTO getTodayRate(BalanceType type) {
        ExchangeRateEntity entity = exchangeRateRepository
                .findByCreatedDateAndType(LocalDate.now(), type.name())
                .orElseThrow(() -> {
                    log.warn("Exchange Rate Not Found! {}", type);
                    return new AppBadRequestException("Server Didn't Work!");
                });

        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setRate(entity.getRate());
        dto.setType(entity.getType());

        return dto;
    }

}
