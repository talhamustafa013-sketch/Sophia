package com.talhamustafa.sophia

import android.app.Activity
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 36)
        }
        status = TextView(this).apply {
            textSize = 18f
            text = "Sophia is installed. Choose how to set your assistant."
        }
        val setAssistant = Button(this).apply {
            text = "Set Sophia as default assistant"
            setOnClickListener { requestAssistantRole() }
        }
        val openSettings = Button(this).apply {
            text = "Open Android assistant settings"
            setOnClickListener { openAssistantSettings() }
        }
        val testVoice = Button(this).apply {
            text = "Test Sophia's voice"
            setOnClickListener { speak("Hello. I'm Sophia. Your voice assistant is installed.") }
        }
        layout.addView(status)
        layout.addView(setAssistant)
        layout.addView(openSettings)
        layout.addView(testVoice)
        setContentView(layout)
    }

    private fun requestAssistantRole() {
        status.text = "Checking Android assistant support..."
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            status.text = "This Android version does not support the assistant role request. Opening settings."
            openAssistantSettings()
            return
        }
        try {
            val roles = getSystemService(RoleManager::class.java)
            if (roles == null) {
                status.text = "Android did not provide the assistant role service. Opening settings."
                openAssistantSettings()
                return
            }
            if (!roles.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
                status.text = "Your phone does not expose the assistant role request. Opening Android settings instead."
                openAssistantSettings()
                return
            }
            if (roles.isRoleHeld(RoleManager.ROLE_ASSISTANT)) {
                status.text = "Sophia is already your default assistant."
                speak(status.text.toString())
                return
            }
            status.text = "Opening Android's assistant selection..."
            startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT), 42)
        } catch (e: Exception) {
            status.text = "Could not open the selection popup: ${e.javaClass.simpleName}. Trying Android settings."
            openAssistantSettings()
        }
    }

    private fun openAssistantSettings() {
        val candidates = listOf(
            Settings.ACTION_VOICE_INPUT_SETTINGS,
            Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS
        )
        for (action in candidates) {
            try {
                startActivity(Intent(action))
                return
            } catch (_: ActivityNotFoundException) {
                // Try the next Android settings screen.
            } catch (_: Exception) {
                // Try the next Android settings screen.
            }
        }
        status.text = "Your phone does not provide an assistant settings screen. Open Settings and search for 'Default apps' or 'Digital assistant app'."
    }

    @Deprecated("Deprecated by Android; retained for device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42) {
            val roles = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getSystemService(RoleManager::class.java) else null
            status.text = if (roles?.isRoleHeld(RoleManager.ROLE_ASSISTANT) == true) {
                "Sophia is now your default assistant. Wake-word support still depends on device and system support."
            } else {
                "Assistant selection was cancelled or not completed. You can try Android assistant settings."
            }
            speak(status.text.toString())
        }
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault()
    }

    private fun speak(message: String) {
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "sophia-" + System.currentTimeMillis())
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}