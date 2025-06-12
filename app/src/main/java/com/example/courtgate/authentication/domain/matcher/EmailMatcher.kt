package com.example.courtgate.authentication.domain.matcher

interface EmailMatcher {
    fun isValid(email : String) : Boolean
}