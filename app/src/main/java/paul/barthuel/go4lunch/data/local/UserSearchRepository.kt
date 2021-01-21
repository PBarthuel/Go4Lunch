package paul.barthuel.go4lunch.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UserSearchRepository private constructor() {
    val userSearchQueryLiveData = MutableLiveData<String>()
    fun updateSearchQuery(newText: String) {
        userSearchQueryLiveData.value = newText
    }

    fun getUserSearchQueryLiveData(): LiveData<String> {
        return userSearchQueryLiveData
    }

    companion object {
        private var sInstance: UserSearchRepository? = null
        val instance: UserSearchRepository
            get() {
                if (sInstance == null) {
                    sInstance = UserSearchRepository()
                }
                return sInstance!!
            }
    }
}