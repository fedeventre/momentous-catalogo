package examen.momentoustech.com.catalogo.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class GsonRequest<T> extends JsonRequest<T> {

    private final Gson mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final Type mClassType;
    private final Map<String, String> mHeaders;
    private final Listener<T> mListener;

    /**
     * Make a request and return a parsed object from JSON.
     * @param method Request method GET, POST, PUT, DELETE
     * @param url URL of the request to make
     * @param classType Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param requestBody JSON string of request body
     * @param listener Response listener
     * @param errorListener Error listener
     */
    public GsonRequest(int method, String url,
                       Type classType,
                       Map<String, String> headers,
                       String requestBody,
                       Listener<T> listener,
                       Response.ErrorListener errorListener) {

        super(method, url, requestBody, listener, errorListener);
        mClassType = classType;
        mHeaders = headers;
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));

            T result = mGson.fromJson(json, mClassType);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
    
}
