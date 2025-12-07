package ro.tss.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste pentru demonstrarea comportamentului mutanților.
 * 
 * Acest fișier conține teste care demonstrează:
 * 1. Mutantul echivalent produce aceleași rezultate ca originalul
 * 2. Mutantul ne-echivalent "killed" este detectat de teste
 * 3. Mutantul ne-echivalent "survived" nu este detectat de anumite teste
 */
@DisplayName("Teste Mutanți")
class MutantTest {

    private DeliveryService original;
    private DeliveryServiceMutantEquivalent mutantEquivalent;
    private DeliveryServiceMutantKilled mutantKilled;
    private DeliveryServiceMutantSurvived mutantSurvived;

    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        original = new DeliveryService();
        mutantEquivalent = new DeliveryServiceMutantEquivalent();
        mutantKilled = new DeliveryServiceMutantKilled();
        mutantSurvived = new DeliveryServiceMutantSurvived();
    }

    // ==================== TESTE MUTANT ECHIVALENT ====================

    @Nested
    @DisplayName("Mutant Echivalent - Comportament identic cu originalul")
    class EquivalentMutantTests {

        @Test
        @DisplayName("Mutant echivalent: w <= 2 kg produce același rezultat")
        void testEquivalentMutant_LightWeight() {
            // Testăm cazul unde mutația este activă (weightKg <= 2)
            double originalResult = original.calculateDeliveryFee(5, 1);
            double mutantResult = mutantEquivalent.calculateDeliveryFee(5, 1);
            
            assertEquals(originalResult, mutantResult, DELTA,
                    "Mutantul echivalent trebuie să producă același rezultat");
            assertEquals(7.50, mutantResult, DELTA);
        }

        @Test
        @DisplayName("Mutant echivalent: toate greutățile <= 2 kg")
        void testEquivalentMutant_AllLightWeights() {
            // Testăm mai multe valori în intervalul afectat de mutație
            double[] weights = {0.01, 0.5, 1.0, 1.5, 1.99, 2.0};
            
            for (double w : weights) {
                double originalResult = original.calculateDeliveryFee(5, w);
                double mutantResult = mutantEquivalent.calculateDeliveryFee(5, w);
                assertEquals(originalResult, mutantResult, DELTA,
                        "Mutant echivalent pentru w = " + w);
            }
        }

        @Test
        @DisplayName("Mutant echivalent: comportament identic și pentru alte greutăți")
        void testEquivalentMutant_OtherWeights() {
            // Verificăm că nu am afectat alte părți ale codului
            assertEquals(original.calculateDeliveryFee(5, 3),
                    mutantEquivalent.calculateDeliveryFee(5, 3), DELTA, "w = 3");
            assertEquals(original.calculateDeliveryFee(5, 10),
                    mutantEquivalent.calculateDeliveryFee(5, 10), DELTA, "w = 10");
            assertEquals(original.calculateDeliveryFee(5, 20),
                    mutantEquivalent.calculateDeliveryFee(5, 20), DELTA, "w = 20");
        }
    }

    // ==================== TESTE MUTANT KILLED ====================

    @Nested
    @DisplayName("Mutant Killed - Detectat de test BVA_D5")
    class KilledMutantTests {

        @Test
        @DisplayName("Test care OMOARĂ mutantul: d = 10.0 km exact")
        void testMutantKilled_ExactBoundary() {
            // Testul BVA_D5: d = 10.0, w = 1.0
            double originalResult = original.calculateDeliveryFee(10.0, 1.0);
            double mutantResult = mutantKilled.calculateDeliveryFee(10.0, 1.0);
            
            // Original: 10.0 <= 10 este TRUE → distanceFee = 10 * 0.50 = 5.00
            // Mutant:   10.0 < 10 este FALSE → distanceFee = 10 * 0.40 = 4.00
            assertEquals(10.00, originalResult, DELTA, "Original corect");
            assertEquals(9.00, mutantResult, DELTA, "Mutant diferit");
            
            // Demonstrăm că sunt diferite
            assertNotEquals(originalResult, mutantResult, DELTA,
                    "Mutantul produce rezultat diferit → este OMORÂT");
        }

        @Test
        @DisplayName("Demonstrație: testul BVA_D5 omoară mutantul")
        void demonstrateMutantKilled() {
            // Acesta este testul BVA_D5 care ar trebui să eșueze pe mutant
            double expected = 10.00;  // Rezultatul corect
            double mutantActual = mutantKilled.calculateDeliveryFee(10.0, 1.0);
            
            // Acest assert ar EȘUA dacă am testa mutantul
            // assertEquals(expected, mutantActual, DELTA); // WOULD FAIL!
            
            assertNotEquals(expected, mutantActual, DELTA,
                    "Mutantul returnează 9.00 în loc de 10.00 → KILLED");
        }

        @Test
        @DisplayName("Mutant killed: valori aproape de limită funcționează diferit")
        void testMutantKilled_NearBoundary() {
            // La d = 9.99, ambele ar trebui să fie egale (sub limită)
            assertEquals(original.calculateDeliveryFee(9.99, 1.0),
                    mutantKilled.calculateDeliveryFee(9.99, 1.0), DELTA,
                    "Sub limită: comportament identic");
            
            // La d = 10.01, ambele ar trebui să fie egale (peste limită)
            assertEquals(original.calculateDeliveryFee(10.01, 1.0),
                    mutantKilled.calculateDeliveryFee(10.01, 1.0), DELTA,
                    "Peste limită: comportament identic");
            
            // DOAR la d = 10.0 exact diferă
            assertNotEquals(original.calculateDeliveryFee(10.0, 1.0),
                    mutantKilled.calculateDeliveryFee(10.0, 1.0), DELTA,
                    "PE limită: comportament diferit!");
        }
    }

    // ==================== TESTE MUTANT SURVIVED ====================

    @Nested
    @DisplayName("Mutant Survived - NU este detectat de testul EP_D2")
    class SurvivedMutantTests {

        @Test
        @DisplayName("Test EP_D2 care NU omoară mutantul: d = 5, w = 1")
        void testMutantSurvived_EP_D2() {
            // Testul EP_D2: d = 5, w = 1
            double originalResult = original.calculateDeliveryFee(5, 1);
            double mutantResult = mutantSurvived.calculateDeliveryFee(5, 1);
            
            // Ambele returnează același rezultat pentru d = 5
            // deoarece 5 > 0 și 5 < 0 sunt ambele diferite
            assertEquals(originalResult, mutantResult, DELTA,
                    "Mutantul SUPRAVIEȚUIEȘTE acestui test");
            assertEquals(7.50, mutantResult, DELTA);
        }

        @Test
        @DisplayName("Demonstrație: testul EP_D2 NU omoară mutantul")
        void demonstrateMutantSurvived() {
            // Testul EP_D2 verifică:
            double expected = 7.50;
            double mutantActual = mutantSurvived.calculateDeliveryFee(5, 1);
            
            // Acest assert TRECE - mutantul supraviețuiește!
            assertEquals(expected, mutantActual, DELTA,
                    "Mutantul returnează același rezultat → SURVIVED");
        }

        @Test
        @DisplayName("Mutant survived: diferă DOAR pentru d = 0")
        void testMutantSurvived_DiffersOnlyAtZero() {
            // Original: d = 0 → Exception
            assertThrows(IllegalArgumentException.class,
                    () -> original.calculateDeliveryFee(0, 1),
                    "Original aruncă excepție pentru d = 0");
            
            // Mutant: d = 0 → NU aruncă excepție (0 < 0 este FALSE)
            assertDoesNotThrow(
                    () -> mutantSurvived.calculateDeliveryFee(0, 1),
                    "Mutantul NU aruncă excepție pentru d = 0");
            
            // Mutantul calculează un rezultat pentru d = 0
            double mutantResult = mutantSurvived.calculateDeliveryFee(0, 1);
            // distanceFee = 0 * 0.50 = 0, weightFee = 0, baseFee = 5
            assertEquals(5.00, mutantResult, DELTA,
                    "Mutantul calculează 5.00 RON pentru d = 0");
        }

        @Test
        @DisplayName("Test care AR OMORA mutantul (dacă l-am include)")
        void testThatWouldKillMutant() {
            // Acest test demonstrează că mutantul POATE fi omorât
            // de un test care verifică explicit d = 0
            
            // Dacă am adăuga acest test în suita noastră:
            assertThrows(IllegalArgumentException.class,
                    () -> original.calculateDeliveryFee(0, 1));
            
            // Pe mutant, acest test ar EȘUA:
            // assertThrows ar arunca AssertionError deoarece
            // mutantul NU aruncă excepție pentru d = 0
            
            // Demonstrăm diferența:
            try {
                double result = mutantSurvived.calculateDeliveryFee(0, 1);
                // Mutantul a returnat un rezultat în loc să arunce excepție
                assertNotNull(result, "Mutantul returnează valoare în loc de excepție");
            } catch (IllegalArgumentException e) {
                fail("Mutantul NU ar trebui să arunce excepție pentru d = 0");
            }
        }
    }

    // ==================== SUMAR COMPARATIV ====================

    @Nested
    @DisplayName("Sumar Comparativ - Toate mutațiile")
    class ComparativeSummary {

        @Test
        @DisplayName("Tabel comparativ pentru d = 10, w = 1")
        void comparativeTable_d10_w1() {
            double d = 10.0, w = 1.0;
            
            double orig = original.calculateDeliveryFee(d, w);
            double equiv = mutantEquivalent.calculateDeliveryFee(d, w);
            double killed = mutantKilled.calculateDeliveryFee(d, w);
            double survived = mutantSurvived.calculateDeliveryFee(d, w);
            
            System.out.println("=== Comparație pentru d=" + d + ", w=" + w + " ===");
            System.out.println("Original:           " + orig + " RON");
            System.out.println("Mutant Echivalent:  " + equiv + " RON");
            System.out.println("Mutant Killed:      " + killed + " RON");
            System.out.println("Mutant Survived:    " + survived + " RON");
            
            assertEquals(orig, equiv, DELTA, "Echivalent = Original");
            assertNotEquals(orig, killed, DELTA, "Killed ≠ Original");
            assertEquals(orig, survived, DELTA, "Survived = Original (pentru acest input)");
        }

        @Test
        @DisplayName("Tabel comparativ pentru d = 5, w = 1")
        void comparativeTable_d5_w1() {
            double d = 5.0, w = 1.0;
            
            double orig = original.calculateDeliveryFee(d, w);
            double equiv = mutantEquivalent.calculateDeliveryFee(d, w);
            double killed = mutantKilled.calculateDeliveryFee(d, w);
            double survived = mutantSurvived.calculateDeliveryFee(d, w);
            
            System.out.println("=== Comparație pentru d=" + d + ", w=" + w + " ===");
            System.out.println("Original:           " + orig + " RON");
            System.out.println("Mutant Echivalent:  " + equiv + " RON");
            System.out.println("Mutant Killed:      " + killed + " RON");
            System.out.println("Mutant Survived:    " + survived + " RON");
            
            // Pentru acest input, toți returnează același rezultat
            assertEquals(orig, equiv, DELTA);
            assertEquals(orig, killed, DELTA);
            assertEquals(orig, survived, DELTA);
        }
    }
}

