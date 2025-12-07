package ro.tss.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste bazate pe Equivalence Partitioning (EP).
 * 
 * PARTIȚII DE ECHIVALENȚĂ IDENTIFICATE:
 * 
 * Pentru distanceKm:
 *   - EP_D1: Invalide (d <= 0)           → IllegalArgumentException
 *   - EP_D2: Distanță scurtă (0 < d <= 10)    → 0.50 RON/km
 *   - EP_D3: Distanță medie (10 < d <= 50)    → 0.40 RON/km
 *   - EP_D4: Distanță lungă (d > 50)          → 0.30 RON/km
 * 
 * Pentru weightKg:
 *   - EP_W1: Invalide (w <= 0)           → IllegalArgumentException
 *   - EP_W2: Greutate ușoară (0 < w <= 2)     → 0.00 RON
 *   - EP_W3: Greutate medie (2 < w <= 5)      → 4.50 RON
 *   - EP_W4: Greutate mare (5 < w <= 15)      → 8.00 RON
 *   - EP_W5: Greutate foarte mare (w > 15)    → 15.00 RON
 * 
 * REPREZENTANȚI ALEȘI:
 *   - EP_D1: -5, 0
 *   - EP_D2: 5 km
 *   - EP_D3: 25 km
 *   - EP_D4: 75 km
 *   - EP_W1: -3, 0
 *   - EP_W2: 1 kg
 *   - EP_W3: 3 kg
 *   - EP_W4: 10 kg
 *   - EP_W5: 20 kg
 */
@DisplayName("Teste Equivalence Partitioning (EP)")
class EquivalencePartitioningTest {

    private DeliveryService service;
    private static final double DELTA = 0.001; // Toleranță pentru comparații double

    @BeforeEach
    void setUp() {
        service = new DeliveryService();
    }

    // ==================== PARTIȚII INVALIDE ====================

    @Nested
    @DisplayName("EP - Partiții Invalide")
    class InvalidPartitions {

        @Test
        @DisplayName("EP_D1a: Distanță negativă (-5 km) → Exception")
        void testNegativeDistance() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(-5, 1),
                    "Distanța negativă trebuie să genereze excepție");
        }

        @Test
        @DisplayName("EP_D1b: Distanță zero (0 km) → Exception")
        void testZeroDistance() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(0, 1),
                    "Distanța zero trebuie să genereze excepție");
        }

        @Test
        @DisplayName("EP_W1a: Greutate negativă (-3 kg) → Exception")
        void testNegativeWeight() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(5, -3),
                    "Greutatea negativă trebuie să genereze excepție");
        }

        @Test
        @DisplayName("EP_W1b: Greutate zero (0 kg) → Exception")
        void testZeroWeight() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(5, 0),
                    "Greutatea zero trebuie să genereze excepție");
        }

        @Test
        @DisplayName("EP_D1+W1: Ambele invalide → Exception")
        void testBothInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.calculateDeliveryFee(-1, -1),
                    "Ambele valori invalide trebuie să genereze excepție");
        }
    }

    // ==================== PARTIȚII VALIDE - DISTANȚĂ ====================

    @Nested
    @DisplayName("EP - Partiții Valide Distanță")
    class ValidDistancePartitions {

        @Test
        @DisplayName("EP_D2: Distanță scurtă (5 km) + Greutate ușoară (1 kg)")
        void testShortDistance() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 0.00
            // Total = 7.50 RON
            double result = service.calculateDeliveryFee(5, 1);
            assertEquals(7.50, result, DELTA,
                    "5 km, 1 kg: 5.00 + 2.50 + 0.00 = 7.50 RON");
        }

        @Test
        @DisplayName("EP_D3: Distanță medie (25 km) + Greutate medie (3 kg)")
        void testMediumDistance() {
            // T_B = 5.00, T_D = 25 * 0.40 = 10.00, T_G = 4.50
            // Total = 19.50 RON
            double result = service.calculateDeliveryFee(25, 3);
            assertEquals(19.50, result, DELTA,
                    "25 km, 3 kg: 5.00 + 10.00 + 4.50 = 19.50 RON");
        }

        @Test
        @DisplayName("EP_D4: Distanță lungă (75 km) + Greutate mare (10 kg)")
        void testLongDistance() {
            // T_B = 5.00, T_D = 75 * 0.30 = 22.50, T_G = 8.00
            // Total = 35.50 RON
            double result = service.calculateDeliveryFee(75, 10);
            assertEquals(35.50, result, DELTA,
                    "75 km, 10 kg: 5.00 + 22.50 + 8.00 = 35.50 RON");
        }
    }

    // ==================== PARTIȚII VALIDE - GREUTATE ====================

    @Nested
    @DisplayName("EP - Partiții Valide Greutate")
    class ValidWeightPartitions {

        @Test
        @DisplayName("EP_W2: Greutate ușoară (1 kg) → T_G = 0.00")
        void testLightWeight() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 0.00
            double result = service.calculateDeliveryFee(5, 1);
            assertEquals(7.50, result, DELTA,
                    "Greutate ușoară: T_G = 0.00");
        }

        @Test
        @DisplayName("EP_W3: Greutate medie (3 kg) → T_G = 4.50")
        void testMediumWeight() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 4.50
            double result = service.calculateDeliveryFee(5, 3);
            assertEquals(12.00, result, DELTA,
                    "Greutate medie: T_G = 4.50");
        }

        @Test
        @DisplayName("EP_W4: Greutate mare (10 kg) → T_G = 8.00")
        void testHeavyWeight() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 8.00
            double result = service.calculateDeliveryFee(5, 10);
            assertEquals(15.50, result, DELTA,
                    "Greutate mare: T_G = 8.00");
        }

        @Test
        @DisplayName("EP_W5: Greutate foarte mare (20 kg) → T_G = 15.00")
        void testVeryHeavyWeight() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 15.00
            double result = service.calculateDeliveryFee(5, 20);
            assertEquals(22.50, result, DELTA,
                    "Greutate foarte mare: T_G = 15.00");
        }
    }

    // ==================== COMBINAȚII COMPLETE ====================

    @Nested
    @DisplayName("EP - Combinații reprezentative")
    class CombinedPartitions {

        @Test
        @DisplayName("EP_D2 × EP_W5: Distanță scurtă + Greutate foarte mare")
        void testShortDistanceVeryHeavy() {
            // T_B = 5.00, T_D = 5 * 0.50 = 2.50, T_G = 15.00
            // Total = 22.50 RON
            double result = service.calculateDeliveryFee(5, 20);
            assertEquals(22.50, result, DELTA);
        }

        @Test
        @DisplayName("EP_D4 × EP_W2: Distanță lungă + Greutate ușoară")
        void testLongDistanceLightWeight() {
            // T_B = 5.00, T_D = 75 * 0.30 = 22.50, T_G = 0.00
            // Total = 27.50 RON
            double result = service.calculateDeliveryFee(75, 1);
            assertEquals(27.50, result, DELTA);
        }

        @Test
        @DisplayName("EP_D3 × EP_W4: Distanță medie + Greutate mare")
        void testMediumDistanceHeavyWeight() {
            // T_B = 5.00, T_D = 25 * 0.40 = 10.00, T_G = 8.00
            // Total = 23.00 RON
            double result = service.calculateDeliveryFee(25, 10);
            assertEquals(23.00, result, DELTA);
        }
    }
}

