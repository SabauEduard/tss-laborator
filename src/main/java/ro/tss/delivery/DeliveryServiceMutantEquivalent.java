package ro.tss.delivery;

/**
 * MUTANT ECHIVALENT de ordinul 1.
 * 
 * MUTAȚIA APLICATĂ:
 * =================
 * Linia originală (în calculul weightFee):
 *     if (weightKg <= 2.0) {
 *         weightFee = 0.0;
 *     }
 * 
 * Linia mutată:
 *     if (weightKg <= 2.0) {
 *         weightFee = 0.0 * weightKg;  // Mutație: înmulțire cu weightKg
 *     }
 * 
 * EXPLICAȚIE DE CE ESTE ECHIVALENT:
 * =================================
 * Mutația înmulțește 0.0 cu weightKg. Matematic:
 *     0.0 * weightKg = 0.0 (pentru orice valoare a weightKg)
 * 
 * Deci comportamentul rămâne identic cu cel original:
 * - În ambele cazuri, weightFee = 0.0 când weightKg <= 2.0
 * - Niciun test nu poate distinge între versiunea originală și mutant
 * - Mutantul este semantic echivalent cu originalul
 * 
 * TIPUL MUTAȚIEI:
 * ===============
 * - Operator de mutație: Scalar Variable Replacement (SVR) / 
 *   Arithmetic Operator Insertion (AOI)
 * - Înlocuirea unei constante (0.0) cu o expresie (0.0 * weightKg)
 *   care produce același rezultat pentru toate valorile posibile
 */
public class DeliveryServiceMutantEquivalent {

    private static final double BASE_FEE = 5.00;
    private static final double RATE_SHORT_DISTANCE = 0.50;
    private static final double RATE_MEDIUM_DISTANCE = 0.40;
    private static final double RATE_LONG_DISTANCE = 0.30;
    private static final double DISTANCE_THRESHOLD_SHORT = 10.0;
    private static final double DISTANCE_THRESHOLD_MEDIUM = 50.0;
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
        if (distanceKm <= DISTANCE_THRESHOLD_SHORT) {
            distanceFee = distanceKm * RATE_SHORT_DISTANCE;
        } else if (distanceKm <= DISTANCE_THRESHOLD_MEDIUM) {
            distanceFee = distanceKm * RATE_MEDIUM_DISTANCE;
        } else {
            distanceFee = distanceKm * RATE_LONG_DISTANCE;
        }

        double weightFee;
        if (weightKg <= WEIGHT_THRESHOLD_LIGHT) {
            // ========== MUTAȚIE ECHIVALENTĂ ==========
            // Original: weightFee = 0.0;
            // Mutant:   weightFee = 0.0 * weightKg;
            // 0.0 * orice = 0.0, deci comportamentul este identic
            weightFee = 0.0 * weightKg;  // MUTAȚIE ECHIVALENTĂ
            // =========================================
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

