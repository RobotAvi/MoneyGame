package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.models.Profession
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.ArgumentCaptor
import org.mockito.MockitoAnnotations
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.models.Dream
import com.financialsuccess.game.R
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotEquals

class ProfessionAdapterTest {
    private lateinit var adapter: ProfessionAdapter
    private lateinit var professions: List<Profession>
    private lateinit var mockCallback: (Profession) -> Unit

    @Before
    fun setUp() {
        professions = listOf(
            Profession("teacher", "Учитель", "desc", 1, 2, 3, "edu"),
            Profession("engineer", "Инженер", "desc", 1, 2, 3, "edu"),
            Profession("manager", "Менеджер", "desc", 1, 2, 3, "edu"),
            Profession("lawyer", "Юрист", "desc", 1, 2, 3, "edu")
        )
        mockCallback = mock<(Profession) -> Unit>()
        adapter = ProfessionAdapter(professions, mockCallback)
    }

    @Test
    fun testItemCount() {
        assertEquals(4, adapter.itemCount)
    }

    @Test
    fun testOnProfessionSelectedCallback() {
        // Симулируем клик по третьей профессии (менеджер)
        val viewHolder = adapter.onCreateViewHolder(mock(ViewGroup::class.java), 0)
        viewHolder.bind(professions[2], false)
        viewHolder.binding.cardProfession.performClick()
        val captor = ArgumentCaptor.forClass(Profession::class.java)
        verify(mockCallback, atLeastOnce()).invoke(captor.capture())
        assertEquals("Менеджер", captor.value.name)
    }

    @Test
    fun testSelectionState() {
        // После клика selectedPosition должен обновиться
        val viewHolder = adapter.onCreateViewHolder(mock(ViewGroup::class.java), 0)
        viewHolder.bind(professions[1], false)
        viewHolder.binding.cardProfession.performClick()
        // selectedPosition должен быть равен позиции инженера (1)
        // (Проверить напрямую нельзя, но можно проверить, что callback вызван с "Инженер")
        val captor = ArgumentCaptor.forClass(Profession::class.java)
        verify(mockCallback, atLeastOnce()).invoke(captor.capture())
        assertEquals("Инженер", captor.value.name)
    }

    @Test
    fun testAllProfessionsDisplayed() {
        val allProfessions = listOf(
            Profession("teacher", "Учитель", "desc", 1, 2, 3, "edu"),
            Profession("engineer", "Инженер", "desc", 1, 2, 3, "edu"),
            Profession("doctor", "Врач", "desc", 1, 2, 3, "edu"),
            Profession("manager", "Менеджер", "desc", 1, 2, 3, "edu"),
            Profession("mechanic", "Механик", "desc", 1, 2, 3, "edu"),
            Profession("lawyer", "Юрист", "desc", 1, 2, 3, "edu")
        )
        val adapter = ProfessionAdapter(allProfessions, mockCallback)
        assertEquals(6, adapter.itemCount)
    }

    @Test
    fun testAllDreamsHaveValidDrawable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dreams = GameDataManager.getDreams()
        for (dream in dreams) {
            val resId = when (dream.id) {
                "yacht" -> R.drawable.dream_yacht
                "restaurant" -> R.drawable.dream_restaurant
                "charity" -> R.drawable.dream_charity
                "island" -> R.drawable.dream_island
                "space_trip" -> R.drawable.dream_space
                "business_empire" -> R.drawable.dream_business
                else -> R.drawable.ic_dream_placeholder
            }
            val exists = try {
                context.resources.getDrawable(resId, null)
                true
            } catch (e: Exception) {
                false
            }
            assertNotEquals("Drawable for dream ${dream.id} not found!", R.drawable.ic_dream_placeholder, resId)
            assert(exists) { "Drawable resource for dream ${dream.id} does not exist!" }
        }
    }
} 