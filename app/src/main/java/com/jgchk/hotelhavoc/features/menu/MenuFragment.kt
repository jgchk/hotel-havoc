/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jgchk.hotelhavoc.features.menu

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.core.extension.observe
import com.jgchk.hotelhavoc.core.extension.viewModel
import com.jgchk.hotelhavoc.core.navigation.Navigator
import com.jgchk.hotelhavoc.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_menu.*
import javax.inject.Inject

class MenuFragment : BaseFragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var menuViewModel: MenuViewModel

    companion object {
        private val TAG = MenuFragment::class.qualifiedName
        private const val RC_SIGN_IN = 6701
    }

    override fun layoutId() = R.layout.fragment_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        menuViewModel = viewModel(viewModelFactory) {
            observe(loggedIn, ::handleLoginStateChange)
        }
    }

    private fun attemptLogin() {
        val googleSignInClient = GoogleSignIn.getClient(activity!!, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun attemptLogout() {
        val googleSignInClient = GoogleSignIn.getClient(activity!!, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        googleSignInClient.signOut()
                .addOnSuccessListener { menuViewModel.logout() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener { menuViewModel.login() }
        }
    }

    private fun handleLoginStateChange(loggedIn: Boolean?) {
        when (loggedIn) {
            false, null -> showSinglePlayerMode()
            true -> showMultiplayerMode()
        }
    }

    private fun showSinglePlayerMode() {
        startgame_btn.text = "Single Player"
        startgame_btn.setOnClickListener { navigator.showGame(context!!) }

        login_btn.text = "Multiplayer Login"
        login_btn.setOnClickListener { attemptLogin() }
    }

    private fun showMultiplayerMode() {
        startgame_btn.text = "Start Game"
        startgame_btn.setOnClickListener { navigator.showGame(context!!) } // TODO: google play games

        login_btn.text = "Logout"
        login_btn.setOnClickListener { attemptLogout() }
    }
}
