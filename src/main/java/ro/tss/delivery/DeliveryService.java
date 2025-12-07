package ro.tss.delivery;

/**
 * Serviciu pentru calculul taxei de livrare.
 * 
 * Taxa totală (T_totală) = T_B + T_D + T_G
 * unde:
 * - T_B = Taxa de Bază (5.00 RON)
 * - T_D = Taxa pe Distanță (variabilă în funcție de km)
 * - T_G = Taxa pe Greutate (fixă per interval)
 */
public class DeliveryService {

    // Constante pentru taxa de bază
    private static final double BASE_FEE = 5.00;

    // Constante pentru taxa pe distanță (RON/km)
    private static final double RATE_SHORT_DISTANCE = 0.50;    // 0-10 km
    private static final double RATE_MEDIUM_DISTANCE = 0.40;   // 10-50 km
    private static final double RATE_LONG_DISTANCE = 0.30;     // >50 km

    // Praguri distanță (km)
    private static final double DISTANCE_THRESHOLD_SHORT = 10.0;
    private static final double DISTANCE_THRESHOLD_MEDIUM = 50.0;

    // Constante pentru taxa pe greutate (RON)
    private static final double WEIGHT_FEE_LIGHT = 0.00;       // 0-2 kg
    private static final double WEIGHT_FEE_MEDIUM = 4.50;      // 2-5 kg
    private static final double WEIGHT_FEE_HEAVY = 8.00;       // 5-15 kg
    private static final double WEIGHT_FEE_VERY_HEAVY = 15.00; // >15 kg

    // Praguri greutate (kg)
    private static final double WEIGHT_THRESHOLD_LIGHT = 2.0;
    private static final double WEIGHT_THRESHOLD_MEDIUM = 5.0;
    private static final double WEIGHT_THRESHOLD_HEAVY = 15.0;

    /**
     * Calculează taxa totală de livrare pe baza distanței și greutății.
     *
     * @param distanceKm Distanța de livrare în kilometri (trebuie să fie > 0)
     * @param weightKg   Greutatea pachetului în kilograme (trebuie să fie > 0)
     * @return Taxa totală de livrare în RON
     * @throws IllegalArgumentException dacă distanța sau greutatea nu sunt pozitive
     */
    public double calculateDeliveryFee(double distanceKm, double weightKg) {
        // C1: Verificare distanță validă
        // C2: Verificare greutate validă
        if (distanceKm <= 0 || weightKg <= 0) {
            throw new IllegalArgumentException("Distanța și greutatea trebuie să fie pozitive.");
        }

        // Calcul T_D (Taxa pe Distanță)
        double distanceFee;
        if (distanceKm <= DISTANCE_THRESHOLD_SHORT) {           // C3: d <= 10
            distanceFee = distanceKm * RATE_SHORT_DISTANCE;
        } else if (distanceKm <= DISTANCE_THRESHOLD_MEDIUM) {   // C4: d <= 50
            distanceFee = distanceKm * RATE_MEDIUM_DISTANCE;
        } else {                                                 // C5: d > 50
            distanceFee = distanceKm * RATE_LONG_DISTANCE;
        }

        // Calcul T_G (Taxa pe Greutate)
        double weightFee;
        if (weightKg <= WEIGHT_THRESHOLD_LIGHT) {               // C6: w <= 2
            weightFee = WEIGHT_FEE_LIGHT;
        } else if (weightKg <= WEIGHT_THRESHOLD_MEDIUM) {       // C7: w <= 5
            weightFee = WEIGHT_FEE_MEDIUM;
        } else if (weightKg <= WEIGHT_THRESHOLD_HEAVY) {        // C8: w <= 15
            weightFee = WEIGHT_FEE_HEAVY;
        } else {                                                 // C9: w > 15
            weightFee = WEIGHT_FEE_VERY_HEAVY;
        }

        // Taxa Totală = T_B + T_D + T_G
        return BASE_FEE + distanceFee + weightFee;
    }
}

