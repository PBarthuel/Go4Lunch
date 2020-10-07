package paul.barthuel.go4lunch.data.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserSearchRepository {

    public static UserSearchRepository getInstance() {
        return new UserSearchRepository();
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
