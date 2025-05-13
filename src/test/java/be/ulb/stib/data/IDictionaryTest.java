package be.ulb.stib.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class IDictionaryTest {

    // ---------- Test 1 : aucune duplication ----------
    @Test
    void sameStrReturnsSameIdx() {
        IDictionary dict = new IDictionary();
        int a1 = dict.getOrAdd("DELIJN");
        int a2 = dict.getOrAdd("DELIJN");
        assertEquals(a1, a2);
        assertEquals(0, a1);
        assertEquals(1, dict.size());
    }

    // ---------- Test 2 : ajout ----------
    @Test
    void differentStrReturnDifferentIdx() {
        IDictionary dict = new IDictionary();
        int delijn = dict.getOrAdd("DELIJN");
        int stib = dict.getOrAdd("STIB");
        assertNotEquals(delijn, stib);
        assertEquals(0, delijn);
        assertEquals(1, stib);
        assertEquals(2, dict.size());
    }

    // ---------- Test 3 : string inexistant ----------
    @Test
    void getReturnsNegativeWhenAbsent() {
        IDictionary dict = new IDictionary();
        assertEquals(-1, dict.get("UNKNOWN"));
    }

    // ---------- Test 4 : mapping bidirectionnel ----------
    @Test
    void reverseLookupWorks() {
        IDictionary dict = new IDictionary();
        int idx = dict.getOrAdd("DELIJN");
        assertEquals("DELIJN", dict.get(idx));
    }

    // ---------- Test 5 : verrouillage dict ----------
    @Test
    void freezePreventsNewIds() {
        IDictionary dict = new IDictionary();
        dict.getOrAdd("DELIJN");
        dict.freeze();
        assertTrue(dict.isFrozen());
        assertThrows(IllegalStateException.class, () -> dict.getOrAdd("NEW"));
    }
}
