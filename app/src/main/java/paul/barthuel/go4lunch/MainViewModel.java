package paul.barthuel.go4lunch;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import paul.barthuel.go4lunch.data.firestore.user.UserRepository;

public class MainViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private UserRepository mUserRepository;

    public MainViewModel(FirebaseAuth mAuth, UserRepository mUserRepository) {
        this.mAuth = mAuth;
        this.mUserRepository = mUserRepository;
    }

    public void createUser() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            mUserRepository.createUser(mAuth.getCurrentUser().getUid(),
                    mAuth.getCurrentUser().getDisplayName(),
                    mAuth.getCurrentUser().getPhotoUrl().toString());
        }
    }
}
