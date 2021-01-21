package paul.barthuel.go4lunch.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import paul.barthuel.go4lunch.R

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)
        val workmateId = intent.getStringExtra(KEY_WORKMATE_ID)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.chat_container, ChatFragment.newInstance(workmateId))
                    .commitNow()
        }
    }

    companion object {
        private const val KEY_WORKMATE_ID = "KEY_WORKMATE_ID"
        @JvmStatic
        fun navigate(context: Context?, workmateId: String?): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(KEY_WORKMATE_ID, workmateId)
            return intent
        }
    }
}