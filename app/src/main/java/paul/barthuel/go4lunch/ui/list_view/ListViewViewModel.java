package paul.barthuel.go4lunch.ui.list_view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListViewViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListViewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ListView fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}