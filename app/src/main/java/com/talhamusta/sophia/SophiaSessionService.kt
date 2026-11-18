package com.talhamusta.sophia
import android.content.Context
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
class SophiaSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle): VoiceInteractionSession = SophiaSession(this)
}
class SophiaSession(context: Context) : VoiceInteractionSession(context)
