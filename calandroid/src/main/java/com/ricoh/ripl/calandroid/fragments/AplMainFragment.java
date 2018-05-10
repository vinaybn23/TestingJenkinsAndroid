package com.ricoh.ripl.calandroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ricoh.ripl.calandroid.R;
import com.ricoh.ripl.calandroid.framework.AplProgressScreen;
import com.ricoh.ripl.calandroid.framework.AplThumbImage;
import com.ricoh.ripl.calandroid.framework.ArrayAdapter;
import com.ricoh.ripl.calandroid.framework.OcutagAuthScheme;
import com.ricoh.ripl.calandroid.framework.ThumbImageThread;
import com.ricoh.ripl.calandroid.model.AppData;
import com.ricoh.ripl.calandroid.model.PrefixData;
import com.ricoh.ripl.calandroid.model.ProductData;
import com.ricoh.ripl.calandroid.model.RectData;
import com.ricoh.ripl.calandroid.model.SearchData;
import com.ricoh.ripl.calandroid.model.SosData;
import com.ricoh.ripl.calandroid.recyclerview.AplRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class AplMainFragment extends Fragment implements OnFragmentInteractionListener, Handler.Callback {

    private static String TAG = "AplMainFragment";
    public static boolean pastJob = false;
    private boolean invalidData = false;
    private Context context;
    private String finalJsonResult = null;
    private Date date;
    public static String[] authHeaders;
    private URL url;
    public static AplRecyclerViewAdapter aplRecyclerViewAdapter;
    private ArrayAdapter<String> sortadapter, sosadapter;
    public static boolean isSortSelected = true;
    private ImageButton correctiveActions;
    private ImageButton fitToHeight;
    private ImageButton fitToWidth;
    private static ImageButton submitCA;
    private ImageButton backimg,apl_sort_drop_down;
    private ImageButton sos;
    private ImageView itemImage, closesearch;
    private TextView title;
    private static TextView search_result;
    private TextView search_title;
    private TextView sortby;
    private TextView textcatname;
    private TextView textothername;
    private TextView searchitemcount;
    private TextView sos_spinner_item_name;
    private TextView spinner_item_name;
    private TextView categoryname;
    private TextView searchname;
    private TextView searchcount;
    private TextView textviewtext;
    private TextView networktext;
    private TextView parametertext;
    public static TextView progresstext;
    private TextView totcatper, tototherper,spinner;
    public static FrameLayout progressBarHolder;
    public static RelativeLayout secondParent, searchresultview, splitlayout;
    private FrameLayout sos_layout;
    public static RecyclerView recyclerView;
    private AutoCompleteTextView search;
    public static List<SosData> sosFacingsList;
    private String upcnumber, desc, finalImg = null;
    private Dialog builder;
    private String category = null, cpg = null, tokenn = null, host = null, appid = null, appsecret = null, devid = null, devsecret = null, upcprefix = null, pimhost = null, temppastjob = null;
    private Spinner sos_spinner;
    private GetData getDataTask;
    private String token;
    private RelativeLayout pogrogLayout, searchresultlayout,titlelayout,constantLayout;
    private NetworkInfo networkInfo;
    private String[] sa = null;
    int curCount = 0;
    float totalCount = 0F;
    private ThreadPoolExecutor executor;
    private Button cancel, ok;
    private RelativeLayout alllayout,yeslayout,nolayout,unknownlayout;
    public static RelativeLayout apl_spinner_view;
    private LinearLayout aplspecificlayout;

    public static final int THREAD_INFO = 1;
    public static final int UPDATE_ADAPTER = 2;
    public ArrayAdapter<SearchData> arrayAdapter;
    public static String highlightedTag;
    public static boolean isCalSelected = true;
    private Set<SearchData> uniqueSearchItems;
    public static ArrayList<ProductData> sortList;
    public static Set<String> savedList;
    public static boolean sosSelected = false;
    private static final int viewId = 1, borderId = 2;
    private int countOfSos = 0;
    private JSONObject result;

    //for Apl
    public static ArrayList<ProductData> aplProductArray;
    public static AppData aplAppData;
    public static ArrayList<ProductData> aplFilteredArray;
    private Set<SearchData> uniqueAplSearchItems;
    private SearchData[] searchAplList;
    public static List<SosData> sosFacingsAplList;
    public static String isAplSortSelected = "All";
    private boolean isSaveOnDeviceRequired = true;
    private static final int READ_BLOCK_SIZE = 100;

    public AplMainFragment() {
    }

    public static AplMainFragment newInstance() {
        return new AplMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedList = new HashSet<>();
        sosFacingsList = new ArrayList<>();
        sortList = new ArrayList<>();

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        Bundle args = null;
        try {
            args = getArguments();

            if (args != null) {
                tokenn = args.getString("token", "");
                host = args.getString("host", "");
                appid = args.getString("appid", "");
                appsecret = args.getString("appsecret", "");
                devid = args.getString("devid", "");
                devsecret = args.getString("devsecret", "");
                pimhost = args.getString("pimhost", "");
                temppastjob = args.getString("pastjob", "");
                if (temppastjob.equals("true")) {
                    pastJob = true;
                } else {
                    pastJob = false;
                }
            }

            date = new Date();
            token = tokenn;
            authHeaders = new String[4];
            authHeaders[0] = devid;
            authHeaders[1] = devsecret;
            authHeaders[2] = appid;
            authHeaders[3] = appsecret;

            url = new URL("https://" + host + "/ore/api/v1/apl_json?requestToken=" + token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parameters and network error condition handling
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        View view = inflater.inflate(R.layout.fragment_apl_main, container, false);

        InitializeViews(view);

        View recyclerImage = inflater.inflate(R.layout.apl_recycler_item, container, false);
        itemImage = recyclerImage.findViewById(R.id.itemImage);

        if (!((networkInfo != null) && (networkInfo.isConnected()))) {
            view = inflater.inflate(R.layout.errorscreen, container, false);
            if (!(TextUtils.isEmpty(tokenn) || tokenn.equals("") || tokenn.length() < 8 || TextUtils.isEmpty(host) || host.equals("") || TextUtils.isEmpty(appid) || appid.equals("") || appid.length() < 44 || TextUtils.isEmpty(appsecret) || appsecret.equals("") || appsecret.length() < 62 || TextUtils.isEmpty(devid) || devid.equals("") || devid.length() < 44 || TextUtils.isEmpty(devsecret) || devsecret.equals("") || devsecret.length() < 62 || TextUtils.isEmpty(pimhost) || pimhost.equals(""))) {
                parametertext = view.findViewById(R.id.parametertext);
                parametertext.setVisibility(View.INVISIBLE);
            }
            networktext = view.findViewById(R.id.networktext);
            networktext.setText(R.string.network);
            networktext.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(tokenn) || tokenn.equals("") || tokenn.length() < 8 || TextUtils.isEmpty(host) || host.equals("") || TextUtils.isEmpty(appid) || appid.equals("") || appid.length() < 44 || TextUtils.isEmpty(appsecret) || appsecret.equals("") || appsecret.length() < 62 || TextUtils.isEmpty(devid) || devid.equals("") || devid.length() < 44 || TextUtils.isEmpty(devsecret) || devsecret.equals("") || devsecret.length() < 62 || TextUtils.isEmpty(pimhost) || pimhost.equals("")) {
            view = inflater.inflate(R.layout.errorscreen, container, false);

            if (networkInfo != null) {
                networktext = view.findViewById(R.id.networktext);
                networktext.setVisibility(View.INVISIBLE);
            }
            parametertext = view.findViewById(R.id.parametertext);
            parametertext.setText(R.string.params);
            parametertext.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        File[] files = new File[0];
        try {
            File directory = getActivity().getFilesDir();
            files = directory.listFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != files && files.length > 0) {
            for (File file : files) {
                if (file.getName().contains(token)) {
                    loadJSONFromStorage();
                }
            }
        } else {
            try {
                if (!(networkInfo == null || (TextUtils.isEmpty(tokenn) || tokenn.equals("") || tokenn.length() < 8 || TextUtils.isEmpty(host) || host.equals("") || TextUtils.isEmpty(appid) || appid.equals("") || appid.length() < 44 || TextUtils.isEmpty(appsecret) || appsecret.equals("") || appsecret.length() < 62 || TextUtils.isEmpty(devid) || devid.equals("") || devid.length() < 44 || TextUtils.isEmpty(devsecret) || devsecret.equals("") || devsecret.length() < 62 || TextUtils.isEmpty(pimhost) || pimhost.equals("")))) {
                    secondParent.setVisibility(View.GONE);
                    getDataTask = new GetData();
                    if (!(AsyncTask.Status.RUNNING == getDataTask.getStatus()) || !(AsyncTask.Status.PENDING == getDataTask.getStatus()) || AsyncTask.Status.FINISHED == getDataTask.getStatus()) {
                        getDataTask.execute();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // adding sos spinner items
        String[] sosSpinner = new String[]{"Percent"};
        View view1 = getLayoutInflater().inflate(R.layout.sos_spinner_item, null);
        sos_spinner_item_name = view1.findViewById(R.id.sos_spinner_item_name);

        sosadapter = new ArrayAdapter<>(getActivity(), R.layout.sos_spinner_item, sosSpinner);
        sos_spinner.setAdapter(sosadapter);

        sos_layout.setOnClickListener(null);
        sos_layout.setVisibility(View.INVISIBLE);
        sosSelected = false;

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setSelected(!spinner.isSelected());

                if (spinner.isSelected()) {
                    apl_spinner_view.setVisibility(View.VISIBLE);
                } else {
                    apl_spinner_view.setVisibility(View.INVISIBLE);
                }
            }
        });

        apl_sort_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apl_sort_drop_down.setSelected(!apl_sort_drop_down.isSelected());

                if (apl_sort_drop_down.isSelected()) {
                    apl_spinner_view.setVisibility(View.VISIBLE);
                } else {
                    apl_spinner_view.setVisibility(View.INVISIBLE);
                }
            }
        });

        alllayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAplSortSelected = "All";
                setSortCondition();
                spinner.setText("All");
            }
        });

        yeslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAplSortSelected = "Yes";
                setSortCondition();
                spinner.setText("Yes");
            }
        });

        nolayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAplSortSelected = "No";
                setSortCondition();
                spinner.setText("No");
            }
        });

        unknownlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAplSortSelected = "Unknown";
                setSortCondition();
                spinner.setText("Unknown");
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String queryStr = String.valueOf(s).trim();
                if (queryStr.length() > 0) {
                    closesearch.setVisibility(View.VISIBLE);
                } else {
                    closesearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
                String queryStr = String.valueOf(s).trim();
                if (queryStr.length() > 0) {
                    closesearch.setVisibility(View.VISIBLE);
                } else {
                    closesearch.setVisibility(View.INVISIBLE);
                }
            }
        };
        search.addTextChangedListener(textWatcher);

        // setting textviews data
        title.setText(R.string.category);
        title.setTextSize(18);
        title.setVisibility(View.VISIBLE);
        search_title.setText(R.string.apl_search_title);
        search_title.setTextSize(10);
        search_title.setTextColor(Color.parseColor("#3648a8"));
        search_title.setVisibility(View.VISIBLE);

        search_result.setTextSize(14);
        search_result.setTextColor(Color.parseColor("#3648a8"));
        search_result.setVisibility(View.VISIBLE);
        sortby.setText(R.string.viewBy);
        sortby.setTextSize(10);
        sortby.setTextColor(Color.parseColor("#3648a8"));
        sortby.setVisibility(View.VISIBLE);

        // SOS view onclick
        sos.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                sos.setSelected(!sos.isSelected());
                apl_spinner_view.setVisibility(View.INVISIBLE);

                if (sos.isSelected()) {

                    if (sosFacingsAplList.size() == 0) {
                        sos.setSelected(false);
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Alert!");
                        alertDialog.setMessage("SoS is not available as system is not able to detect products with our UPC prefixes");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    } else {
                        sosSelected = true;
                        sos_layout.setVisibility(View.VISIBLE);
                        calculationOfTotalPercentSOS();

                        for (View sosrect : AplRogFragment.sosRects) {
                            sosrect.setVisibility(View.VISIBLE);
                        }
                        for (View sostext : AplRogFragment.sosText) {
                            sostext.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    sos_layout.setVisibility(View.INVISIBLE);
                    sosSelected = false;
                    for (View sosrect : AplRogFragment.sosRects) {
                        sosrect.setVisibility(View.INVISIBLE);
                    }
                    for (View sostext : AplRogFragment.sosText) {
                        sostext.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        // CA view onclick
        correctiveActions.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View imageButton) {
                correctiveActions.setSelected(!correctiveActions.isSelected());
                apl_spinner_view.setVisibility(View.INVISIBLE);

                isCalSelected = correctiveActions.isSelected();
                updateViewsLayer(AplRecyclerViewAdapter.mAplValuesFiltered);

            }
        });

        // submit CA
        submitCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onClick of submit button: ");
                try {
                    builder = new Dialog(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View uncorrectedview = inflater.inflate(R.layout.submit_incorrected_dialog, null);
                    TextView unresolvedtext = uncorrectedview.findViewById(R.id.submittext);

                    unresolvedtext.setText("Please confirm, you want to submit the APL?");
                    cancel = uncorrectedview.findViewById(R.id.cancel);
                    ok = uncorrectedview.findViewById(R.id.ok);
                    builder.setContentView(uncorrectedview);
                    builder.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        isSaveOnDeviceRequired = false;
                        try {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
                            if (((networkInfo != null) && (networkInfo.isConnected()))) {
                                createJsonData();
                                secondParent.setVisibility(View.INVISIBLE);
                                AplProgressScreen.loadProgressScreen(getActivity());
                            } else {
                                Toast.makeText(getActivity(), "Please verify network connectivity.If problems persist,please contact technical support", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        builder.dismiss();
                    }
                });
            }
        });

        secondParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        constantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        titlelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        aplspecificlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apl_spinner_view.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setSortCondition(){
        isSortSelected = true;
        aplRecyclerViewAdapter = new AplRecyclerViewAdapter(getContext(), sortList);

        sortList = new ArrayList<>();

        sortList = sortRecyclerArray();
        if (null != sortList) {
            updateViewsLayer(sortList);
            aplRecyclerViewAdapter = new AplRecyclerViewAdapter(getContext(), sortList);
            recyclerView.setAdapter(aplRecyclerViewAdapter);
            aplRecyclerViewAdapter.notifyDataSetChanged();
        }
        apl_spinner_view.setVisibility(View.INVISIBLE);
    }

    public static ArrayList<ProductData> sortRecyclerArray() {
        switch (isAplSortSelected) {
            case "Yes":
                sortList.clear();
                for (ProductData jsonListData : aplFilteredArray) {
                    if (jsonListData.present.equals("yes")) {
                        sortList.add(jsonListData);
                    }
                }
                return sortList;
            case "No":
                sortList.clear();
                for (ProductData jsonListData : aplFilteredArray) {
                    if (jsonListData.present.equals("no")) {
                        sortList.add(jsonListData);
                    }
                }
                return sortList;
            case "Unknown":
                sortList.clear();
                for (ProductData jsonListData : aplFilteredArray) {
                    if (jsonListData.present.equals("unknown")) {
                        sortList.add(jsonListData);
                    }
                }
                return sortList;
            case "All":
                sortList.clear();
                if (null != aplFilteredArray)
                    sortList.addAll(aplFilteredArray);
                return sortList;
            default:
                sortList.clear();
                return sortList;
        }

    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case THREAD_INFO:
                curCount++;
                totalCount = aplFilteredArray.size();
                float per = (curCount / totalCount) * 100;
                Log.d(TAG, "Thumb Image Download Progress: " + per);
                if (per < 100)
                    Log.d(TAG, "Downloaded [" + curCount + "/" + (int) totalCount + "]");
                else
                    Log.d(TAG, "All images downloaded.");
                break;
            case UPDATE_ADAPTER:
                Log.d(TAG, "goto updated prod list");
                updateBitmapInProductList();
                break;
        }

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPause() {
        super.onPause();

        if (isSaveOnDeviceRequired) {
            try {
                FileOutputStream fileout = getActivity().openFileOutput(token + ".json", MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write(createJsonData().toString());
                outputWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadJSONFromStorage() {
        try {
            FileInputStream inputStream = getActivity().openFileInput(token + ".json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String jsonData = "";
            int charRead;

            while ((charRead = inputStreamReader.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                jsonData += readstring;
            }

            inputStream.close();
            ParseAPLJson(jsonData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private JSONObject createJsonData() throws JSONException {

        String corrected = "false";

        JSONObject object = new JSONObject(finalJsonResult);
        JSONObject result = object.getJSONObject("result");

        JSONObject aplData = result.getJSONObject("apl");
        JSONArray aplItems = aplData.getJSONArray("items");
        for (int x = 0; x < aplItems.length(); x++) {
            ProductData productData = new ProductData();
            JSONObject aplDataItem = aplItems.getJSONObject(x);

            for (ProductData eachData : aplProductArray) {
                if (eachData.upc.equals(aplDataItem.getString("upc"))) {

                    if (eachData.edited == 1 || eachData.edited == 2 || eachData.edited == 3) {
                        corrected = "true";
                    }

                    if (null != eachData.editSelected) {
                        switch (eachData.editSelected) {
                            case "yes":
                                aplDataItem.put("corrected", corrected);
                                aplDataItem.put("comment", eachData.edittextData);
                                break;

                            case "no":
                                aplDataItem.put("corrected", corrected);
                                aplDataItem.put("comment", eachData.edittextData);
                                break;

                            case "unknown":
                                aplDataItem.put("corrected", corrected);
                                aplDataItem.put("comment", eachData.edittextData);
                                break;

                            default:
                                aplDataItem.put("corrected", false);
                                aplDataItem.put("comment", null);
                                break;
                        }
                    }
                }
            }
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (((networkInfo != null) && (networkInfo.isConnected()))) {
            new SendData().execute(String.valueOf(object));
        } else {
            Toast.makeText(getActivity(), "Please verify network connectivity.If problems persist,please contact technical support", Toast.LENGTH_SHORT).show();
        }
        return object;
    }

    //////////////////////////////// Getting Data///////////////////////
    private class GetData extends AsyncTask<String, Void, String> {

        String responseMsg = null;
        String finalMsg = null;
        String sendMsg = null;
        int responseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AplProgressScreen.loadProgressScreen(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Get Authorization headers
                String authString = null;
                OcutagAuthScheme.Builder builder = new OcutagAuthScheme.Builder("GET", String.valueOf(url), sendDate(date), authHeaders);
                OcutagAuthScheme authScheme = null;
                try {
                    authScheme = new OcutagAuthScheme(builder);
                    authString = authScheme.getAuthorizedHeader();
                    Log.d(TAG, "Json response: " + authString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // make API call for cal_json
                URL urls = new URL(String.valueOf(url));
                HttpsURLConnection con = (HttpsURLConnection) urls.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Date", sendDate(date));
                con.setRequestProperty("Authorization", authString);
                Map<String, List<String>> headerResp = con.getHeaderFields();
                responseCode = con.getResponseCode();
                responseMsg = con.getResponseMessage();

                Log.d(TAG, "Json Server Response: " + headerResp);
                Log.d(TAG, "Json response: -----------------> " + responseMsg + " : " + responseCode);

                if (responseCode == 200) {
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    finalMsg = readStream(in, 500000);
                    Log.d(TAG, "Json response: " + finalMsg);
                } else {
                    Log.d(TAG, "Json response: Error connecting to server!");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return finalMsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }
            Log.d(TAG, "jsonData: " + result);
            if (responseCode != 200 || TextUtils.isEmpty(finalMsg) || finalMsg.equals("") || finalMsg.length() < 0) {
                finalMsg = "";
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Request cannot be processed. Please verify request token");
                alertDialog.setCancelable(false);
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            getActivity().onBackPressed();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            } else {
                finalJsonResult = result;
                ParseAPLJson(result);

                categoryname.setText(" " + category);
                categoryname.setTextSize(18);
                categoryname.setVisibility(View.VISIBLE);

                correctiveActions.setSelected(true);
                isCalSelected = true;
                highlightedTag = "";

//                for (ProductData jsonListData : aplFilteredArray) {
//                    if (jsonListData.present.equals("yes")) {
//                        sortList.add(jsonListData);
//                        search_result.setText(sortList.size() + " of " + aplFilteredArray.size());
//                        isSortSelected = true;
//                    }
//                }
                updateText();

                for (int j = 0; j < aplFilteredArray.size(); j++) {
                    String upc = aplFilteredArray.get(j).upc;
                    if(getActivity() != null) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
                        if (((networkInfo != null) && (networkInfo.isConnected()))) {
                            executor.execute(new AplThumbImage(j, context, upc, pimhost, new Handler(AplMainFragment.this)));
                        } else {
                            Toast.makeText(getActivity(), "Please verify network connectivity.If problems persist,please contact technical support", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if (!invalidData && getActivity()!=null) {
                    try {
                        Fragment fragment = AplRogFragment.newInstance(Uri.parse(aplAppData.stitchedImage));
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.commitAllowingStateLoss();
                        transaction.replace(R.id.pogrogLayout, fragment, "aplRogfragment");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if(null != searchAplList && getActivity()!=null) {
                    arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.search_dropdown, searchAplList);
                    search.setThreshold(1);
                    search.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
                    search.setAdapter(arrayAdapter);
                }
                search.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            apl_spinner_view.setVisibility(View.INVISIBLE);
                            isSortSelected = false;

                            String s = search.getText().toString();
                            search.dismissDropDown();
                            searchname.setText("Products Containing '" + s + "'");

                            List<ProductData> filteredJsonList = new ArrayList<>();
                            if (s.length() > 0) {

                                for (int searchposition = 0; searchposition < arrayAdapter.getCount(); searchposition++) {
                                    SearchData dataForSearch = arrayAdapter.getItem(searchposition);
                                    for (ProductData tempData : aplFilteredArray) {
                                        if (tempData.upc.equalsIgnoreCase(dataForSearch.uniqueTagOfProduct)) {
                                            filteredJsonList.add(tempData);
                                            break;
                                        }
                                    }
                                }
                            }
                            AplRecyclerViewAdapter.mAplValuesFiltered = filteredJsonList;

                            searchcount.setText(filteredJsonList.size() + " Products Found");

                            aplRecyclerViewAdapter.notifyDataSetChanged();
                            updateViewsLayer(filteredJsonList);

                            searchresultlayout.setVisibility(View.INVISIBLE);
                            searchresultview.setVisibility(View.VISIBLE);
                            closesearch.setVisibility(View.VISIBLE);

                            InputMethodManager inputManager = (InputMethodManager) getActivity().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                            return true;

                        }
                        return false;
                    }
                });

                search.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        apl_spinner_view.setVisibility(View.INVISIBLE);
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            closesearch.setVisibility(View.VISIBLE);
                            searchresultlayout.setVisibility(View.VISIBLE);
                            searchresultview.setVisibility(View.INVISIBLE);
                            isSortSelected = true;
                            AplRecyclerViewAdapter.mAplValuesFiltered = sortRecyclerArray();

                            aplRecyclerViewAdapter.notifyDataSetChanged();
                            updateViewsLayer(AplRecyclerViewAdapter.mAplValuesFiltered);

                            if(search.getText().toString().isEmpty()){
                                closesearch.setVisibility(View.INVISIBLE);
                            }
                        }
                        return false;
                    }
                });

                closesearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apl_spinner_view.setVisibility(View.INVISIBLE);
                        /*if (isSortSelected) {
                            AplRecyclerViewAdapter.mAplValuesFiltered = sortList;
                        } else {
                            AplRecyclerViewAdapter.mAplValuesFiltered = aplFilteredArray;
                        }*/
                        AplRecyclerViewAdapter.mAplValuesFiltered = sortRecyclerArray();


                        isSortSelected = true;
                        aplRecyclerViewAdapter.notifyDataSetChanged();
                        updateViewsLayer(AplRecyclerViewAdapter.mAplValuesFiltered);

                        searchresultview.setVisibility(View.GONE);
                        searchresultlayout.setVisibility(View.VISIBLE);
                        search.setText("");
                        closesearch.setVisibility(View.INVISIBLE);

                        InputMethodManager inputManager = (InputMethodManager) getActivity().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });

                search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        apl_spinner_view.setVisibility(View.INVISIBLE);
                        isSortSelected = false;
                        SearchData tempSearch = (SearchData) parent.getItemAtPosition(position);
                        ArrayList<ProductData> filteredList = new ArrayList<>();

                        for (ProductData tempCalData : aplFilteredArray) {
                            if (tempCalData.upc.equalsIgnoreCase(tempSearch.uniqueTagOfProduct)) {
                                filteredList.add(tempCalData);
                                break;
                            }
                        }
                        AplRecyclerViewAdapter.mAplValuesFiltered = filteredList;

                        if (tempSearch.isUpcOrDesc) {
                            search.setText(tempSearch.upc);
                            searchname.setText(tempSearch.upc);
                        } else {
                            search.setText(tempSearch.description);
                            searchname.setText(tempSearch.description);
                        }
                        String s = search.getText().toString();

                        searchcount.setText(filteredList.size() + " Products Found");

                        searchresultlayout.setVisibility(View.INVISIBLE);
                        searchresultview.setVisibility(View.VISIBLE);
                        searchcount.setText(AplRecyclerViewAdapter.mAplValuesFiltered.size() + " Products Found");

                        aplRecyclerViewAdapter.notifyDataSetChanged();
                        updateViewsLayer(filteredList);
                        InputMethodManager inputManager = (InputMethodManager) getActivity().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });

                backimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apl_spinner_view.setVisibility(View.INVISIBLE);
//                        if (isSortSelected) {
//                            AplRecyclerViewAdapter.mAplValuesFiltered = sortList;
//                        } else {
//                            AplRecyclerViewAdapter.mAplValuesFiltered = aplFilteredArray;
//                        }
                        AplRecyclerViewAdapter.mAplValuesFiltered = sortRecyclerArray();
                        isSortSelected = true;
                        aplRecyclerViewAdapter.notifyDataSetChanged();
                        updateViewsLayer(AplRecyclerViewAdapter.mAplValuesFiltered);

                        searchresultview.setVisibility(View.GONE);
                        searchresultlayout.setVisibility(View.VISIBLE);
                        search.setText("");
                        closesearch.setVisibility(View.INVISIBLE);

                        InputMethodManager inputManager = (InputMethodManager) getActivity().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
            }
        }

    }

    private class SendData extends AsyncTask<String, Void, Void> {
        String responseMsg = null;
        int responseCode = 0;
        HttpsURLConnection con;
        DataOutputStream wr;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL hostURL = new URL("https://" + host + "/ore/api/v1/apl_json?requestToken=" + tokenn);

                // Get Authorization headers
                String authString = null;
                OcutagAuthScheme.Builder builder = new OcutagAuthScheme.Builder("PUT", String.valueOf(hostURL), sendDate(date), authHeaders);
                Log.d(TAG, "doInBackground: lenght:" + strings[0].length());
                byte[] contentBody = builder.md5(strings[0]);
                String base64Data = builder.getBase64Value(contentBody);
                builder.setContentMD5(String.valueOf(contentBody));

                OcutagAuthScheme authScheme = null;
                try {
                    authScheme = new OcutagAuthScheme(builder);
                    authString = authScheme.getAuthorizedHeader();
                    Log.d(TAG, "Json response: " + authString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // make API call for sending cal_json
                URL urls = new URL(String.valueOf(url));
                con = (HttpsURLConnection) urls.openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("PUT");
                con.setRequestProperty("Date", sendDate(date));
                con.setRequestProperty("Authorization", authString);
                con.setRequestProperty("Host", host);
                con.setRequestProperty("Content-MD5", Arrays.toString(contentBody));
                con.addRequestProperty("Content-Type", "application/json");


                //byte[] outputInBytes = strings[0].getBytes("UTF-8");
                Log.d(TAG, "doInBackground: lenght after:" + strings[0].length());
                con.addRequestProperty("Content-Length", String.valueOf(strings[0].length()));

                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(strings[0]);
                wr.flush();


                Map<String, List<String>> headerResp = con.getHeaderFields();
                responseCode = con.getResponseCode();
                responseMsg = con.getResponseMessage();

                Log.d(TAG, "Json Server Response: " + headerResp);
                Log.d(TAG, "Json response: -----------------> " + responseMsg + " : " + responseCode);

                if (responseCode == 200) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                secondParent.setVisibility(View.VISIBLE);
                                AplProgressScreen.unloadProgressScreen(getActivity());
                                submitCA.setEnabled(false);
                                submitCA.setAlpha(0.3f);
                            }
                        });
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    pastJob = true;
                    Log.d(TAG, "Json Data Sent Successfully!");
                } else {
                    pastJob = false;
                    Log.d(TAG, "Error sending Json Data to server!");
                }

            } catch (Exception e) {
                e.getCause();
                e.printStackTrace();
            } finally {
                if (null != con) {
                    con.disconnect();
                    try {
                        if (null != wr)
                            wr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void calculationOfTotalPercentSOS() {
        float competitorPercent = 0;
        float cpgPercent = 0;

        for (SosData sosJsonList : sosFacingsAplList) {
            if (sosJsonList.sos_competitor.equals("true")) {
                competitorPercent += sosJsonList.sos_percent * 100;
            } else {
                cpgPercent += sosJsonList.sos_percent * 100;
            }
        }

        if (!TextUtils.isEmpty(cpg)) {
            textcatname.setText(cpg + "  -  " + (int) Math.round(cpgPercent / countOfSos) + "%");
        } else {
            textcatname.setText("Category not specified" + "  -  " + (int) Math.round(cpgPercent / countOfSos) + "%");
        }
        textothername.setText("Competitor's  -  " + (int) Math.round(competitorPercent / countOfSos) + "%");
    }

    private void InitializeViews(View view) {
        searchresultlayout = view.findViewById(R.id.searchresultlayout);
        backimg = view.findViewById(R.id.backimg);
        searchname = view.findViewById(R.id.searchname);
        searchcount = view.findViewById(R.id.searchcount);

        constantLayout = view.findViewById(R.id.constantLayout);
        titlelayout = view.findViewById(R.id.titlelayout);
        aplspecificlayout = view.findViewById(R.id.aplspecificlayout);
        apl_spinner_view = view.findViewById(R.id.apl_spinner_view);
        alllayout = view.findViewById(R.id.alllayout);
        yeslayout = view.findViewById(R.id.yeslayout);
        nolayout = view.findViewById(R.id.nolayout);
        unknownlayout = view.findViewById(R.id.unknownlayout);
        apl_sort_drop_down = view.findViewById(R.id.apl_sort_drop_down);
        totcatper = view.findViewById(R.id.totcatper);
        tototherper = view.findViewById(R.id.tototherper);
        textcatname = view.findViewById(R.id.textcatname);
        textothername = view.findViewById(R.id.textothername);
        sos_layout = view.findViewById(R.id.sos_layout);
        sos = view.findViewById(R.id.sos);
        categoryname = view.findViewById(R.id.categoryname);
        progressBarHolder = view.findViewById(R.id.aplProgressBarHolder);
        progresstext = view.findViewById(R.id.progresstext);
        closesearch = view.findViewById(R.id.closesearch);
        searchresultview = view.findViewById(R.id.searchresultview);
        correctiveActions = view.findViewById(R.id.ca);
        submitCA = view.findViewById(R.id.submitCA);

        recyclerView = view.findViewById(R.id.aplRecyclerView);
        search = view.findViewById(R.id.apl_search_box);

        title = view.findViewById(R.id.title);
        search_title = view.findViewById(R.id.searchTitle);
        search_result = view.findViewById(R.id.searchResult);
        sortby = view.findViewById(R.id.apl_sortBy);

        spinner = view.findViewById(R.id.apl_sortspinner);
        sos_spinner = view.findViewById(R.id.sos_spinner);
        secondParent = view.findViewById(R.id.secondParent);
    }

   /* public void updateViewsLayer(List<ProductData> listDisplayed) {
        if (isCalSelected && (null != listDisplayed) && (null != AplRogFragment.viewObjects)) {
            List<String> uniqueTagList = new ArrayList<>();
            for (ProductData singleCalData : listDisplayed) {
                uniqueTagList.add(singleCalData.upc);
            }
            List<String> keyList = new ArrayList<>(AplRogFragment.viewObjects.keySet());
            for (String key : keyList) {
                if (uniqueTagList.contains(key) && (null != AplRogFragment.viewObjects.get(key))) {
                    for (View eachView : AplRogFragment.viewObjects.get(key)) {
                        if (eachView.getId() == viewId) {
                            eachView.setVisibility(View.VISIBLE);
                        } else if ((eachView.getId() == borderId) && eachView.getTag().equals(highlightedTag)) {
                            eachView.setVisibility(View.VISIBLE);
                        } else {
                            eachView.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    if (null != AplRogFragment.viewObjects.get(key)) {
                        for (View eachView : AplRogFragment.viewObjects.get(key)) {
                            eachView.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            }

        } else {

            ArrayList<String> keyList = new ArrayList<String>(AplRogFragment.viewObjects.keySet());
            for (String key : keyList) {
                if (null != AplRogFragment.viewObjects.get(key)) {
                    for (View eachView : AplRogFragment.viewObjects.get(key)) {
                        eachView.setVisibility(View.INVISIBLE);
                    }
                }

            }
        }
    }*/

    public static void updateViewsLayer(List<ProductData> listDisplayed) {
        if (isCalSelected && (null != listDisplayed) && (null != AplRogFragment.viewObjects)) {
            HashMap<String, String> uniqueTagList = new HashMap<>();
            for (ProductData singleCalData : listDisplayed) {
                uniqueTagList.put(singleCalData.upc, singleCalData.present);
            }
            List<String> keyList = new ArrayList<>(AplRogFragment.viewObjects.keySet());
            for (String key : keyList) {
                List<String> uniqueTagKeyList = new ArrayList<>(uniqueTagList.keySet());
                if (uniqueTagKeyList.contains(key) && (null != AplRogFragment.viewObjects.get(key))) {
                    switch (uniqueTagList.get(key)) {
                        case "yes":
                            for (View eachView : AplRogFragment.viewObjects.get(key)) {
                                if (eachView.getId() == (int) 1) {
//                                    ((ViewGroup) eachView.getParent()).removeView(eachView);
                                    eachView.setBackgroundResource(R.drawable.aplrectborder_yes);
//                                    AplRogFragment.parent_layout.addView(eachView);
//                                    eachView.invalidate();
                                    eachView.setVisibility(View.VISIBLE);
                                } else if ((eachView.getId() == borderId) && eachView.getTag().equals(highlightedTag)) {
                                    eachView.setVisibility(View.VISIBLE);
                                } else {
                                    eachView.setVisibility(View.INVISIBLE);
                                }
                            }

                            break;
                        case "no":
                            for (View eachView : AplRogFragment.viewObjects.get(key)) {
                                if (eachView.getId() == (int) 1) {
//                                    ((ViewGroup) eachView.getParent()).removeView(eachView);
                                    eachView.setBackgroundResource(R.drawable.aplrectborder_no);
//                                    AplRogFragment.parent_layout.addView(eachView);
//                                    eachView.invalidate();
                                    eachView.setVisibility(View.VISIBLE);
                                } else if ((eachView.getId() == borderId) && eachView.getTag().equals(highlightedTag)) {
                                    eachView.setVisibility(View.VISIBLE);
                                } else {
                                    eachView.setVisibility(View.INVISIBLE);
                                }
                            }
                            break;
                        case "unknown":
                            for (View eachView : AplRogFragment.viewObjects.get(key)) {
                                if (eachView.getId() == (int) 1) {
//                                    ((ViewGroup) eachView.getParent()).removeView(eachView);
                                    eachView.setBackgroundResource(R.drawable.aplrectborder_unknown);
//                                    AplRogFragment.parent_layout.addView(eachView);
//                                    eachView.invalidate();
                                    eachView.setVisibility(View.VISIBLE);
                                } else if ((eachView.getId() == borderId) && eachView.getTag().equals(highlightedTag)) {
                                    eachView.setVisibility(View.VISIBLE);
                                } else {
                                    eachView.setVisibility(View.INVISIBLE);
                                }
                            }
                            break;
                    }
                } else {
                    if (null != AplRogFragment.viewObjects.get(key)) {
                        for (View eachView : AplRogFragment.viewObjects.get(key)) {
                            eachView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

        } else {

            ArrayList<String> keyList = new ArrayList<String>(AplRogFragment.viewObjects.keySet());
            for (String key : keyList) {
                if (null != AplRogFragment.viewObjects.get(key)) {
                    for (View eachView : AplRogFragment.viewObjects.get(key)) {
                        eachView.setVisibility(View.INVISIBLE);
                    }
                }

            }
        }
    }

    private String readStream(InputStream stream, int maxReadSize) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        Log.d(TAG, "readStream: " + buffer.toString());
        return buffer.toString();
    }

    private String sendDate(java.util.Date date) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String finalDate = dateFormatGmt.format(date).toString();
        Log.d(TAG, "sendDate: Date :" + finalDate);
        return String.valueOf(finalDate);
    }

    //Parsing Apl json
    private void ParseAPLJson(String finalMsg) {

        aplFilteredArray = new ArrayList<>();
        aplProductArray = new ArrayList<>();
        aplAppData = new AppData();
        uniqueAplSearchItems = new HashSet<>();
        sosFacingsAplList = new ArrayList<>();

        try {
            // start of json parsing
            Log.d(TAG, "ParseJson: " + finalMsg);
            JSONObject object = new JSONObject(finalMsg);
            JSONObject finalResult = object.getJSONObject("result");
            JSONObject appData = finalResult.getJSONObject("appData");
            String maxDimension = null;

            if (appData.has("maxDimension")) {
                maxDimension = appData.getString("maxDimension");
            }
            try {
                sa = maxDimension.split("x");
                aplAppData.canvasHeight = Float.parseFloat(sa[1]);
                aplAppData.canvasWidth = Float.parseFloat(sa[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (validCheck(appData, "stitchedImage")) {
                aplAppData.stitchedImage = appData.getString("stitchedImage");
            } else {
                throw new Exception("invalidJson");
            }


            JSONObject metaData = finalResult.getJSONObject("metaData");
            aplAppData.category = metaData.getString("category");
            category = metaData.getString("category");
            aplAppData.subCategory = metaData.getString("subCategory");
            aplAppData.cpg = metaData.getString("cpg");

//            Boolean isPrefixNotFound = true;
            ArrayList<PrefixData> prefixArray = new ArrayList<>();
            if (metaData.has("prefixes")) {
                JSONArray prefixes = metaData.getJSONArray("prefixes");
                Log.d(TAG, "Prefixes found" + prefixes);
                for (int r = 0; r < prefixes.length(); r++) {
//                    isPrefixNotFound = false;
                    JSONObject data = prefixes.getJSONObject(r);
                    Log.d(TAG, " prefix data: " + data.toString());
                    if (data.isNull("prefix") || data.isNull("brand") || data.isNull("competitor")) {
                        //need to check
                    } else {
                        if (data.getString("competitor").equals("false")) {
                            PrefixData prefixObject = new PrefixData();
                            prefixObject.prefix = data.getString("prefix");
                            prefixObject.brand = data.getString("brand");
                            prefixObject.competitor = data.getString("competitor");
                            prefixArray.add(prefixObject);
                        }
                    }

                }
                aplAppData.prefixes = prefixArray;

//                if(prefixArray.size()>0) {
//                    for (ProductData productSingleData : productsList) {
//                        for (PrefixData prefString : prefixArray) {
//                            if (productSingleData.upc.startsWith(String.valueOf(prefString))) {
//                                aplFilteredArray.add(productSingleData);
//                                SearchData tempData = new SearchData();
//                                tempData.upc = productSingleData.upc;
//                                tempData.description = productSingleData.name;
//                                tempData.uniqueTagOfProduct = productSingleData.upc;
//                                uniqueSearchItems.add(tempData);
//                            }
//                        }
//                    }
//                    searchList = new SearchData[productsList.size()];
//                    searchList = uniqueSearchItems.toArray(new SearchData[uniqueSearchItems.size()]);
//
//                    aplRecyclerViewAdapter = new AplRecyclerViewAdapter(getContext(), aplFilteredArray);
//                } else {
//                    isPrefixNotFound = true;
//                }
            }

            JSONObject aplData = finalResult.getJSONObject("apl");
            JSONArray aplItems = aplData.getJSONArray("items");
            for (int x = 0; x < aplItems.length(); x++) {
                ProductData productData = new ProductData();
                JSONObject aplDataItem = aplItems.getJSONObject(x);
                if (validCheck(aplDataItem, "present")) {
                    productData.present = aplDataItem.getString("present");
                } else {
                    throw new Exception("invalidJson");
                }

                if (aplDataItem.has("upc") && !aplDataItem.isNull("upc")) {

                    productData.upc = aplDataItem.getString("upc");

                } else {
                    throw new Exception("invalidJson");

                }

                if (aplDataItem.has("productDetails")) {
                    JSONObject productDetails = aplDataItem.getJSONObject("productDetails");
                    productData.name = productDetails.getString("productName");
                    productData.category = productDetails.getString("productCategory");
                    productData.subcategory = productDetails.getString("productSubcategory");
                    productData.brand = productDetails.getString("productBrand");
                }

                if (aplDataItem.has("facings") && !aplDataItem.isNull("facings")) {
                    JSONArray facingArray = aplDataItem.getJSONArray("facings");

                    ArrayList<RectData> rectDataArray = new ArrayList<>();
                    for (int y = 0; y < facingArray.length(); y++) {
                        JSONObject facingObject = facingArray.getJSONObject(y);
                        JSONObject rectData = facingObject.getJSONObject("rect");
                        RectData rectDataForProduct = new RectData();
                        rectDataForProduct.height_apl = Float.parseFloat(rectData.getString("height"));
                        rectDataForProduct.width_apl = Float.parseFloat(rectData.getString("width"));
                        rectDataForProduct.x_apl = Float.parseFloat(rectData.getString("x"));
                        rectDataForProduct.y_apl = Float.parseFloat(rectData.getString("y"));
                        rectDataArray.add(rectDataForProduct);
                    }
                    productData.facing = rectDataArray;
                }


                aplProductArray.add(productData);
            }

            JSONObject share = finalResult.getJSONObject("share");
            JSONObject drawSOS = share.getJSONObject("drawSOS");

            JSONArray linearGroups = drawSOS.getJSONArray("linearGroups");
            countOfSos = linearGroups.length();
            for (int i = 0; i < linearGroups.length(); i++) {
                JSONObject lg = linearGroups.getJSONObject(i);

                if (lg.has("facings")) {
                    JSONArray facings = lg.getJSONArray("facings");
                    for (int p = 0; p < facings.length(); p++) {
                        SosData sositem = new SosData();
                        JSONObject rectface = facings.getJSONObject(p);
                        if (rectface.has("rect")) {

                            JSONObject rect = rectface.getJSONObject("rect");
                            if (rect.isNull("height") || rect.isNull("width") || rect.isNull("x") || rect.isNull("y")) {
                                sositem.sos_height = Float.parseFloat("");
                                sositem.sos_width = Float.parseFloat("");
                                sositem.sos_x = Float.parseFloat("");
                                sositem.sos_y = Float.parseFloat("");
                            } else {
                                sositem.sos_height = Float.parseFloat(rect.getString("height"));
                                sositem.sos_width = Float.parseFloat(rect.getString("width"));
                                sositem.sos_x = Float.parseFloat(rect.getString("x"));
                                sositem.sos_y = Float.parseFloat(rect.getString("y"));
                            }
                            if (rectface.isNull("sos") || rectface.isNull("competitor") || rectface.isNull("upcPrefix")) {
                                sositem.sos_percent = Float.parseFloat("");
                                sositem.sos_competitor = "";

                            } else {
                                sositem.sos_percent = Float.parseFloat(rectface.getString("sos"));
                                sositem.sos_competitor = rectface.getString("competitor");

                            }
                            sositem.canvas_height = aplAppData.canvasHeight;
                            sositem.canvas_width = aplAppData.canvasWidth;
                            if (!rectface.getString("competitor").equals("unknown")) {
                                sosFacingsAplList.add(sositem);
                            }
                        } else Log.d(TAG, "Facings rect does not exist");
                    }
                }
            }


                if (null != aplAppData.prefixes && aplAppData.prefixes.size() > 0 && aplProductArray.size() > 0) {
                    for (ProductData productObject : aplProductArray) {
                        for (PrefixData prefixData : aplAppData.prefixes) {
                            if (productObject.upc.startsWith(prefixData.prefix)) {
                                aplFilteredArray.add(productObject);
                                SearchData tempData = new SearchData();
                                tempData.upc = productObject.upc;
                                tempData.description = productObject.name;
                                tempData.uniqueTagOfProduct = productObject.upc;
                                uniqueAplSearchItems.add(tempData);
                            }
                        }
                    }

            } else {
                if (aplProductArray.size() > 0) {
                    for (ProductData productObject : aplProductArray) {
                        aplFilteredArray.add(productObject);
                        SearchData tempData = new SearchData();
                        tempData.upc = productObject.upc;
                        tempData.description = productObject.name;
                        tempData.uniqueTagOfProduct = productObject.upc;
                        uniqueAplSearchItems.add(tempData);

                    }
                }
            }

            searchAplList = new SearchData[aplProductArray.size()];
            searchAplList = uniqueAplSearchItems.toArray(new SearchData[uniqueAplSearchItems.size()]);

            aplRecyclerViewAdapter = new AplRecyclerViewAdapter(getContext(), aplFilteredArray);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(aplRecyclerViewAdapter);
            aplRecyclerViewAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("invalidJson")) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setMessage("Incorrect planogram capture");
                alertDialog.setCancelable(false);
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            getActivity().onBackPressed();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            }
        }
    }

    public static void hideSortDropdown(){
        apl_spinner_view.setVisibility(View.INVISIBLE);
    }

    public void updateBitmapInProductList() {
        for (int i = 0; i < aplFilteredArray.size(); i++) {
            Bitmap b = null;
            Log.d(TAG, "Inside update, thumbhash size= " + ThumbImageThread.thumbHash.size());
            b = AplThumbImage.thumbHash.get(aplFilteredArray.get(i).upc);
            if (null != b) {
                aplFilteredArray.get(i).thumb = b;
                Log.d(TAG, "Bitmap updated to prod list" + i);
            }
        }
        aplRecyclerViewAdapter.notifyDataSetChanged();
    }

    public static void updateText() {
        int count = 0;
        for (ProductData jsonListData : aplFilteredArray) {
            if (jsonListData.present.equals("yes")) {
                count++;
            }
        }
        search_result.setText(count + " of " + aplFilteredArray.size());
    }

    private boolean validCheck(JSONObject jsonObj, String validateString) {
        if (jsonObj.has(validateString) && !jsonObj.isNull(validateString)) {
            return true;
        }
        return false;
    }
}
