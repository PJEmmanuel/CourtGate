package com.example.courtgate.framework.remote

import com.google.firebase.firestore.PropertyName

data class ScheduleDTO(
    @get:PropertyName("default_hours")//TODO: Estudiar esto
    @set:PropertyName("default_hours")
    var defaultHours : List<String> = emptyList()
)
