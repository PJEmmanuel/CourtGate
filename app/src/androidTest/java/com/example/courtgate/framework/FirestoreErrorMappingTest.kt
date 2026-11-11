package com.example.courtgate.framework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.courtgate.ResultManage
import com.example.courtgate.domain.models.DomainError
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

// FirebaseFirestoreException.Code necesita clases de Android, así que este caso no puede ir
// en src/test. No hay conexión con Firebase: solo se crean excepciones.
@RunWith(AndroidJUnit4::class)
class FirestoreErrorMappingTest {

    @Test
    fun firestoreCall_mapsFirestoreCodes() = runBlocking {
        val cases = mapOf(
            Code.PERMISSION_DENIED to DomainError.Remote.AccessDenied,
            Code.NOT_FOUND to DomainError.Remote.NotFound,
            Code.UNAVAILABLE to DomainError.Remote.ServerError,
            Code.INTERNAL to DomainError.Remote.ServerError,
            Code.ABORTED to DomainError.Remote.UnknownRemoteError,
        )
        cases.forEach { (code, expected) ->
            val result = firestoreCall<Unit> { throw FirebaseFirestoreException("test", code) }
            assertEquals("code $code", ResultManage.Failure(expected), result)
        }
    }
}
