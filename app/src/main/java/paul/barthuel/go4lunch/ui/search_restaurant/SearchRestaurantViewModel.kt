package paul.barthuel.go4lunch.ui.search_restaurant

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import paul.barthuel.go4lunch.data.firestore.user.UserRepository
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser
import paul.barthuel.go4lunch.data.local.ActualLocationRepository
import paul.barthuel.go4lunch.data.local.UserSearchRepository
import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete
import paul.barthuel.go4lunch.data.model.autocomplet.Prediction
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository
import java.util.*

class SearchRestaurantViewModel(private val autocompleteRepository: AutocompleteRepository,
                                actualLocationRepository: ActualLocationRepository,
                                private val userRepository: UserRepository,
                                private val userSearchRepository: UserSearchRepository) : ViewModel() {
    private val predictionsMediatorLiveData = MediatorLiveData<List<Prediction>>()
    private val selectedQueryLiveData = MutableLiveData<String>()
    val uiModelsMediatorLiveData = MediatorLiveData<List<String>>()

    private fun combineForPredictions(location: Location?, userSearchQuery: String?) {
        if (location != null && userSearchQuery != null) {
            predictionsMediatorLiveData.addSource(autocompleteRepository.getAutocompleteForLocation(userSearchQuery, location)) { autocomplete: Autocomplete -> predictionsMediatorLiveData.setValue(autocomplete.predictions) }
        }
    }

    private fun combineForUserSearchQuery(userSearchQuery: String?,
                                          predictions: List<Prediction>?,
                                          selectedQuery: String?) {
        if (userSearchQuery == null || predictions == null) {
            return
        }
        if (userSearchQuery == selectedQuery || userSearchQuery.isEmpty()) {
            uiModelsMediatorLiveData.value = ArrayList()
            Log.d("courgette", "combineForUserSearchQuery() called with: userSearchQuery = [$userSearchQuery], selectedQuery = [$selectedQuery]")
        } else {
            val autocompleteResults: MutableList<String> = ArrayList()
            for (prediction in predictions) {
                autocompleteResults.add(prediction.structuredFormatting.mainText)
            }
            uiModelsMediatorLiveData.setValue(autocompleteResults)
        }
    }

    val currentTodayUserLiveData: LiveData<TodayUser>?
        get() = if (FirebaseAuth.getInstance().currentUser != null) {
            userRepository.getTodayUser(FirebaseAuth.getInstance().currentUser!!.uid)
        } else {
            null
        }

    fun onSearchQueryChange(newText: String?) {
        userSearchRepository.updateSearchQuery(newText)
    }

    fun onAutocompleteSelected(selectedText: String) {
        selectedQueryLiveData.value = selectedText
    }

    init {
        val userSearchQueryLiveData = userSearchRepository.userSearchQueryLiveData
        val locationLiveData = actualLocationRepository.locationLiveData
        predictionsMediatorLiveData.addSource(locationLiveData) { location: Location? ->
            combineForPredictions(
                    location,
                    userSearchQueryLiveData.value)
        }
        uiModelsMediatorLiveData.addSource(userSearchQueryLiveData) { userSearchQuery: String? ->
            combineForUserSearchQuery(
                    userSearchQuery,
                    predictionsMediatorLiveData.value,
                    selectedQueryLiveData.value)
        }
        uiModelsMediatorLiveData.addSource(selectedQueryLiveData) { selectedQuery: String? ->
            combineForUserSearchQuery(
                    userSearchQueryLiveData.value,
                    predictionsMediatorLiveData.value,
                    selectedQuery)
        }
        uiModelsMediatorLiveData.addSource(predictionsMediatorLiveData) { predictions: List<Prediction>? ->
            combineForUserSearchQuery(
                    userSearchQueryLiveData.value,
                    predictions,
                    selectedQueryLiveData.value)
        }
    }
}