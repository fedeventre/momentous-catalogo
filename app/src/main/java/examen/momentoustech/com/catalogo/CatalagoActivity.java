package examen.momentoustech.com.catalogo;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import examen.momentoustech.com.catalogo.manager.DatabaseManager;
import examen.momentoustech.com.catalogo.manager.RequestManager;
import examen.momentoustech.com.catalogo.model.Producto;
import examen.momentoustech.com.catalogo.network.GsonRequest;
import examen.momentoustech.com.catalogo.service.SyncService;
import examen.momentoustech.com.catalogo.utils.ApiHelper;
import examen.momentoustech.com.catalogo.utils.EndlessRecyclerViewScrollListener;
import examen.momentoustech.com.catalogo.views.adapters.ProductoAdapter;

public class CatalagoActivity extends AppCompatActivity {

    private static final String TAG = "ProductCatalogo";
    private static final int PAGING_LIMIT = 11;

    private ArrayList<Producto> mProductList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProductoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ContentLoadingProgressBar mProgressBar;
    private TextView emptyMsg;
    private MenuItem mSearchMenuItem;
    private boolean isSortList = Boolean.FALSE;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int mOffSet = 1;
    private int mOffsetQuery = 0;

    private BroadcastReceiver mBroacastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(SyncService.NEW_DATA)) {

                if (!mSearchMenuItem.isActionViewExpanded()) {
                    refreshData();
                    showView();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalago);
        registerReceiver(mBroacastReceiver, new IntentFilter(SyncService.NEW_DATA));

        DatabaseManager.getInstance().init(this);
        emptyMsg = (TextView) findViewById(R.id.empty_msg);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progressBar);

        mAdapter = new ProductoAdapter(this, mProductList);
        mRecyclerView.setAdapter(mAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mOffSet++;
                requestProduct();
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);


        // llamamos al servicio de sincronizacion

        Intent service = new Intent(this, SyncService.class);
        startService(service);


        DatabaseManager.getInstance().deleteAllProducts();
        requestProduct();
        showView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //Solo realizo la busqueda en la BD local, lo mejor seria tener una api para realizar
            //la busqueda en el servidor en toda la bd.
            mProductList.clear();
            mProductList.addAll(DatabaseManager.getInstance().searchProductByName(query, PAGING_LIMIT));
            mAdapter.notifyDataSetChanged();
            showView();

        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroacastReceiver);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        mSearchMenuItem = menu.findItem(R.id.search);


        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        mRecyclerView.clearOnScrollListeners();
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        mProductList.clear();
                        mOffsetQuery = 0;
                        mRecyclerView.addOnScrollListener(scrollListener);
                        refreshData();
                        scrollListener.resetState();
                        showView();
                        return true;
                    }
                });


        return true;
    }

    private void showView() {
        if (mProductList.isEmpty()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            emptyMsg.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyMsg.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_list:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                isSortList = item.isChecked();
                sortProducts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortProducts() {
        if (mSearchMenuItem.isActionViewExpanded()) {
            Collections.sort(mProductList, new Comparator<Producto>() {
                @Override
                public int compare(Producto producto, Producto t1) {
                    return producto.getName().compareTo(t1.getName());
                }
            });
            mAdapter.notifyDataSetChanged();

        } else {
            mOffsetQuery = 0;
            mProductList.clear();
            refreshData();
            showView();
        }
    }

    private void refreshData() {
        List<Producto> listTemp = DatabaseManager.getInstance().getProductos(isSortList, PAGING_LIMIT, mOffsetQuery);
        if (!listTemp.isEmpty()) {
            mProductList.addAll(listTemp);
            mAdapter.notifyDataSetChanged();
            scrollListener.resetState();
            mOffsetQuery++;
        }

    }

    private void requestProduct() {


        //verifica que mOffSet no sea mayor que 3, ya que solo es un ejemplo y la Api soporta un OffSet de 3
        if (mOffSet < 4) {
            Uri.Builder builder = Uri.parse(ApiHelper.CATALOGO_URL + mOffSet).buildUpon();

            mProgressBar.setVisibility(View.VISIBLE);
            String url = builder.build().toString();

            GsonRequest<ArrayList<Producto>> req = new GsonRequest<>(
                    Request.Method.GET,
                    url,
                    new TypeToken<ArrayList<Producto>>() {
                    }.getType(),
                    null,
                    null,
                    onRequestSuccess(),
                    onRequestError());

            RequestManager.getInstance().addToRequestQueue(req);

        } else {
            refreshData();

        }


    }


    protected Response.Listener<ArrayList<Producto>> onRequestSuccess() {
        return new Response.Listener<ArrayList<Producto>>() {
            @Override
            public void onResponse(ArrayList<Producto> response) {

                ArrayList<Producto> productos = response;
                DatabaseManager.getInstance().saveProduct(productos);

                refreshData();
                mProgressBar.setVisibility(View.GONE);
                showView();

            }
        };
    }

    protected Response.ErrorListener onRequestError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
                String message = null;
                mProgressBar.setVisibility(View.GONE);
                if (error instanceof NetworkError) {
                    message = getString(R.string.network_error);
                } else if (error instanceof ServerError) {
                    message = getString(R.string.server_error);
                } else if (error instanceof AuthFailureError) {
                    message = getString(R.string.auth_failure_error);
                } else if (error instanceof ParseError) {
                    message = getString(R.string.parsing_error);
                } else if (error instanceof NoConnectionError) {
                    message = getString(R.string.parsing_error);
                } else if (error instanceof TimeoutError) {
                    message = getString(R.string.parsing_error);
                }

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                showView();
                Log.i("Error reponse", message);
                scrollListener.resetState();

            }
        };
    }


}
