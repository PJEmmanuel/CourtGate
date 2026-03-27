package com.example.courtgate.framework.remote

import com.google.firebase.firestore.PropertyName

//get y set es traductor para camelCase y snake_case
data class ScheduleDTO(
    @get:PropertyName("default_hours")// serializar
    @set:PropertyName("default_hours")// deserializar
    var defaultHours : List<String> = emptyList()
)
