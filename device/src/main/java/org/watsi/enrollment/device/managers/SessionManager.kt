package org.watsi.enrollment.device.managers

import io.reactivex.Completable
import org.watsi.enrollment.domain.entities.AuthenticationToken

interface SessionManager {
    fun login(username: String, password: String): Completable
    fun logout()
    fun currentAuthenticationToken(): AuthenticationToken?
    fun shouldClearUserData(): Boolean

    class PermissionException : Exception()

    companion object {
        val ALLOWED_ROLES = listOf("enrollment")
    }
}
