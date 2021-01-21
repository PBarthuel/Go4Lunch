package paul.barthuel.go4lunch

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import paul.barthuel.go4lunch.data.firestore.user.UserRepository

class MainViewModel(private val mAuth: FirebaseAuth, private val mUserRepository: UserRepository) : ViewModel() {
    fun createUser() {
        if (mAuth.currentUser != null && mAuth.currentUser!!.photoUrl != null) {
            mUserRepository.createUser(mAuth.currentUser!!.uid,
                    mAuth.currentUser!!.displayName,
                    mAuth.currentUser!!.photoUrl.toString())
        }
    }

}