package mjkarbasian.moshtarimadar;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class Sales extends DrawerActivity {

    Fragment costsSaleProductFragment = new CostSaleProductList();
    Bundle salesBundle = new Bundle();

    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);


        salesBundle.putString("witchActivity", "sale");
        costsSaleProductFragment.setArguments(salesBundle);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) handleIntent(intent);
        else fragmentManager.beginTransaction().replace(R.id.container, costsSaleProductFragment,"CostSaleProductList").commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        CostSaleProductList queryFragment = (CostSaleProductList) fragmentManager.findFragmentByTag("CostSaleProductList");
        queryFragment.getSearchQuery(query);
    }

    public void fab_cost_sale_product(View v) {
        Intent intent = null;
        intent = new Intent(getBaseContext(), DetailSaleInsert.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sales, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_button).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                doMySearch(newText);
                // this is your adapter that will be filtered
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                //Here u can get the value "query" which is entered in the search box.
                return (query!=null)?true:false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }
}
