package com.jgchk.hotelhavoc.features.menu

import android.content.Context
import com.jgchk.hotelhavoc.core.exception.Failure
import com.jgchk.hotelhavoc.core.functional.Either
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

interface LoginRepository {
    fun lastLogin(): Either<Failure, GoogleSignInAccount>

    class Network
    @Inject constructor(private val context: Context) : LoginRepository {
        override fun lastLogin(): Either<Failure, GoogleSignInAccount> {
            val lastLogin = GoogleSignIn.getLastSignedInAccount(context)
            return if (lastLogin == null) {
                Either.Left(NoLoginFound())
            } else {
                Either.Right(lastLogin)
            }
        }
    }

    class NoLoginFound : Failure.FeatureFailure()
}