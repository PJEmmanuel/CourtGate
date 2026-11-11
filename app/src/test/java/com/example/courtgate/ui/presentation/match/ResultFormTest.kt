package com.example.courtgate.ui.presentation.match

import com.example.courtgate.core.lastResultTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResultFormTest {

    private fun form(vararg sets: Pair<String, String>) = ResultForm(
        sets = List(SETS_PER_MATCH) { index ->
            sets.getOrNull(index)?.let { (p1, p2) -> SetScoreInput(p1, p2) } ?: SetScoreInput()
        }
    )

    @Test
    fun `two sets won by the same pair is valid`() {
        assertNull(form("6" to "4", "7" to "5").validate())
    }

    @Test
    fun `three sets with a split in the first two is valid`() {
        assertNull(form("6" to "4", "3" to "6", "7" to "6").validate())
    }

    @Test
    fun `missing second set is MissingSets`() {
        assertEquals(ResultFormError.MissingSets, form("6" to "4").validate())
    }

    @Test
    fun `half filled third set is MissingSets`() {
        assertEquals(ResultFormError.MissingSets, form("6" to "4", "3" to "6", "6" to "").validate())
    }

    @Test
    fun `more than seven games is InvalidGames`() {
        assertEquals(ResultFormError.InvalidGames, form("8" to "6", "6" to "4").validate())
    }

    @Test
    fun `tied set is TiedSet`() {
        assertEquals(ResultFormError.TiedSet, form("6" to "6", "6" to "4").validate())
    }

    @Test
    fun `third set after winning the first two is ThirdSetNotNeeded`() {
        assertEquals(
            ResultFormError.ThirdSetNotNeeded,
            form("6" to "4", "6" to "2", "6" to "1").validate()
        )
    }

    @Test
    fun `third set not played is saved as 0-0`() {
        val result = form("6" to "4", "6" to "2").toLastResult()

        assertEquals(lastResultTest(0, 6, 4, 6, 2, 0, 0), result)
    }

    @Test
    fun `toForm and toLastResult round trip keeps the result`() {
        val saved = lastResultTest(3, 6, 4, 3, 6, 7, 5)

        assertEquals(saved, saved.toForm().toLastResult())
    }

    @Test
    fun `toForm leaves the third set empty when it was not played`() {
        val form = lastResultTest(3, 6, 4, 6, 2, 0, 0).toForm()

        assertEquals(SetScoreInput(), form.sets[2])
        assertEquals(false, form.isNew)
    }
}
