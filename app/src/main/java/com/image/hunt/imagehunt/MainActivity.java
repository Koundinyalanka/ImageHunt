package com.image.hunt.imagehunt;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.alexbykov.nopaginate.callback.OnLoadMoreListener;
import ru.alexbykov.nopaginate.paginate.Paginate;
import ru.alexbykov.nopaginate.paginate.PaginateBuilder;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    EditText search_text;
    Button search;
    TextView tv;
    String param;
    public ProgressDialog pd;
    public RecyclerView results;
    RecyclerAdapter mAdapter;
    GridLayoutManager gridLayoutManager;
    public static final int GETJSONLOADER=25;
    ArrayList<Photo> al=null;
    int numberOfCols=2;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        results=(RecyclerView) findViewById(R.id.recycler_view);
        search_text=(EditText)findViewById(R.id.search_text);
        search=(Button)findViewById(R.id.search);
        tv=(TextView)findViewById(R.id.text);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                param=search_text.getText().toString();
                //Toast.makeText(MainActivity.this,NetworkUtils.getBaseUrl(param),Toast.LENGTH_LONG).show();
                numberOfCols=2;
                getLoaderManager().restartLoader(GETJSONLOADER,null,MainActivity.this);
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please Wait...");
                pd.setCancelable(false);
                pd.show();
            }
        });
        results.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(!view.canScrollVertically(1))
                {
                    Toast.makeText(MainActivity.this,"End of List",Toast.LENGTH_LONG).show();
                }
            }
        });

        gridLayoutManager=new GridLayoutManager(this,numberOfCols);
        results.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("array",al);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        al=savedInstanceState.getParcelableArrayList("array");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid=item.getItemId();
        if(itemid==R.id.two)
        {
            numberOfCols=2;
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,numberOfCols);
            results.setLayoutManager(gridLayoutManager);
            getLoaderManager().restartLoader(GETJSONLOADER,null,MainActivity.this);
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }
        else if(itemid==R.id.three)
        {
            numberOfCols=3;
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,numberOfCols);
            results.setLayoutManager(gridLayoutManager);
            getLoaderManager().restartLoader(GETJSONLOADER,null,MainActivity.this);
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }
        else if(itemid==R.id.four)
        {
            numberOfCols=4;
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,numberOfCols);
            results.setLayoutManager(gridLayoutManager);
            getLoaderManager().restartLoader(GETJSONLOADER,null,MainActivity.this);
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        switch (i)
        {
            case GETJSONLOADER:
                return new AsyncTaskLoader<String>(this) {
                    @Override
                    public String loadInBackground() {
                        URL url=null;
                        try {
                            url=new URL(NetworkUtils.getBaseUrl(param));
                            Log.e("URL",url.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        String s= null;
                        try {
                            s = NetworkUtils.getTheResponse(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Log.e("Return",s);
                        return s;
                    }

                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                        super.onStartLoading();
                    }
                };

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        //tv.setText(s);
        switch (loader.getId()) {
            case GETJSONLOADER:
            try {
                if (s != null) {
                    parse(s);
                    InternalStorage.writeObject(this, search_text.getText().toString(), s);
                } else {
                    s = InternalStorage.readObject(this, search_text.getText().toString()).toString();
                    Toast.makeText(this, "Internal Storage", Toast.LENGTH_LONG).show();
                }
                al = parse(s);
                mAdapter = new RecyclerAdapter(MainActivity.this, al);
                results.setAdapter(mAdapter);
                Paginate paginate = new PaginateBuilder()
                        .with(results)
                        .setOnLoadMoreListener(new OnLoadMoreListener() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onLoadMore() {
                                new Sample().execute();
                                //getLoaderManager().restartLoader(NEXTPAGELOADER,null,MainActivity.this);
                            }
                        })
                        .build();
                //paginate.showLoading(true);
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<Photo> parse(String s) throws JSONException, IOException, ClassNotFoundException {
        al=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(s);
        JSONObject photos=jsonObject.getJSONObject("photos");
        final int pages=photos.getInt("pages");
        JSONArray jsonArray=photos.getJSONArray("photo");
        //Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject image=jsonArray.getJSONObject(i);
            //Toast.makeText(MainActivity.this,image.getString("owner"),Toast.LENGTH_SHORT).show();
            al.add(new Photo(image.getString("id"),image.getString("owner"),image.getString("secret"),image.getString("server"),
                    image.getInt("farm"),image.getString("title"),image.getInt("ispublic"),image.getInt("isfriend"),
                    image.getInt("isfamily")));
        }

        return al;
    }

    //http or db request here
    private class Sample extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
        URL url=null;
        try {
            url=new URL(NetworkUtils.getBaseUrl(param));
            Log.e("URL",url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String s1= null;
        try {
            s1 = NetworkUtils.getTheResponse(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.e("Return",s);
        return s1;
    }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String s1) {
        super.onPostExecute(s1);
        try {
            ArrayList<Photo>a1=parse(s1);
            //Toast.makeText(MainActivity.this,s1,Toast.LENGTH_LONG).show();
            mAdapter.addItems(a1);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    }


}
