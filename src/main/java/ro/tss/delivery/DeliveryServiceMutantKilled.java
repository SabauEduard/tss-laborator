package ro.tss.delivery;

/**
 * MUTANT NE-ECHIVALENT care ESTE OMORÂT de teste.
 * 
 * MUTAȚIA APLICATĂ:
 * =================
 * Linia originală:
 *     if (distanceKm <= 10.0) {
 * 
 * Linia mutată:
 *     if (distanceKm < 10.0) {    // Mutație: <= înlocuit cu <
 * 
 * TIPUL MUTAȚIEI:
 * ===============
 * - Operator de mutație: Relational Operator Replacement (ROR)
 * - Înlocuirea operatorului <= cu <
 * 
 * EXPLICAȚIE DE CE NU ESTE ECHIVALENT:
 * ====================================
 * Mutația schimbă comportamentul pentru cazul exact distanceKm = 10.0:
 * 
 * | distanceKm | Original (<=)        | Mutant (<)           |
 * |------------|----------------------|----------------------|
 * | 9.99       | T → 0.50 RON/km     | T → 0.50 RON/km     |
 * | 10.0       | T → 0.50 RON/km     | F → 0.40 RON/km     | ← DIFERENȚĂ!
 * | 10.01      | F → 0.40 RON/km     | F → 0.40 RON/km     |
 * 
 * Pentru d = 10.0:
 * - Original: distanceFee = 10.0 * 0.50 = 5.00 RON
 * - Mutant:   distanceFee = 10.0 * 0.40 = 4.00 RON
 * 
 * TEST CARE OMOARĂ MUTANTUL:
 * ==========================
 * Test BVA_D5: d = 10.0, w = 1.0
 * - Expected (original): 5.00 + 5.00 + 0.00 = 10.00 RON
 * - Actual (mutant):     5.00 + 4.00 + 0.00 = 9.00 RON
 * - Assertion FAILS → Mutant KILLED ✓
 */
public class DeliveryServiceMutantKilled {

    private static final double BASE_FEE = 5.00;
    private static final double RATE_SHORT_DISTANCE = 0.50;
    private static final double RATE_MEDIUM_DISTANCE = 0.40;
    private static final double RATE_LONG_DISTANCE = 0.30;
    private static final double DISTANCE_THRESHOLD_SHORT = 10.0;
    private static final double DISTANCE_THRESHOLD_MEDIUM = 50.0;
    private static final double WEIGHT_FEE_LIGHT = 0.00;
    private static final double WEIGHT_FEE_MEDIUM = 4.50;
    private static final double WEIGHT_FEE_HEAVY = 8.00;
    private static final double WEIGHT_FEE_VERY_HEAVY = 15.00;
    private static final double WEIGHT_THRESHOLD_LIGHT = 2.0;
    private static final double WEIGHT_THRESHOLD_MEDIUM = 5.0;
    private static final double WEIGHT_THRESHOLD_HEAVY = 15.0;

    public double calculateDeliveryFee(double distanceKm, double weightKg) {
        if (distanceKm <= 0 || weightKg <= 0) {
            throw new IllegalArgumentException("Distanța și greutatea trebuie să fie pozitive.");
        }

        double distanceFee;
        // ========== MUTAȚIE NE-ECHIVALENTĂ (OMORÂTĂ) ==========
        // Original: if (distanceKm <= DISTANCE_THRESHOLD_SHORT)
        // Mutant:   if (distanceKm < DISTANCE_THRESHOLD_SHORT)
        if (distanceKm < DISTANCE_THRESHOLD_SHORT) {  // MUTAȚIE: <= schimbat în <
        // ======================================================
            distanceFee = distanceKm * RATE_SHORT_DISTANCE;
        } else if (distanceKm <= DISTANCE_THRESHOLD_MEDIUM) {
            distanceFee = distanceKm * RATE_MEDIUM_DISTANCE;
        } else {
            distanceFee = distanceKm * RATE_LONG_DISTANCE;
        }

        double weightFee;
        if (weightKg <= WEIGHT_THRESHOLD_LIGHT) {
            weightFee = WEIGHT_FEE_LIGHT;
        } else if (weightKg <= WEIGHT_THRESHOLD_MEDIUM) {
            weightFee = WEIGHT_FEE_MEDIUM;
        } else if (weightKg <= WEIGHT_THRESHOLD_HEAVY) {
            weightFee = WEIGHT_FEE_HEAVY;
        } else {
            weightFee = WEIGHT_FEE_VERY_HEAVY;
        }

        return BASE_FEE + distanceFee + weightFee;
    }
}

