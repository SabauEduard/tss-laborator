package ro.tss.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste bazate pe Boundary Value Analysis (BVA).
 * 
 * VALORI LIMITĂ IDENTIFICATE:
 * 
 * Pentru distanceKm:
 *   Prag 0 (valid/invalid):
 *     - BVA_D1: d = -0.01 (invalid)
 *     - BVA_D2: d = 0     (invalid, pe limită)
 *     - BVA_D3: d = 0.01  (valid, imediat după limită)
 *   
 *   Prag 10 km (scurt → mediu):
 *     - BVA_D4: d = 9.99  (scurt)
 *     - BVA_D5: d = 10.0  (pe limită, scurt)
 *     - BVA_D6: d = 10.01 (mediu)
 *   
 *   Prag 50 km (mediu → lung):
 *     - BVA_D7: d = 49.99 (mediu)
 *     - BVA_D8: d = 50.0  (pe limită, mediu)
 *     - BVA_D9: d = 50.01 (lung)
 * 
 * Pentru weightKg:
 *   Prag 0 (valid/invalid):
 *     - BVA_W1: w = -0.01 (invalid)
 *     - BVA_W2: w = 0     (invalid, pe limită)
 *     - BVA_W3: w = 0.01  (valid)
 *   
 *   Prag 2 kg (ușor → mediu):
 *     - BVA_W4: w = 1.99  (ușor, T_G = 0)
 *     - BVA_W5: w = 2.0   (pe limită, T_G = 0)
 *     - BVA_W6: w = 2.01  (mediu, T_G = 4.50)
 *   
 *   Prag 5 kg (mediu → greu):
 *     - BVA_W7: w = 4.99  (mediu, T_G = 4.50)
 *     - BVA_W8: w = 5.0   (pe limită, T_G = 4.50)
 *     - BVA_W9: w = 5.01  (greu, T_G = 8.00)
 *   
 *   Prag 15 kg (greu → foarte greu):
 *     - BVA_W10: w = 14.99 (greu, T_G = 8.00)
 *     - BVA_W11: w = 15.0  (pe limită, T_G = 8.00)
 *     - BVA_W12: w = 15.01 (foarte greu, T_G = 15.00)
 */
@DisplayName("Teste Boundary Value Analysis (BVA)")
class BoundaryValueAnalysisTest {

    private DeliveryService service;
    private static final double DELTA = 0.001;
    
    // Valoare nominală pentru parametrul netratat (în mijlocul unei partiții)
    private static final double NOMINAL_DISTANCE = 5.0;  // Mijlocul intervalului (0, 10]
    private static final double NOMINAL_WEIGHT = 1.0;    // Mijlocul intervalului (0, 2]

    @BeforeEach
    void setUp() {
        service = new DeliveryService();
    }

    // ==================== LIMITE DISTANȚĂ - PRAG 0 ====================

    @Nested
    @DisplayName("BVA - Distanță: Limita 0 (valid/invalid)")
    class DistanceBoundaryZero {

        @Test
        @DisplayName("BVA_D1: d = -0.01 → Exception (invalid)")
        void testDistanceJustBelowZero() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(-0.01, NOMINAL_WEIGHT));
        }

        @Test
        @DisplayName("BVA_D2: d = 0 → Exception (pe limită, invalid)")
        void testDistanceExactlyZero() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(0, NOMINAL_WEIGHT));
        }

        @Test
        @DisplayName("BVA_D3: d = 0.01 → Valid (imediat după limită)")
        void testDistanceJustAboveZero() {
            // T_B = 5.00, T_D = 0.01 * 0.50 = 0.005, T_G = 0.00
            double result = service.calculateDeliveryFee(0.01, NOMINAL_WEIGHT);
            assertEquals(5.005, result, DELTA);
        }
    }

    // ==================== LIMITE DISTANȚĂ - PRAG 10 km ====================

    @Nested
    @DisplayName("BVA - Distanță: Limita 10 km (scurt → mediu)")
    class DistanceBoundaryTen {

        @Test
        @DisplayName("BVA_D4: d = 9.99 → Tarif scurt (0.50 RON/km)")
        void testDistanceJustBelow10() {
            // T_B = 5.00, T_D = 9.99 * 0.50 = 4.995, T_G = 0.00
            double result = service.calculateDeliveryFee(9.99, NOMINAL_WEIGHT);
            assertEquals(9.995, result, DELTA);
        }

        @Test
        @DisplayName("BVA_D5: d = 10.0 → Pe limită, tarif scurt (0.50 RON/km)")
        void testDistanceExactly10() {
            // T_B = 5.00, T_D = 10 * 0.50 = 5.00, T_G = 0.00
            double result = service.calculateDeliveryFee(10.0, NOMINAL_WEIGHT);
            assertEquals(10.00, result, DELTA);
        }

        @Test
        @DisplayName("BVA_D6: d = 10.01 → Tarif mediu (0.40 RON/km)")
        void testDistanceJustAbove10() {
            // T_B = 5.00, T_D = 10.01 * 0.40 = 4.004, T_G = 0.00
            double result = service.calculateDeliveryFee(10.01, NOMINAL_WEIGHT);
            assertEquals(9.004, result, DELTA);
        }
    }

    // ==================== LIMITE DISTANȚĂ - PRAG 50 km ====================

    @Nested
    @DisplayName("BVA - Distanță: Limita 50 km (mediu → lung)")
    class DistanceBoundaryFifty {

        @Test
        @DisplayName("BVA_D7: d = 49.99 → Tarif mediu (0.40 RON/km)")
        void testDistanceJustBelow50() {
            // T_B = 5.00, T_D = 49.99 * 0.40 = 19.996, T_G = 0.00
            double result = service.calculateDeliveryFee(49.99, NOMINAL_WEIGHT);
            assertEquals(24.996, result, DELTA);
        }

        @Test
        @DisplayName("BVA_D8: d = 50.0 → Pe limită, tarif mediu (0.40 RON/km)")
        void testDistanceExactly50() {
            // T_B = 5.00, T_D = 50 * 0.40 = 20.00, T_G = 0.00
            double result = service.calculateDeliveryFee(50.0, NOMINAL_WEIGHT);
            assertEquals(25.00, result, DELTA);
        }

        @Test
        @DisplayName("BVA_D9: d = 50.01 → Tarif lung (0.30 RON/km)")
        void testDistanceJustAbove50() {
            // T_B = 5.00, T_D = 50.01 * 0.30 = 15.003, T_G = 0.00
            double result = service.calculateDeliveryFee(50.01, NOMINAL_WEIGHT);
            assertEquals(20.003, result, DELTA);
        }
    }

    // ==================== LIMITE GREUTATE - PRAG 0 ====================

    @Nested
    @DisplayName("BVA - Greutate: Limita 0 (valid/invalid)")
    class WeightBoundaryZero {

        @Test
        @DisplayName("BVA_W1: w = -0.01 → Exception (invalid)")
        void testWeightJustBelowZero() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(NOMINAL_DISTANCE, -0.01));
        }

        @Test
        @DisplayName("BVA_W2: w = 0 → Exception (pe limită, invalid)")
        void testWeightExactlyZero() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(NOMINAL_DISTANCE, 0));
        }

        @Test
        @DisplayName("BVA_W3: w = 0.01 → Valid (T_G = 0.00)")
        void testWeightJustAboveZero() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 0.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 0.01);
            assertEquals(7.50, result, DELTA);
        }
    }

    // ==================== LIMITE GREUTATE - PRAG 2 kg ====================

    @Nested
    @DisplayName("BVA - Greutate: Limita 2 kg (ușor → mediu)")
    class WeightBoundaryTwo {

        @Test
        @DisplayName("BVA_W4: w = 1.99 → T_G = 0.00")
        void testWeightJustBelow2() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 0.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 1.99);
            assertEquals(7.50, result, DELTA);
        }

        @Test
        @DisplayName("BVA_W5: w = 2.0 → Pe limită, T_G = 0.00")
        void testWeightExactly2() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 0.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 2.0);
            assertEquals(7.50, result, DELTA);
        }

        @Test
        @DisplayName("BVA_W6: w = 2.01 → T_G = 4.50")
        void testWeightJustAbove2() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 4.50
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 2.01);
            assertEquals(12.00, result, DELTA);
        }
    }

    // ==================== LIMITE GREUTATE - PRAG 5 kg ====================

    @Nested
    @DisplayName("BVA - Greutate: Limita 5 kg (mediu → greu)")
    class WeightBoundaryFive {

        @Test
        @DisplayName("BVA_W7: w = 4.99 → T_G = 4.50")
        void testWeightJustBelow5() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 4.50
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 4.99);
            assertEquals(12.00, result, DELTA);
        }

        @Test
        @DisplayName("BVA_W8: w = 5.0 → Pe limită, T_G = 4.50")
        void testWeightExactly5() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 4.50
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 5.0);
            assertEquals(12.00, result, DELTA);
        }

        @Test
        @DisplayName("BVA_W9: w = 5.01 → T_G = 8.00")
        void testWeightJustAbove5() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 8.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 5.01);
            assertEquals(15.50, result, DELTA);
        }
    }

    // ==================== LIMITE GREUTATE - PRAG 15 kg ====================

    @Nested
    @DisplayName("BVA - Greutate: Limita 15 kg (greu → foarte greu)")
    class WeightBoundaryFifteen {

        @Test
        @DisplayName("BVA_W10: w = 14.99 → T_G = 8.00")
        void testWeightJustBelow15() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 8.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 14.99);
            assertEquals(15.50, result, DELTA);
        }

        @Test
        @DisplayName("BVA_W11: w = 15.0 → Pe limită, T_G = 8.00")
        void testWeightExactly15() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 8.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 15.0);
            assertEquals(15.50, result, DELTA);
        }

        @Test
        @DisplayName("BVA_W12: w = 15.01 → T_G = 15.00")
        void testWeightJustAbove15() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 15.00
            double result = service.calculateDeliveryFee(NOMINAL_DISTANCE, 15.01);
            assertEquals(22.50, result, DELTA);
        }
    }

    // ==================== TESTE PARAMETRIZATE PENTRU TOATE LIMITELE ====================

    @ParameterizedTest(name = "d={0} km, w={1} kg → {2} RON")
    @DisplayName("BVA - Teste parametrizate pentru toate valorile limită")
    @CsvSource({
        // Limite distanță cu greutate nominală (1 kg)
        "0.01, 1.0, 5.005",    // Imediat după 0
        "9.99, 1.0, 9.995",    // Imediat înainte de 10
        "10.0, 1.0, 10.00",    // Exact 10
        "10.01, 1.0, 9.004",   // Imediat după 10
        "49.99, 1.0, 24.996",  // Imediat înainte de 50
        "50.0, 1.0, 25.00",    // Exact 50
        "50.01, 1.0, 20.003",  // Imediat după 50
        
        // Limite greutate cu distanță nominală (5 km)
        "5.0, 0.01, 7.50",     // Imediat după 0 kg
        "5.0, 1.99, 7.50",     // Imediat înainte de 2 kg
        "5.0, 2.0, 7.50",      // Exact 2 kg
        "5.0, 2.01, 12.00",    // Imediat după 2 kg
        "5.0, 4.99, 12.00",    // Imediat înainte de 5 kg
        "5.0, 5.0, 12.00",     // Exact 5 kg
        "5.0, 5.01, 15.50",    // Imediat după 5 kg
        "5.0, 14.99, 15.50",   // Imediat înainte de 15 kg
        "5.0, 15.0, 15.50",    // Exact 15 kg
        "5.0, 15.01, 22.50"    // Imediat după 15 kg
    })
    void testBoundaryValues(double distance, double weight, double expectedFee) {
        double result = service.calculateDeliveryFee(distance, weight);
        assertEquals(expectedFee, result, DELTA,
                String.format("Pentru d=%.2f km și w=%.2f kg, taxa ar trebui să fie %.3f RON",
                        distance, weight, expectedFee));
    }
}

