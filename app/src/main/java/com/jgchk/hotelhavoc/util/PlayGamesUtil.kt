package com.jgchk.hotelhavoc.util

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.*
import com.google.android.gms.games.multiplayer.Invitation
import com.google.android.gms.games.multiplayer.InvitationCallback
import com.google.android.gms.games.multiplayer.Multiplayer
import com.google.android.gms.games.multiplayer.Participant
import com.google.android.gms.games.multiplayer.realtime.*
import com.google.android.gms.tasks.OnFailureListener
import com.jgchk.hotelhavoc.R

class PlayGamesUtil(val context: Context, val callbacks: PlayGamesCallbacks) {

    companion object {
        private val TAG = PlayGamesUtil::class.qualifiedName
    }

    private val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
    private var realTimeMultiplayerClient: RealTimeMultiplayerClient? = null
    private var invitationsClient: InvitationsClient? = null

    private lateinit var signedInAccount: GoogleSignInAccount
    private lateinit var playerId: String

    private var incomingInvitationId: String? = null
    private var roomConfig: RoomConfig? = null
    private var roomId: String? = null
    private var participants: ArrayList<Participant>? = null
    private var myId: String? = null

    private val participantScores = HashMap<String, Int>()
    private val finishedParticipants = HashSet<String>()

    private var invitationCallback: InvitationCallback = object : InvitationCallback() {
        override fun onInvitationReceived(invitation: Invitation) {
            // We got an invitation to play a game! So, store it in
            // incomingInvidationId
            // and show the popup on screen
            incomingInvitationId = invitation.invitationId
            callbacks.onInvitationReceived(invitation)
        }

        override fun onInvitationRemoved(invitationId: String) {
            if (incomingInvitationId.equals(invitationId) && incomingInvitationId != null) {
                incomingInvitationId = null
                callbacks.onInvitationRemoved(invitationId)
            }
        }
    }

    fun signInSilently() {
        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInSilently(): success")
                onConnected(task.result!!)
            } else {
                Log.d(TAG, "signInSilently(): failure", task.exception)
                onDisconnected()
            }
        }
    }

    fun unregisterListeners() {
        invitationsClient?.unregisterInvitationCallback(invitationCallback)
    }

    fun onConnected(googleSignInAccount: GoogleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs")
        if (signedInAccount != googleSignInAccount) {

            signedInAccount = googleSignInAccount

            // update the clients
            realTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(context, googleSignInAccount)
            invitationsClient = Games.getInvitationsClient(context, googleSignInAccount)

            // get the playerId from the PlayersClient
            val playersClient = Games.getPlayersClient(context, googleSignInAccount)
            playersClient.currentPlayer
                .addOnSuccessListener { player -> playerId = player.playerId }
                .addOnFailureListener(createFailureListener("There was a problem getting the player id!"))
        }

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        invitationsClient?.registerInvitationCallback(invitationCallback)

        // get the invitation from the connection hint
        // retrieve the TurnBasedMatch from the connectionHint
        val gamesClient = Games.getGamesClient(context, googleSignInAccount)
        gamesClient.activationHint
            .addOnSuccessListener { hint ->
                if (hint != null) {
                    val invitation: Invitation? = hint.getParcelable(Multiplayer.EXTRA_INVITATION)
                    if (invitation != null && invitation.invitationId != null) {
                        Log.d(TAG, "onConnected(): connection hint has a room invite!")
                        acceptInviteToRoom(invitation.invitationId)
                    }
                }
            }
            .addOnFailureListener(createFailureListener("There was a problem getting the activation hint!"))
    }

    fun onDisconnected() {
        Log.d(TAG, "onDisconnected()")

        realTimeMultiplayerClient = null
        invitationsClient = null

        callbacks.onDisconnected()
    }

    private val roomStatusUpdateCallback = object : RoomStatusUpdateCallback() {

        // Called when we are connected to the room. We're not ready to play yet!
        // (maybe not everyone is connected yet)
        override fun onConnectedToRoom(room: Room?) {
            Log.d(TAG, "onConnectedToRoom($room)")

            // get participants and my id
            participants = room?.participants
            myId = room?.getParticipantId(playerId)

            // save room id if its not initialized in onRoomCreated() so we can leave cleanly before the game starts
            if (roomId == null) {
                roomId = room?.roomId
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: $roomId")
            Log.d(TAG, "My ID: $myId")
            Log.d(TAG, "<< CONNECTED TO ROOM >>")
        }

        // Called when we get disconnected from the room
        override fun onDisconnectedFromRoom(room: Room?) {
            roomId = null
            roomConfig = null
            callbacks.onGameError()
        }

        override fun onPeerDeclined(room: Room?, peers: MutableList<String>) {
            updateRoom(room)
        }

        override fun onPeerInvitedToRoom(room: Room?, peers: MutableList<String>) {
            updateRoom(room)
        }

        override fun onP2PConnected(participant: String) {}

        override fun onP2PDisconnected(participant: String) {}

        override fun onPeerJoined(room: Room?, peers: MutableList<String>) {
            updateRoom(room)
        }

        override fun onPeerLeft(room: Room?, peers: MutableList<String>) {
            updateRoom(room)
        }

        override fun onRoomAutoMatching(room: Room?) {
            updateRoom(room)
        }

        override fun onRoomConnecting(room: Room?) {
            updateRoom(room)
        }

        override fun onPeersConnected(room: Room?, peers: MutableList<String>) {
            updateRoom(room)
        }

        override fun onPeersDisconnected(room: Room?, peers: MutableList<String>) {
            updateRoom(room)
        }
    }

    private val roomUpdateCallback = object : RoomUpdateCallback() {

        // called when room has been created
        override fun onRoomCreated(statusCode: Int, room: Room?) {
            Log.d(TAG, "onRoomCreated($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status $statusCode")
                callbacks.onGameError()
                return
            }

            // save room id so we can leave cleanly before the game starts
            roomId = room?.roomId

            // show the waiting room UI
            callbacks.showWaitingRoom(room)
        }

        // called when room is fully connected
        override fun onRoomConnected(statusCode: Int, room: Room?) {
            Log.d(TAG, "onRoomConnected($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status $statusCode")
                callbacks.onGameError()
                return
            }

            updateRoom(room)
        }

        override fun onJoinedRoom(statusCode: Int, room: Room?) {
            Log.d(TAG, "onJoinedRoom($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status $statusCode")
                callbacks.onGameError()
                return
            }

            // show the waiting room UI
            callbacks.showWaitingRoom(room)
        }

        // Called when we've successfully left the room (this happens
        // as a result of voluntarily leaving via leaveRoom()
        // If we get disconnected, we get onDisconnectedFromRoom()
        override fun onLeftRoom(statusCode: Int, roomId: String) {
            // we have left the room, return to main screen
            Log.d(TAG, "onLeftRoom, code $statusCode")
            callbacks.onLeftRoom()
        }
    }

    fun updateRoom(room: Room?) {
        participants = room?.participants
        participants?.let { callbacks.updatePeerScoresDisplay(it, participantScores) }
    }

    private val onRealTimeMessageReceivedListener = OnRealTimeMessageReceivedListener { realTimeMessage ->
        val buf = realTimeMessage.messageData
        val sender = realTimeMessage.senderParticipantId
        Log.d(TAG, "Message received: ${buf[0].toChar()}/${buf[1].toInt()}")

        if (buf[0].toChar() == 'F' || buf[0].toChar() == 'U') {
            // score update
            val existingScore = participantScores.getOrDefault(sender, 0)
            val thisScore = buf[1].toInt()
            if (thisScore > existingScore) {
                // this check is necessary because packets may arrive out of order, so we
                // should only ever consider the highest score we received, as we know in
                // our game there is no way to lose points. If there was a way to lose
                // points, we'd have to add a "serial number" to the packet
                participantScores[sender] = thisScore
            }

            // update the scores on the screen
            participants?.let { callbacks.updatePeerScoresDisplay(it, participantScores) }

            // if it's the final score, mark this participant as having finished the game
            if (buf[0].toChar() == 'F') {
                finishedParticipants.add(realTimeMessage.senderParticipantId)
            }
        }
    }

    fun acceptInviteToRoom(invitationId: String) {
        Log.d(TAG, "Accepting invitation: $invitationId")

        roomConfig = RoomConfig.builder(roomUpdateCallback)
            .setInvitationIdToAccept(invitationId)
            .setOnMessageReceivedListener(onRealTimeMessageReceivedListener)
            .setRoomStatusUpdateCallback(roomStatusUpdateCallback)
            .build()

        // TODO: waiting screen
        // TODO: reset game vars

        realTimeMultiplayerClient?.join(roomConfig!!)
            ?.addOnSuccessListener { Log.d(TAG, "Room joined successfully!") }
    }

    private fun createFailureListener(string: String): OnFailureListener {
        return OnFailureListener { exception -> handleException(exception, string) }
    }

    private fun handleException(exception: Exception, details: String) {

        var status = 0
        if (exception is ApiException) {
            status = exception.statusCode
        }

        lateinit var errorString: String
        when (status) {
            GamesCallbackStatusCodes.OK -> return
            GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER ->
                errorString = context.getString(R.string.status_multiplayer_error_not_trusted_tester)
            GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED ->
                errorString = context.getString(R.string.match_error_already_rematched)
            GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED ->
                errorString = context.getString(R.string.network_error_operation_failed)
            GamesClientStatusCodes.INTERNAL_ERROR ->
                errorString = context.getString(R.string.internal_error)
            GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH ->
                errorString = context.getString(R.string.match_error_inactive_match)
            GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED ->
                errorString = context.getString(R.string.match_error_locally_modified)
            else ->
                errorString = context.getString(
                    R.string.unexpected_status,
                    GamesClientStatusCodes.getStatusCodeString(status)
                )
        }

        val message = context.getString(R.string.status_exception_error, details, status, exception)
        Log.e(TAG, message + "\n" + errorString)
    }
}