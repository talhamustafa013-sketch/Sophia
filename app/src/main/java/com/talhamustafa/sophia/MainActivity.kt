package com.talhamustafa.sophia
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
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
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(36, 48, 36, 36) }
        status = TextView(this).apply { textSize = 20f; text = "Sophia is ready to be configured." }
        val setAssistant = Button(this).apply { text = "Set Sophia as default assistant"; setOnClickListener { requestAssistantRole() } }
        val testVoice = Button(this).apply { text = "Test Sophia's voice"; setOnClickListener { speak("Hello. I'm Sophia. Your voice assistant is installed.") } }
        layout.addView(status); layout.addView(setAssistant); layout.addView(testVoice); setContentView(layout)
    }
    private fun requestAssistantRole() {
        val roles = getSystemService(RoleManager::class.java)
        if (roles == null || !roles.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) { status.text = "This Android build does not expose the assistant role to this app."; return }
        if (roles.isRoleHeld(RoleManager.ROLE_ASSISTANT)) { status.text = "Sophia is already your default assistant."; return }
        startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT), 42)
    }
    @Deprecated("Deprecated by Android; retained for device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42) {
            val roles = getSystemService(RoleManager::class.java)
            status.text = if (roles?.isRoleHeld(RoleManager.ROLE_ASSISTANT) == true) "Sophia is now your default assistant. Wake-word support still depends on device/system support." else "Assistant selection was not completed."
            speak(status.text.toString())
        }
    }
    override fun onInit(statusCode: Int) { if (statusCode == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault() }
    private fun speak(message: String) { tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "sophia-" + System.currentTimeMillis()) }
    override fun onDestroy() { tts?.stop(); tts?.shutdown(); super.onDestroy() }
}
