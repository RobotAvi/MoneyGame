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
} 