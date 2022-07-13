package uz.uzcard.util;

public class CommissionUtil {

    public static Long commission(Long amount, Long afterAmount, Double commissionPercent, Double afterPercentage) {
        double commissionAmount = 0;

        if (amount < afterAmount) {
            commissionAmount = (amount * commissionPercent) / 100;
        } else {
            commissionAmount = (amount * afterPercentage) / 100;
        }

        return Math.round(commissionAmount);
    }

}
