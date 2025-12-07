package ro.tss.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste bazate pe Cause-Effect Graphing (CEG).
 * 
 * CAUZE IDENTIFICATE (Intrări):
 * =============================
 * C1: distanceKm <= 0          (Distanță invalidă)
 * C2: weightKg <= 0            (Greutate invalidă)
 * C3: 0 < distanceKm <= 10     (Distanță scurtă)
 * C4: 10 < distanceKm <= 50    (Distanță medie)
 * C5: distanceKm > 50          (Distanță lungă)
 * C6: 0 < weightKg <= 2        (Greutate ușoară)
 * C7: 2 < weightKg <= 5        (Greutate medie)
 * C8: 5 < weightKg <= 15       (Greutate mare)
 * C9: weightKg > 15            (Greutate foarte mare)
 * 
 * EFECTE IDENTIFICATE (Ieșiri):
 * =============================
 * E1: IllegalArgumentException  (Excepție pentru intrări invalide)
 * E2: T_D = d × 0.50           (Tarif distanță scurtă)
 * E3: T_D = d × 0.40           (Tarif distanță medie)
 * E4: T_D = d × 0.30           (Tarif distanță lungă)
 * E5: T_G = 0.00               (Taxa greutate ușoară)
 * E6: T_G = 4.50               (Taxa greutate medie)
 * E7: T_G = 8.00               (Taxa greutate mare)
 * E8: T_G = 15.00              (Taxa greutate foarte mare)
 * E9: Taxa totală calculată    (T_B + T_D + T_G)
 * 
 * CONSTRÂNGERI LOGICE:
 * ====================
 * - C3, C4, C5 sunt mutual exclusive (doar una poate fi adevărată)
 * - C6, C7, C8, C9 sunt mutual exclusive (doar una poate fi adevărată)
 * - C1 OR C2 → E1 (orice intrare invalidă generează excepție)
 * - NOT(C1) AND NOT(C2) → E9 (intrări valide generează taxă)
 * 
 * TABEL DE DECIZIE (Decision Table):
 * ==================================
 * 
 * | Test | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 | C9 | → | E1 | E2 | E3 | E4 | E5 | E6 | E7 | E8 | E9 |
 * |------|----|----|----|----|----|----|----|----|----| - |----|----|----|----|----|----|----|----|----|
 * | T1   | T  | -  | -  | -  | -  | -  | -  | -  | -  |   | T  | -  | -  | -  | -  | -  | -  | -  | -  |
 * | T2   | -  | T  | -  | -  | -  | -  | -  | -  | -  |   | T  | -  | -  | -  | -  | -  | -  | -  | -  |
 * | T3   | T  | T  | -  | -  | -  | -  | -  | -  | -  |   | T  | -  | -  | -  | -  | -  | -  | -  | -  |
 * | T4   | F  | F  | T  | F  | F  | T  | F  | F  | F  |   | F  | T  | F  | F  | T  | F  | F  | F  | T  |
 * | T5   | F  | F  | T  | F  | F  | F  | T  | F  | F  |   | F  | T  | F  | F  | F  | T  | F  | F  | T  |
 * | T6   | F  | F  | T  | F  | F  | F  | F  | T  | F  |   | F  | T  | F  | F  | F  | F  | T  | F  | T  |
 * | T7   | F  | F  | T  | F  | F  | F  | F  | F  | T  |   | F  | T  | F  | F  | F  | F  | F  | T  | T  |
 * | T8   | F  | F  | F  | T  | F  | T  | F  | F  | F  |   | F  | F  | T  | F  | T  | F  | F  | F  | T  |
 * | T9   | F  | F  | F  | T  | F  | F  | T  | F  | F  |   | F  | F  | T  | F  | F  | T  | F  | F  | T  |
 * | T10  | F  | F  | F  | T  | F  | F  | F  | T  | F  |   | F  | F  | T  | F  | F  | F  | T  | F  | T  |
 * | T11  | F  | F  | F  | T  | F  | F  | F  | F  | T  |   | F  | F  | T  | F  | F  | F  | F  | T  | T  |
 * | T12  | F  | F  | F  | F  | T  | T  | F  | F  | F  |   | F  | F  | F  | T  | T  | F  | F  | F  | T  |
 * | T13  | F  | F  | F  | F  | T  | F  | T  | F  | F  |   | F  | F  | F  | T  | F  | T  | F  | F  | T  |
 * | T14  | F  | F  | F  | F  | T  | F  | F  | T  | F  |   | F  | F  | F  | T  | F  | F  | T  | F  | T  |
 * | T15  | F  | F  | F  | F  | T  | F  | F  | F  | T  |   | F  | F  | F  | T  | F  | F  | F  | T  | T  |
 */
@DisplayName("Teste Cause-Effect Graphing (CEG)")
class CauseEffectGraphingTest {

    private DeliveryService service;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        service = new DeliveryService();
    }

    // ==================== TESTE PENTRU CAZURI DE EROARE (E1) ====================

    @Test
    @DisplayName("T1: C1=T → E1 (Distanță invalidă, greutate validă)")
    void testT1_InvalidDistance() {
        // C1: distanceKm <= 0 (folosim -5)
        // C2: weightKg > 0 (folosim 3)
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateDeliveryFee(-5, 3),
                "Distanța negativă trebuie să genereze IllegalArgumentException");
    }

    @Test
    @DisplayName("T2: C2=T → E1 (Distanță validă, greutate invalidă)")
    void testT2_InvalidWeight() {
        // C1: distanceKm > 0 (folosim 5)
        // C2: weightKg <= 0 (folosim -2)
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateDeliveryFee(5, -2),
                "Greutatea negativă trebuie să genereze IllegalArgumentException");
    }

    @Test
    @DisplayName("T3: C1=T, C2=T → E1 (Ambele invalide)")
    void testT3_BothInvalid() {
        // C1: distanceKm <= 0 (folosim 0)
        // C2: weightKg <= 0 (folosim 0)
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateDeliveryFee(0, 0),
                "Ambele invalide trebuie să genereze IllegalArgumentException");
    }

    // ==================== TESTE DISTANȚĂ SCURTĂ (C3) × TOATE GREUTĂȚILE ====================

    @Test
    @DisplayName("T4: C3=T, C6=T → E2+E5+E9 (Scurt + Ușor)")
    void testT4_ShortDistance_LightWeight() {
        // d = 5 km (C3: 0 < d <= 10)
        // w = 1 kg (C6: 0 < w <= 2)
        // T_B = 5.00, T_D = 5 × 0.50 = 2.50, T_G = 0.00
        // Total = 7.50 RON
        double result = service.calculateDeliveryFee(5, 1);
        assertEquals(7.50, result, DELTA);
    }

    @Test
    @DisplayName("T5: C3=T, C7=T → E2+E6+E9 (Scurt + Mediu)")
    void testT5_ShortDistance_MediumWeight() {
        // d = 5 km (C3)
        // w = 3 kg (C7: 2 < w <= 5)
        // T_B = 5.00, T_D = 5 × 0.50 = 2.50, T_G = 4.50
        // Total = 12.00 RON
        double result = service.calculateDeliveryFee(5, 3);
        assertEquals(12.00, result, DELTA);
    }

    @Test
    @DisplayName("T6: C3=T, C8=T → E2+E7+E9 (Scurt + Mare)")
    void testT6_ShortDistance_HeavyWeight() {
        // d = 5 km (C3)
        // w = 10 kg (C8: 5 < w <= 15)
        // T_B = 5.00, T_D = 5 × 0.50 = 2.50, T_G = 8.00
        // Total = 15.50 RON
        double result = service.calculateDeliveryFee(5, 10);
        assertEquals(15.50, result, DELTA);
    }

    @Test
    @DisplayName("T7: C3=T, C9=T → E2+E8+E9 (Scurt + Foarte Mare)")
    void testT7_ShortDistance_VeryHeavyWeight() {
        // d = 5 km (C3)
        // w = 20 kg (C9: w > 15)
        // T_B = 5.00, T_D = 5 × 0.50 = 2.50, T_G = 15.00
        // Total = 22.50 RON
        double result = service.calculateDeliveryFee(5, 20);
        assertEquals(22.50, result, DELTA);
    }

    // ==================== TESTE DISTANȚĂ MEDIE (C4) × TOATE GREUTĂȚILE ====================

    @Test
    @DisplayName("T8: C4=T, C6=T → E3+E5+E9 (Mediu + Ușor)")
    void testT8_MediumDistance_LightWeight() {
        // d = 25 km (C4: 10 < d <= 50)
        // w = 1 kg (C6)
        // T_B = 5.00, T_D = 25 × 0.40 = 10.00, T_G = 0.00
        // Total = 15.00 RON
        double result = service.calculateDeliveryFee(25, 1);
        assertEquals(15.00, result, DELTA);
    }

    @Test
    @DisplayName("T9: C4=T, C7=T → E3+E6+E9 (Mediu + Mediu)")
    void testT9_MediumDistance_MediumWeight() {
        // d = 25 km (C4)
        // w = 3 kg (C7)
        // T_B = 5.00, T_D = 25 × 0.40 = 10.00, T_G = 4.50
        // Total = 19.50 RON
        double result = service.calculateDeliveryFee(25, 3);
        assertEquals(19.50, result, DELTA);
    }

    @Test
    @DisplayName("T10: C4=T, C8=T → E3+E7+E9 (Mediu + Mare)")
    void testT10_MediumDistance_HeavyWeight() {
        // d = 25 km (C4)
        // w = 10 kg (C8)
        // T_B = 5.00, T_D = 25 × 0.40 = 10.00, T_G = 8.00
        // Total = 23.00 RON
        double result = service.calculateDeliveryFee(25, 10);
        assertEquals(23.00, result, DELTA);
    }

    @Test
    @DisplayName("T11: C4=T, C9=T → E3+E8+E9 (Mediu + Foarte Mare)")
    void testT11_MediumDistance_VeryHeavyWeight() {
        // d = 25 km (C4)
        // w = 20 kg (C9)
        // T_B = 5.00, T_D = 25 × 0.40 = 10.00, T_G = 15.00
        // Total = 30.00 RON
        double result = service.calculateDeliveryFee(25, 20);
        assertEquals(30.00, result, DELTA);
    }

    // ==================== TESTE DISTANȚĂ LUNGĂ (C5) × TOATE GREUTĂȚILE ====================

    @Test
    @DisplayName("T12: C5=T, C6=T → E4+E5+E9 (Lung + Ușor)")
    void testT12_LongDistance_LightWeight() {
        // d = 75 km (C5: d > 50)
        // w = 1 kg (C6)
        // T_B = 5.00, T_D = 75 × 0.30 = 22.50, T_G = 0.00
        // Total = 27.50 RON
        double result = service.calculateDeliveryFee(75, 1);
        assertEquals(27.50, result, DELTA);
    }

    @Test
    @DisplayName("T13: C5=T, C7=T → E4+E6+E9 (Lung + Mediu)")
    void testT13_LongDistance_MediumWeight() {
        // d = 75 km (C5)
        // w = 3 kg (C7)
        // T_B = 5.00, T_D = 75 × 0.30 = 22.50, T_G = 4.50
        // Total = 32.00 RON
        double result = service.calculateDeliveryFee(75, 3);
        assertEquals(32.00, result, DELTA);
    }

    @Test
    @DisplayName("T14: C5=T, C8=T → E4+E7+E9 (Lung + Mare)")
    void testT14_LongDistance_HeavyWeight() {
        // d = 75 km (C5)
        // w = 10 kg (C8)
        // T_B = 5.00, T_D = 75 × 0.30 = 22.50, T_G = 8.00
        // Total = 35.50 RON
        double result = service.calculateDeliveryFee(75, 10);
        assertEquals(35.50, result, DELTA);
    }

    @Test
    @DisplayName("T15: C5=T, C9=T → E4+E8+E9 (Lung + Foarte Mare)")
    void testT15_LongDistance_VeryHeavyWeight() {
        // d = 75 km (C5)
        // w = 20 kg (C9)
        // T_B = 5.00, T_D = 75 × 0.30 = 22.50, T_G = 15.00
        // Total = 42.50 RON
        double result = service.calculateDeliveryFee(75, 20);
        assertEquals(42.50, result, DELTA);
    }

    // ==================== TESTE PARAMETRIZATE PENTRU TOATE COMBINAȚIILE ====================

    static Stream<Arguments> provideAllValidCombinations() {
        return Stream.of(
                // T4-T7: Distanță scurtă (5 km)
                Arguments.of("T4", 5, 1, 7.50, "Scurt+Ușor"),
                Arguments.of("T5", 5, 3, 12.00, "Scurt+Mediu"),
                Arguments.of("T6", 5, 10, 15.50, "Scurt+Mare"),
                Arguments.of("T7", 5, 20, 22.50, "Scurt+F.Mare"),
                
                // T8-T11: Distanță medie (25 km)
                Arguments.of("T8", 25, 1, 15.00, "Mediu+Ușor"),
                Arguments.of("T9", 25, 3, 19.50, "Mediu+Mediu"),
                Arguments.of("T10", 25, 10, 23.00, "Mediu+Mare"),
                Arguments.of("T11", 25, 20, 30.00, "Mediu+F.Mare"),
                
                // T12-T15: Distanță lungă (75 km)
                Arguments.of("T12", 75, 1, 27.50, "Lung+Ușor"),
                Arguments.of("T13", 75, 3, 32.00, "Lung+Mediu"),
                Arguments.of("T14", 75, 10, 35.50, "Lung+Mare"),
                Arguments.of("T15", 75, 20, 42.50, "Lung+F.Mare")
        );
    }

    @ParameterizedTest(name = "{0}: d={1}km, w={2}kg → {3} RON ({4})")
    @MethodSource("provideAllValidCombinations")
    @DisplayName("CEG - Toate combinațiile valide (Tabel de Decizie)")
    void testAllValidCombinations(String testId, double distance, double weight,
                                   double expectedFee, String description) {
        double result = service.calculateDeliveryFee(distance, weight);
        assertEquals(expectedFee, result, DELTA,
                String.format("%s: Pentru %s, taxa ar trebui să fie %.2f RON",
                        testId, description, expectedFee));
    }
}

