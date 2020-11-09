package paul.barthuel.go4lunch.data.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserSearchRepository {

    private static UserSearchRepository sInstance;

    public static UserSearchRepository getInstance() {
        if(sInstance == null) {
            sInstance = new UserSearchRepository();
        }
        return sInstance;
    }

    private final MutableLiveData<String> userSearchQueryLiveData = new MutableLiveData<>();

    private UserSearchRepository() {

    }

    public void updateSearchQuery(String newText) {
        userSearchQueryLiveData.setValue(newText);
    }

    public LiveData<String> getUserSearchQueryLiveData() {
        return userSearchQueryLiveData;
    }
}
