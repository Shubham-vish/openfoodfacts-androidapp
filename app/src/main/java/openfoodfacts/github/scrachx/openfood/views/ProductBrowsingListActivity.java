package openfoodfacts.github.scrachx.openfood.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.Search;
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient;
import openfoodfacts.github.scrachx.openfood.views.adapters.ProductsRecyclerViewAdapter;
import openfoodfacts.github.scrachx.openfood.views.listeners.EndlessRecyclerViewScrollListener;
import openfoodfacts.github.scrachx.openfood.views.listeners.RecyclerItemClickListener;

public class ProductBrowsingListActivity extends BaseActivity {

    private String searchType;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.products_recycler_view)
    RecyclerView productsRecyclerView;
    @BindView(R.id.textCountProduct)
    TextView countProductsView;
    @BindView(R.id.offlineCloudLinearLayout)
    LinearLayout offlineCloudLayout;
    ProgressBar progressBar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private List<Product> mProducts;
    private OpenFoodAPIClient api;
    private OpenFoodAPIClient apiClient;
    private int mCountProducts = 0;
    private int pageAddress = 1;
    private String[] typeStrings;
    String key;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        countProductsView.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();

        typeStrings = new String[]{
                "brand", "country", "additive", "search", "store", "packaging", "label" , "category"
        };

        searchType = extras.getString("search_type");
        key = extras.getString("key");

        getSupportActionBar().setTitle(key);

        switch (searchType) {
            case "brand": {
                getSupportActionBar().setSubtitle(R.string.brand_string);
                break;
            }
            case "country": {
                getSupportActionBar().setSubtitle(R.string.country_string);
                break;
            }
            case "additive": {
                getSupportActionBar().setSubtitle(R.string.additive_string);
                break;
            }
            case "search": {
                getSupportActionBar().setSubtitle(R.string.search_string);
                break;
            }
            case "store": {
                getSupportActionBar().setSubtitle(getString(R.string.store_string));
                break;
            }

            case "packaging": {
                getSupportActionBar().setSubtitle(getString(R.string.packaging_string));
                break;
            }

            case "label": {
                getSupportActionBar().setSubtitle(getString(R.string.label_string));
                break;
            }

            case "category": {
                getSupportActionBar().setSubtitle(getString(R.string.category_string));
                break;
            }
        }


        apiClient = new OpenFoodAPIClient(ProductBrowsingListActivity.this, BuildConfig.OFWEBSITE);
        api = new OpenFoodAPIClient(ProductBrowsingListActivity.this);
        productsRecyclerView = (RecyclerView) findViewById(R.id.products_recycler_view);
        setup();
    }

    @OnClick(R.id.buttonTryAgain)
    public void setup() {
        progressBar.setVisibility(View.VISIBLE);
        offlineCloudLayout.setVisibility(View.INVISIBLE);
        countProductsView.setVisibility(View.INVISIBLE);
        getDataFromAPI();
    }

    public void getDataFromAPI() {


        switch (searchType) {
            case "brand": {
                apiClient.getProductsByBrand(key, pageAddress, new OpenFoodAPIClient.OnBrandCallback() {
                    @Override
                    public void onBrandResponse(boolean value, Search brandObject) {
                        loadData(value, brandObject);
                    }
                });
                break;
            }
            case "country": {
                apiClient.getProductsByCountry(key, pageAddress, new OpenFoodAPIClient.onCountryCallback() {
                    @Override
                    public void onCountryResponse(boolean value, Search country) {
                        loadData(value, country);
                    }
                });
                break;
            }
            case "additive": {
                apiClient.getProductsByAdditive(key, pageAddress, new OpenFoodAPIClient.OnAdditiveCallback() {
                    @Override
                    public void onAdditiveResponse(boolean value, Search country) {
                        loadData(value, country);
                    }
                });
                break;
            }

            case "store": {
                apiClient.getProductsByStore(key, pageAddress, new OpenFoodAPIClient.OnStoreCallback() {
                    @Override
                    public void onStoreResponse(boolean value, Search storeObject) {
                        loadData(value, storeObject);
                    }
                });
                break;
            }

            case "packaging": {
                apiClient.getProductsByPackaging(key, pageAddress, new OpenFoodAPIClient.OnPackagingCallback() {
                    @Override
                    public void onPackagingResponse(boolean value, Search packagingObject) {
                        loadData(value, packagingObject);
                    }
                });
                break;
            }
            case "search": {
                api.searchProduct(key, pageAddress, ProductBrowsingListActivity.this, new OpenFoodAPIClient.OnProductsCallback() {
                    @Override
                    public void onProductsResponse(boolean isOk, Search searchResponse, int countProducts) {
                        loadData(isOk, searchResponse);
                    }
                });
            }

            case "label": {
                api.getProductsByLabel(key, pageAddress, new OpenFoodAPIClient.onLabelCallback() {
                    @Override
                    public void onLabelResponse(boolean value, Search label) {
                        loadData(value, label);
                    }
                });
            }

            case "category": {
                api.getProductsByCategory(key, pageAddress, new OpenFoodAPIClient.onCategoryCallback() {
                    @Override
                    public void onCategoryResponse(boolean value, Search label) {
                        loadData(value, label);
                    }
                });
            }
        }
    }


    private void loadData(boolean isResponseOk, Search response) {

        if (isResponseOk) {
            mCountProducts = Integer.parseInt(response.getCount());
            if (pageAddress == 1) {
                countProductsView.append(" " + NumberFormat.getInstance(getResources().getConfiguration().locale).format(Long.parseLong(response.getCount()
                )));
                mProducts = new ArrayList<>();
                mProducts.addAll(response.getProducts());
                if (mProducts.size() < mCountProducts) {
                    mProducts.add(null);
                }
                setUpRecyclerView();
            } else {
                if (mProducts.size() - 1 < mCountProducts + 1) {
                    final int posStart = mProducts.size();
                    mProducts.remove(mProducts.size() - 1);
                    mProducts.addAll(response.getProducts());
                    if (mProducts.size() < mCountProducts) {
                        mProducts.add(null);
                    }
                    productsRecyclerView.getAdapter().notifyItemRangeChanged(posStart - 1, mProducts.size() - 1);
                }
            }
        } else {
            productsRecyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            offlineCloudLayout.setVisibility(View.VISIBLE);
        }


    }

    private void setUpRecyclerView() {

        progressBar.setVisibility(View.INVISIBLE);
        countProductsView.setVisibility(View.VISIBLE);

        offlineCloudLayout.setVisibility(View.INVISIBLE);

        productsRecyclerView.setVisibility(View.VISIBLE);
        productsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ProductBrowsingListActivity.this, LinearLayoutManager.VERTICAL, false);
        productsRecyclerView.setLayoutManager(mLayoutManager);

        ProductsRecyclerViewAdapter adapter = new ProductsRecyclerViewAdapter(mProducts);
        productsRecyclerView.setAdapter(adapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(productsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        productsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mProducts.size() < mCountProducts) {
                    pageAddress = page;
                    getDataFromAPI();
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        productsRecyclerView.addOnScrollListener(scrollListener);


        productsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ProductBrowsingListActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Product p = ((ProductsRecyclerViewAdapter) productsRecyclerView.getAdapter()).getProduct(position);
                        if (p != null) {
                            String barcode = p.getCode();
                            api.getProduct(barcode, ProductBrowsingListActivity.this);
                            try {
                                View view1 = ProductBrowsingListActivity.this.getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager) ProductBrowsingListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mProducts.clear();
                countProductsView.setText(getResources().getString(R.string.number_of_results));
                pageAddress = 1;
                setup();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }
}
