package com.jgchk.hotelhavoc.features.menu

import android.arch.lifecycle.MutableLiveData
import com.jgchk.hotelhavoc.core.platform.BaseViewModel
import javax.inject.Inject

class MenuViewModel
@Inject constructor(private val authenticator: Authenticator) : BaseViewModel() {

    val loggedIn = MutableLiveData<Boolean>().apply { postValue(authenticator.userLoggedIn()) }

    fun logout() {
        loggedIn.value = false
    }

    fun login() {
        loggedIn.value = true
    }

}