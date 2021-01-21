package paul.barthuel.go4lunch.ui.list_view

import android.net.Uri
import androidx.core.util.Pair

class UriBuilder {
    @SafeVarargs
    fun buildUri(scheme: String?,
                 authority: String?,
                 path: String?,
                 vararg queryParameters: Pair<String?, String?>): String {
        val uriBuilder = Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(path)
        for (queryParameter in queryParameters) {
            uriBuilder.appendQueryParameter(queryParameter.first, queryParameter.second)
        }
        return uriBuilder.build().toString()
    }
}