package mjkarbasian.moshtarimadar.Customers;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import mjkarbasian.moshtarimadar.Helpers.GalleryUtil;
import mjkarbasian.moshtarimadar.Helpers.Samples;
import mjkarbasian.moshtarimadar.Helpers.Utility;
import mjkarbasian.moshtarimadar.Others.DrawerActivity;
import mjkarbasian.moshtarimadar.R;

public class Customers extends DrawerActivity {

    private static final int REQUEST_CROP_URI = 800;
    private static final int GALLERY_ACTIVITY_CODE = 200;
    private static final int RESULT_CROP = 400;
    Context mContext;
    String mQuery;
    ImageView mCustomerAvatar;
    Fragment customersFragment = new CustomersLists();
    Fragment customerInsert = new CustomerInsert();


    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    private String LOG_TAG = Customers.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        //this line initialize all references
        Utility.initializer(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        //region handle search query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            handleIntent(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, customersFragment, "customersList").commit();
        }
        //endregion

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
        CustomersLists queryFragment = (CustomersLists) fragmentManager.findFragmentByTag("customersList");
        queryFragment.getSearchQuery(query);
    }

    public void fab_customers(View v) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, customerInsert);
        fragmentTransaction.addToBackStack(null);
        int callBackStack = fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_customers, menu);

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
                return (query != null) ? true : false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CustomersLists sortFragment = (CustomersLists) fragmentManager.findFragmentByTag("customersList");
        switch (item.getItemId()) {
            case R.id.menu_sort_newest:
                sortFragment.getSortOrder(R.id.menu_sort_newest);
                break;
            case R.id.menu_sort_rating:
                sortFragment.getSortOrder(R.id.menu_sort_rating);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void pic_selector(View view) {
        mCustomerAvatar = (ImageView) view;
        Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
        startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String picturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }
        if (requestCode == RESULT_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                Samples.customerAvatar.add(imageUri);
                mCustomerAvatar.setImageURI(imageUri);
                // Set The Bitmap Data To ImageView
            }
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
