package uz.uzcard.util;

import lombok.extern.slf4j.Slf4j;
import uz.uzcard.enums.card.BalanceType;

import java.security.SecureRandom;

@Slf4j
public class NumberUtil {

    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomSmsCode() {
        StringBuilder smsCode = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            smsCode.append(random.nextInt(10));
        }

        return smsCode.toString();
    }

    public static String generatePan(String issuerIdNumber) {
        log.info("Generating Card Number issuer={}", issuerIdNumber);

        StringBuilder cardNumber = new StringBuilder(issuerIdNumber);

        for (int i = 0; i < 16 - issuerIdNumber.length(); i++) {
            cardNumber.append(random.nextInt(10));
        }

        log.info("Generated Card Number cardNumber={}", cardNumber);

        return cardNumber.toString();
    }

    public static String generateCardCvvCode(String issuerIdNumber) {
        log.info("Generating Card CVV issuer={}", issuerIdNumber);

        StringBuilder cvvCode = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            cvvCode.append(random.nextInt(10));
        }

        log.info("Generated Card CVV cvvCode={}", cvvCode);

        return cvvCode.toString();
    }

    public static String doMaskedPan(String pan) {
        StringBuilder maskedPan = new StringBuilder(pan.substring(0, 6));

        maskedPan.append("******").append(pan, maskedPan.length(), pan.length());

        log.info("Do Masked Pan {}", maskedPan);

        return maskedPan.toString();
    }

    public static String balanceToType(Long balance, BalanceType type) {
        String cash = balance.toString();
        if (balance == 0) {
            return "0 " + type.name();
        }
        if (balance < 0) {
            cash = cash.substring(1);

            if (cash.length() < 2) {
                return "-0,0" + cash + " " + type.name();
            }
            if (cash.length() < 3) {
                return "-0," + cash + " " + type.name();
            }
        }

        if (cash.length() < 2) {
            return "0,0" + balance + " " + type.name();
        }
        if (cash.length() < 3) {
            return "0," + balance + " " + type.name();
        }

        String full = cash.substring(0, cash.length() - 2) + "," + cash.substring(cash.length() - 2) + " " + type.name();
        if (balance < 0) {
            return '-' + full;
        }
        return full;
    }

}
