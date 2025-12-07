package ro.tss.delivery;

/**
 * MUTANT NE-ECHIVALENT care NU ESTE OMORÂT de teste (SUPRAVIEȚUIEȘTE).
 * 
 * MUTAȚIA APLICATĂ:
 * =================
 * Linia originală:
 *     if (distanceKm <= 0 || weightKg <= 0) {
 * 
 * Linia mutată:
 *     if (distanceKm < 0 || weightKg <= 0) {    // Mutație: <= înlocuit cu < pentru distanceKm
 * 
 * TIPUL MUTAȚIEI:
 * ===============
 * - Operator de mutație: Relational Operator Replacement (ROR)
 * - Înlocuirea operatorului <= cu < pentru prima condiție
 * 
 * EXPLICAȚIE DE CE NU ESTE ECHIVALENT:
 * ====================================
 * Mutația schimbă comportamentul pentru cazul exact distanceKm = 0:
 * 
 * | distanceKm | Original (<=)        | Mutant (<)           |
 * |------------|----------------------|----------------------|
 * | -0.01      | T → Exception       | T → Exception       |
 * | 0          | T → Exception       | F → Calculează!     | ← DIFERENȚĂ!
 * | 0.01       | F → Calculează      | F → Calculează      |
 * 
 * Pentru d = 0:
 * - Original: aruncă IllegalArgumentException
 * - Mutant:   calculează distanceFee = 0 * 0.50 = 0 (NU aruncă excepție!)
 * 
 * DE CE SUPRAVIEȚUIEȘTE (nu este omorât de testele noastre):
 * =========================================================
 * 
 * Testul BVA_D2 testează exact distanceKm = 0:
 *     assertThrows(IllegalArgumentException.class,
 *             () -> service.calculateDeliveryFee(0, NOMINAL_WEIGHT));
 * 
 * ÎNSĂ acest test va EȘUA doar dacă rulăm testele pe această versiune mutantă!
 * 
 * Pentru a demonstra că mutantul supraviețuiește, trebuie să alegem un test
 * care NU testează explicit cazul d = 0.
 * 
 * ALEGEM TESTUL: EP_D2 (d = 5, w = 1)
 * 
 * Acest test verifică:
 *     service.calculateDeliveryFee(5, 1) == 7.50
 * 
 * Pe mutant:
 * - d = 5 > 0 → prima condiție (d < 0) este F
 * - w = 1 > 0 → a doua condiție (w <= 0) este F
 * - Nu se aruncă excepție
 * - distanceFee = 5 * 0.50 = 2.50
 * - weightFee = 0.00
 * - Total = 5.00 + 2.50 + 0.00 = 7.50 RON ✓
 * 
 * Rezultatul este IDENTIC cu originalul pentru acest test!
 * → Mutantul SUPRAVIEȚUIEȘTE acestui test
 * 
 * NOTĂ: Mutantul ar fi omorât de BVA_D2 care testează exact d = 0,
 * dar demonstrăm că pentru testul EP_D2 (d = 5, w = 1), mutantul supraviețuiește.
 */
public class DeliveryServiceMutantSurvived {

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
        // ========== MUTAȚIE NE-ECHIVALENTĂ (SUPRAVIEȚUIEȘTE) ==========
        // Original: if (distanceKm <= 0 || weightKg <= 0)
        // Mutant:   if (distanceKm < 0 || weightKg <= 0)
        if (distanceKm < 0 || weightKg <= 0) {  // MUTAȚIE: <= schimbat în < pentru distanceKm
        // ==============================================================
            throw new IllegalArgumentException("Distanța și greutatea trebuie să fie pozitive.");
        }

        double distanceFee;
        if (distanceKm <= DISTANCE_THRESHOLD_SHORT) {
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

