package paul.barthuel.go4lunch.ui.list_view;

import android.net.Uri;

import androidx.core.util.Pair;

public class UriBuilder {

    @SafeVarargs
    public final String buildUri(String scheme,
                                 String authority,
                                 String path,
                                 Pair<String, String>... queryParameters) {

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(path);
        for (Pair<String, String> queryParameter : queryParameters) {
            uriBuilder.appendQueryParameter(queryParameter.first, queryParameter.second);
        }
        return uriBuilder.build().toString();
    }
}
