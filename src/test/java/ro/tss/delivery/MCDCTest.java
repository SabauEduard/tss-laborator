package ro.tss.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste bazate pe Modified Condition/Decision Coverage (MC/DC).
 * 
 * GRAFUL DE CONTROL AL FLUXULUI:
 * ==============================
 * 
 * [START]
 *    ↓
 * [N1] Entry Point
 *    ↓
 * [D1] if (distanceKm <= 0 || weightKg <= 0)  ← Decizie compusă (C1 OR C2)
 *    ├── True → [N2] throw IllegalArgumentException → [END_EXCEPTION]
 *    └── False ↓
 * [D2] if (distanceKm <= 10)  ← Condiție simplă (C3)
 *    ├── True → [N3] distanceFee = d * 0.50 →┐
 *    └── False ↓                             │
 * [D3] else if (distanceKm <= 50)  ← (C4)    │
 *    ├── True → [N4] distanceFee = d * 0.40 →┤
 *    └── False ↓                             │
 * [N5] distanceFee = d * 0.30 ───────────────┤
 *                                            ↓
 * [D4] if (weightKg <= 2)  ← (C5)
 *    ├── True → [N6] weightFee = 0.00 ───────┐
 *    └── False ↓                             │
 * [D5] else if (weightKg <= 5)  ← (C6)       │
 *    ├── True → [N7] weightFee = 4.50 ───────┤
 *    └── False ↓                             │
 * [D6] else if (weightKg <= 15)  ← (C7)      │
 *    ├── True → [N8] weightFee = 8.00 ───────┤
 *    └── False ↓                             │
 * [N9] weightFee = 15.00 ────────────────────┤
 *                                            ↓
 * [N10] return 5.00 + distanceFee + weightFee
 *    ↓
 * [END]
 * 
 * 
 * CONDIȚII IDENTIFICATE:
 * ======================
 * C1: distanceKm <= 0
 * C2: weightKg <= 0
 * C3: distanceKm <= 10
 * C4: distanceKm <= 50
 * C5: weightKg <= 2
 * C6: weightKg <= 5
 * C7: weightKg <= 15
 * 
 * DECIZII:
 * ========
 * D1: C1 OR C2 (decizie compusă)
 * D2: C3
 * D3: C4
 * D4: C5
 * D5: C6
 * D6: C7
 * 
 * 
 * MC/DC PENTRU DECIZIA D1 (C1 OR C2):
 * ===================================
 * Pentru MC/DC, fiecare condiție trebuie să afecteze independent rezultatul deciziei.
 * 
 * | Test | C1 (d<=0) | C2 (w<=0) | D1 (C1 OR C2) | Note                          |
 * |------|-----------|-----------|---------------|-------------------------------|
 * | M1   | F         | F         | F             | Ambele valide → continuă      |
 * | M2   | T         | F         | T             | C1 schimbă rezultatul         |
 * | M3   | F         | T         | T             | C2 schimbă rezultatul         |
 * 
 * Perechi care demonstrează independența:
 * - C1: M1 (F,F→F) vs M2 (T,F→T) - C1 schimbă rezultatul când C2=F
 * - C2: M1 (F,F→F) vs M3 (F,T→T) - C2 schimbă rezultatul când C1=F
 * 
 * 
 * MC/DC PENTRU DECIZIILE SIMPLE (D2-D6):
 * ======================================
 * Fiecare decizie simplă necesită 2 teste: unul cu condiția True, unul cu False.
 * 
 * D2 (C3: d <= 10):
 *   - M4: d = 5   → C3 = T → distanceFee = d * 0.50
 *   - M5: d = 25  → C3 = F → merge la D3
 * 
 * D3 (C4: d <= 50, executată doar când C3 = F):
 *   - M5: d = 25  → C4 = T → distanceFee = d * 0.40
 *   - M6: d = 75  → C4 = F → distanceFee = d * 0.30
 * 
 * D4 (C5: w <= 2):
 *   - M7: w = 1   → C5 = T → weightFee = 0.00
 *   - M8: w = 3   → C5 = F → merge la D5
 * 
 * D5 (C6: w <= 5, executată doar când C5 = F):
 *   - M8: w = 3   → C6 = T → weightFee = 4.50
 *   - M9: w = 10  → C6 = F → merge la D6
 * 
 * D6 (C7: w <= 15, executată doar când C5 = F și C6 = F):
 *   - M9: w = 10  → C7 = T → weightFee = 8.00
 *   - M10: w = 20 → C7 = F → weightFee = 15.00
 * 
 * 
 * SET MINIM DE TESTE MC/DC:
 * =========================
 * | Test | d (km) | w (kg) | C1 | C2 | C3 | C4 | C5 | C6 | C7 | Rezultat         |
 * |------|--------|--------|----|----|----|----|----|----|----| -----------------|
 * | M1   | 5      | 1      | F  | F  | T  | -  | T  | -  | -  | 7.50 RON         |
 * | M2   | -5     | 1      | T  | F  | -  | -  | -  | -  | -  | Exception        |
 * | M3   | 5      | -1     | F  | T  | -  | -  | -  | -  | -  | Exception        |
 * | M4   | 5      | 1      | F  | F  | T  | -  | T  | -  | -  | (=M1)            |
 * | M5   | 25     | 1      | F  | F  | F  | T  | T  | -  | -  | 15.00 RON        |
 * | M6   | 75     | 1      | F  | F  | F  | F  | T  | -  | -  | 27.50 RON        |
 * | M7   | 5      | 1      | F  | F  | T  | -  | T  | -  | -  | (=M1)            |
 * | M8   | 5      | 3      | F  | F  | T  | -  | F  | T  | -  | 12.00 RON        |
 * | M9   | 5      | 10     | F  | F  | T  | -  | F  | F  | T  | 15.50 RON        |
 * | M10  | 5      | 20     | F  | F  | T  | -  | F  | F  | F  | 22.50 RON        |
 * 
 * După eliminarea duplicatelor (M4=M1, M7=M1):
 * Set minim: {M1, M2, M3, M5, M6, M8, M9, M10} = 8 teste
 */
@DisplayName("Teste MC/DC (Modified Condition/Decision Coverage)")
class MCDCTest {

    private DeliveryService service;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        service = new DeliveryService();
    }

    // ==================== MC/DC PENTRU D1: C1 OR C2 ====================

    @Nested
    @DisplayName("MC/DC - Decizia D1: (distanceKm <= 0) OR (weightKg <= 0)")
    class DecisionD1_MCDC {

        @Test
        @DisplayName("M1: C1=F, C2=F → D1=F (Ambele valide, continuă execuția)")
        void testM1_BothValid() {
            // d = 5 > 0 → C1 = F
            // w = 1 > 0 → C2 = F
            // D1 = F OR F = F → nu aruncă excepție
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 0.00
            double result = service.calculateDeliveryFee(5, 1);
            assertEquals(7.50, result, DELTA,
                    "M1: Intrări valide, taxa calculată corect");
        }

        @Test
        @DisplayName("M2: C1=T, C2=F → D1=T (Distanță invalidă schimbă rezultatul)")
        void testM2_InvalidDistance() {
            // d = -5 <= 0 → C1 = T
            // w = 1 > 0 → C2 = F
            // D1 = T OR F = T → aruncă excepție
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(-5, 1),
                    "M2: C1 singur determină excepția");
        }

        @Test
        @DisplayName("M3: C1=F, C2=T → D1=T (Greutate invalidă schimbă rezultatul)")
        void testM3_InvalidWeight() {
            // d = 5 > 0 → C1 = F
            // w = -1 <= 0 → C2 = T
            // D1 = F OR T = T → aruncă excepție
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(5, -1),
                    "M3: C2 singur determină excepția");
        }
    }

    // ==================== MC/DC PENTRU D2 și D3: DISTANȚĂ ====================

    @Nested
    @DisplayName("MC/DC - Deciziile D2 și D3: Intervale distanță")
    class DecisionsD2D3_MCDC {

        @Test
        @DisplayName("M4/M1: C3=T → Distanță scurtă (d <= 10)")
        void testM4_ShortDistance() {
            // d = 5 → C3: 5 <= 10 = T
            // distanceFee = 5 * 0.50 = 2.50
            double result = service.calculateDeliveryFee(5, 1);
            assertEquals(7.50, result, DELTA,
                    "M4: Distanță scurtă, tarif 0.50 RON/km");
        }

        @Test
        @DisplayName("M5: C3=F, C4=T → Distanță medie (10 < d <= 50)")
        void testM5_MediumDistance() {
            // d = 25 → C3: 25 <= 10 = F, C4: 25 <= 50 = T
            // distanceFee = 25 * 0.40 = 10.00
            // T_B = 5.00, T_G = 0.00
            double result = service.calculateDeliveryFee(25, 1);
            assertEquals(15.00, result, DELTA,
                    "M5: Distanță medie, tarif 0.40 RON/km");
        }

        @Test
        @DisplayName("M6: C3=F, C4=F → Distanță lungă (d > 50)")
        void testM6_LongDistance() {
            // d = 75 → C3: 75 <= 10 = F, C4: 75 <= 50 = F
            // distanceFee = 75 * 0.30 = 22.50
            // T_B = 5.00, T_G = 0.00
            double result = service.calculateDeliveryFee(75, 1);
            assertEquals(27.50, result, DELTA,
                    "M6: Distanță lungă, tarif 0.30 RON/km");
        }
    }

    // ==================== MC/DC PENTRU D4, D5, D6: GREUTATE ====================

    @Nested
    @DisplayName("MC/DC - Deciziile D4, D5 și D6: Intervale greutate")
    class DecisionsD4D5D6_MCDC {

        @Test
        @DisplayName("M7/M1: C5=T → Greutate ușoară (w <= 2)")
        void testM7_LightWeight() {
            // w = 1 → C5: 1 <= 2 = T
            // weightFee = 0.00
            double result = service.calculateDeliveryFee(5, 1);
            assertEquals(7.50, result, DELTA,
                    "M7: Greutate ușoară, T_G = 0.00");
        }

        @Test
        @DisplayName("M8: C5=F, C6=T → Greutate medie (2 < w <= 5)")
        void testM8_MediumWeight() {
            // w = 3 → C5: 3 <= 2 = F, C6: 3 <= 5 = T
            // weightFee = 4.50
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50
            double result = service.calculateDeliveryFee(5, 3);
            assertEquals(12.00, result, DELTA,
                    "M8: Greutate medie, T_G = 4.50");
        }

        @Test
        @DisplayName("M9: C5=F, C6=F, C7=T → Greutate mare (5 < w <= 15)")
        void testM9_HeavyWeight() {
            // w = 10 → C5: 10 <= 2 = F, C6: 10 <= 5 = F, C7: 10 <= 15 = T
            // weightFee = 8.00
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50
            double result = service.calculateDeliveryFee(5, 10);
            assertEquals(15.50, result, DELTA,
                    "M9: Greutate mare, T_G = 8.00");
        }

        @Test
        @DisplayName("M10: C5=F, C6=F, C7=F → Greutate foarte mare (w > 15)")
        void testM10_VeryHeavyWeight() {
            // w = 20 → C5: 20 <= 2 = F, C6: 20 <= 5 = F, C7: 20 <= 15 = F
            // weightFee = 15.00
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50
            double result = service.calculateDeliveryFee(5, 20);
            assertEquals(22.50, result, DELTA,
                    "M10: Greutate foarte mare, T_G = 15.00");
        }
    }

    // ==================== VERIFICARE ACOPERIRE MC/DC ====================

    @Nested
    @DisplayName("MC/DC - Verificare acoperire completă")
    class MCDCCoverageVerification {

        @Test
        @DisplayName("Verificare: Toate căile prin if-else distanță sunt acoperite")
        void verifyDistancePathsCovered() {
            // Calea 1: d <= 10 (N1 → D1[F] → D2[T] → N3 → ...)
            assertEquals(7.50, service.calculateDeliveryFee(5, 1), DELTA);
            
            // Calea 2: 10 < d <= 50 (N1 → D1[F] → D2[F] → D3[T] → N4 → ...)
            assertEquals(15.00, service.calculateDeliveryFee(25, 1), DELTA);
            
            // Calea 3: d > 50 (N1 → D1[F] → D2[F] → D3[F] → N5 → ...)
            assertEquals(27.50, service.calculateDeliveryFee(75, 1), DELTA);
        }

        @Test
        @DisplayName("Verificare: Toate căile prin if-else greutate sunt acoperite")
        void verifyWeightPathsCovered() {
            // Calea 1: w <= 2 (... → D4[T] → N6 → N10)
            assertEquals(7.50, service.calculateDeliveryFee(5, 1), DELTA);
            
            // Calea 2: 2 < w <= 5 (... → D4[F] → D5[T] → N7 → N10)
            assertEquals(12.00, service.calculateDeliveryFee(5, 3), DELTA);
            
            // Calea 3: 5 < w <= 15 (... → D4[F] → D5[F] → D6[T] → N8 → N10)
            assertEquals(15.50, service.calculateDeliveryFee(5, 10), DELTA);
            
            // Calea 4: w > 15 (... → D4[F] → D5[F] → D6[F] → N9 → N10)
            assertEquals(22.50, service.calculateDeliveryFee(5, 20), DELTA);
        }

        @Test
        @DisplayName("Verificare: Calea excepției este acoperită")
        void verifyExceptionPathCovered() {
            // Calea excepție: N1 → D1[T] → N2 → END_EXCEPTION
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(-5, 1));
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(5, -1));
        }
    }

    // ==================== SUMAR SETURI DE TESTE MC/DC ====================

    @Nested
    @DisplayName("MC/DC - Set minim de teste (8 teste)")
    class MCDCMinimalTestSet {

        @Test
        @DisplayName("Set complet MC/DC executat")
        void runAllMCDCTests() {
            // M1: d=5, w=1 → 7.50 RON
            assertEquals(7.50, service.calculateDeliveryFee(5, 1), DELTA, "M1");
            
            // M2: d=-5, w=1 → Exception
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(-5, 1), "M2");
            
            // M3: d=5, w=-1 → Exception
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(5, -1), "M3");
            
            // M5: d=25, w=1 → 15.00 RON
            assertEquals(15.00, service.calculateDeliveryFee(25, 1), DELTA, "M5");
            
            // M6: d=75, w=1 → 27.50 RON
            assertEquals(27.50, service.calculateDeliveryFee(75, 1), DELTA, "M6");
            
            // M8: d=5, w=3 → 12.00 RON
            assertEquals(12.00, service.calculateDeliveryFee(5, 3), DELTA, "M8");
            
            // M9: d=5, w=10 → 15.50 RON
            assertEquals(15.50, service.calculateDeliveryFee(5, 10), DELTA, "M9");
            
            // M10: d=5, w=20 → 22.50 RON
            assertEquals(22.50, service.calculateDeliveryFee(5, 20), DELTA, "M10");
        }
    }
}

