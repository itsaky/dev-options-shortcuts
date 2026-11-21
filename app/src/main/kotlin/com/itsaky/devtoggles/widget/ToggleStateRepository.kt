package com.itsaky.devtoggles.widget

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ToggleStateRepository {

    private val _wadbAddress = MutableStateFlow<String?>(null)
    val wadbAddress: StateFlow<String?> = _wadbAddress.asStateFlow()

    fun setWadbAddress(address: String?) {
        _wadbAddress.value = address
    }
}
