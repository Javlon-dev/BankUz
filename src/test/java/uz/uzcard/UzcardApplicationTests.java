package uz.uzcard;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UzcardApplicationTests {

//    @Autowired
//    private ExchangeRateRepository exchangeRateRepository;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${server.exchange.rate}")
//    private String serverUrl;
//
//    @Value("${exchange.rate.ruble}")
//    private String ruble;
//
//    @Value("${exchange.rate.dollar}")
//    private String dollar;

//    public ExchangeRateDTO getTodayRate(BalanceType type) {
//
//    }

//        @Scheduled(initialDelay = 500, fixedRate = 1000 * 60 * 60 * 24)
//    public void checkExchangeRate() {
//        if (!exchangeRateRepository.findByCreatedDate(LocalDate.now()).isEmpty()) {
//            return;
//        }
//
//        StringBuilder today = new StringBuilder("/");
//        today.append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//
//        try {
//
//            ResponseEntity<ExchangeRateJSON[]> rubleDTO = restTemplate.getForEntity(serverUrl + ruble + today, ExchangeRateJSON[].class);
//            ResponseEntity<ExchangeRateJSON[]> dollarDTO = restTemplate.getForEntity(serverUrl + dollar + today, ExchangeRateJSON[].class);


//            saveExchangeRate(rubleDTO.getBody()[0]);
//            saveExchangeRate(dollarDTO.getBody()[0]);


//            if (!Optional.ofNullable(rubleDTO.getBody()).isPresent()
//                    || !Optional.ofNullable(dollarDTO.getBody()).isPresent()) {
//                log.warn("Unsuccessfully Response rubleDTO={} dollarDTO={}", rubleDTO, dollarDTO);
//                return;
//            }

//            saveExchangeRate(rubleDTO.getBody()[0]);
//            saveExchangeRate(dollarDTO.getBody()[0]);

//        } catch (RestClientException e) {
//            log.warn("Bad Request exception={}", e.toString());
//            e.printStackTrace();
//        }

//    }

//    private void saveExchangeRate(ExchangeRateJSON dto) {
//        StringBuilder rate = new StringBuilder();
//        for (String split : dto.Rate.split("\\.")) {
//            rate.append(split);
//        }
//
//        ExchangeRateEntity entity = new ExchangeRateEntity();
//        entity.setRate(Long.parseLong(rate.toString()));
//        entity.setType(BalanceType.valueOf(dto.Ccy));
//        exchangeRateRepository.save(entity);
//    }

//    public ExchangeRateDTO todayRate() {
//        ExchangeRateEntity entity = exchangeRateRepository
//                .findByCreatedDate(LocalDate.now())
//                .get();
//
//        ExchangeRateDTO dto = new ExchangeRateDTO();
//        dto.Rate = (String.valueOf(entity.getRate()));
//        dto.Ccy = (entity.getType().name());
//
//        return dto;
//    }

    @Test
    void contextLoads() {
//        checkExchangeRate();
//        System.out.println(todayRate());
    }


}
