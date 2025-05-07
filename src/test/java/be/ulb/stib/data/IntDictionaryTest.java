package be.ulb.stib.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class IntDictionaryTest {

    // ---------- Test 1 : aucune duplication ----------
    @Test
    void sameStrReturnsSameIdx() {
        IntDictionary dict = new IntDictionary();
        int a1 = dict.getOrAdd("DELIJN");
        int a2 = dict.getOrAdd("DELIJN");
        assertEquals(a1, a2);
        assertEquals(0, a1);
        assertEquals(1, dict.size());
    }

    // ---------- Test 2 : ajout ----------
    @Test
    void differentStrReturnDifferentIdx() {
        IntDictionary dict = new IntDictionary();
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
        IntDictionary dict = new IntDictionary();
        assertEquals(-1, dict.get("UNKNOWN"));
    }

    // ---------- Test 4 : mapping bidirectionnel ----------
    @Test
    void reverseLookupWorks() {
        IntDictionary dict = new IntDictionary();
        int idx = dict.getOrAdd("DELIJN");
        assertEquals("DELIJN", dict.get(idx));
    }

    // ---------- Test 5 : verrouillage dict ----------
    @Test
    void freezePreventsNewIds() {
        IntDictionary dict = new IntDictionary();
        dict.getOrAdd("DELIJN");
        dict.freeze();
        assertTrue(dict.isFrozen());
        assertThrows(IllegalStateException.class, () -> dict.getOrAdd("NEW"));
    }
}
