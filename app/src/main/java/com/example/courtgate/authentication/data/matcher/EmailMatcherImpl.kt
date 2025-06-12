package com.example.courtgate.authentication.data.matcher

import android.util.Patterns
import com.example.courtgate.authentication.domain.matcher.EmailMatcher

class EmailMatcherImpl : EmailMatcher {
    override fun isValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}