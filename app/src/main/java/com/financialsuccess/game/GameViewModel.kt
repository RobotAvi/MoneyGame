package com.financialsuccess.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EventPanelState(
    val title: String,
    val message: String,
    val primaryText: String? = null,
    val onPrimary: (() -> Unit)? = null,
    val secondaryText: String? = null,
    val onSecondary: (() -> Unit)? = null
)

class GameViewModel : ViewModel() {
    private val _eventPanel = MutableStateFlow<EventPanelState?>(null)
    val eventPanel: StateFlow<EventPanelState?> = _eventPanel

    fun showEventPanel(state: EventPanelState) { _eventPanel.value = state }
    fun hideEventPanel() { _eventPanel.value = null }
}