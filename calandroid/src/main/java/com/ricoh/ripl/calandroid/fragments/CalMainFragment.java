package com.ricoh.ripl.calandroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ricoh.ripl.calandroid.R;
import com.ricoh.ripl.calandroid.framework.ArrayAdapter;
import com.ricoh.ripl.calandroid.framework.OcutagAuthScheme;
import com.ricoh.ripl.calandroid.framework.ProgressBarHandler;
import com.ricoh.ripl.calandroid.framework.SpinnerAdapter;
import com.ricoh.ripl.calandroid.framework.ThumbImageThread;
import com.ricoh.ripl.calandroid.model.AppData;
import com.ricoh.ripl.calandroid.model.CalData;
import com.ricoh.ripl.calandroid.model.ProductData;
import com.ricoh.ripl.calandroid.model.SearchData;
import com.ricoh.ripl.calandroid.model.SosData;
import com.ricoh.ripl.calandroid.recyclerview.CalRecyclerViewAdapter;
import com.ricoh.ripl.calandroid.utility.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
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

public class CalMainFragment extends Fragment implements OnFragmentInteractionListener, Handler.Callback {

    private static String TAG = "CalMainFragment";

    public static boolean pastJob = false;
    private boolean invalidData = false;
    private boolean isSaveOnDeviceRequired = true;
    private Context context;
    private String finalJsonResult = null;
    private Date date;
    public static String[] authHeaders;
    private URL url;
    private ArrayAdapter<String> sosadapter;
    public static HashMap<String, String> listDataSave;
    public static boolean isRogSelected = false;
    public static boolean isSortSelected = false;
    private ImageButton correctiveActions;
    public static ImageButton fitToHeight;
    public static ImageButton fitToWidth;
    private ImageButton pogView;
    private ImageButton rogView;
    private ImageButton splitView;
    private static ImageButton submitCA;
    private ImageButton backimg, sort_drop_down;
    private ImageButton sos;
    private ImageView itemImage, closesearch;
    private TextView title;
    private static TextView search_result;
    private TextView search_title;
    private TextView pog;
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
    private TextView totcatper, tototherper;
    public static FrameLayout progressBarHolder;
    public static RelativeLayout secondParent, searchresultview, splitlayout, spinner_view;
    private RelativeLayout prioritylayout, locationlayout;
    private FrameLayout sos_layout;
    public static RecyclerView recyclerView;
    private Button viewname;
    private AutoCompleteTextView search;
    public static ArrayList<Float> rectList;
    private SearchData[] searchList;
    public static List<CalData> productsList;
    public static List<CalData> upcFilteredList;
    public static List<SosData> sosFacingsList;
    private static Uri rogImage = null, pogImage = null;
    public static CalRecyclerViewAdapter calRecyclerViewAdapter;
    private String upcnumber, desc, finalImg = null;
    private Dialog builder;
    private String category = null, cpg = null, tokenn = null, host = null, appid = null, appsecret = null, devid = null, devsecret = null, upcprefix = null, pimhost = null, temppastjob = null;
    private Spinner sos_spinner;
    private TextView spinner;
    private GetData getDataTask;
    private String token;
    private RelativeLayout pogrogLayout, searchresultlayout, searchLayout, viewsLayout, titlelayout;
    private NetworkInfo networkInfo;
    private String[] sa = null;
    int curCount = 0;
    float totalCount = 0F;
    private ThreadPoolExecutor executor;
    private Button cancel, ok;
    private boolean isCorrectedData = false;
    private Button ofslegend, mflegend, pilegend;

    public static final int THREAD_INFO = 1;
    public static final int UPDATE_ADAPTER = 2;
    public ArrayAdapter<SearchData> arrayAdapter;
    public static String highlightedTag;
    public static boolean isCalSelected = true;
    private Set<SearchData> uniqueSearchItems;
    public static ArrayList<CalData> sortList;
    public static Set<String> savedList;
    public static boolean sosSelected = false;
    private final int viewId = 1, borderId = 2;
    private int countOfSos = 0;
    private JSONObject result;

    public static String selectedFragment = "";
    private static final int READ_BLOCK_SIZE = 100;

    public CalMainFragment() {
    }

    public static CalMainFragment newInstance() {
        return new CalMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        listDataSave = new HashMap<>();
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

            url = new URL("https://" + host + "/ore/api/v1/cal_json?requestToken=" + token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parameters and network error condition handling
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        View view = inflater.inflate(R.layout.fragment_cal_main, container, false);

        InitializeViews(view);

        View recyclerImage = inflater.inflate(R.layout.cal_recycler_item, container, false);
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
        Boolean isfileExists = false;
        if (null != files && files.length > 0) {
            for (File file : files) {
                if (file.getName().contains(token) && file.length() > 0) {
                    isfileExists = true;
                    loadJSONFromStorage();
                    break;
                }
            }
        }
        if (!isfileExists){
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

        submitCA.setEnabled(false);
        submitCA.setAlpha(0.3f);

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setSelected(!spinner.isSelected());

                if (spinner.isSelected()) {
                    spinner_view.setVisibility(View.VISIBLE);
                } else {
                    spinner_view.setVisibility(View.INVISIBLE);
                }
            }
        });

        sort_drop_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_drop_down.setSelected(!sort_drop_down.isSelected());

                if (sort_drop_down.isSelected()) {
                    spinner_view.setVisibility(View.VISIBLE);
                } else {
                    spinner_view.setVisibility(View.INVISIBLE);
                }
            }
        });

        prioritylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setText("Priority");

                isSortSelected = true;
                RogFragment.isViewByLocation = false;
                CalRecyclerViewAdapter.mPrioritySelected = true;
                if (sortList.size() != upcFilteredList.size()) {
                    sortList = new ArrayList<>();
                    for (CalData jsonListData : upcFilteredList) {
                        switch (jsonListData.kind) {
                            case "OSH":
                            case "OSV":
                                sortList.add(jsonListData);
                                break;

                            default:
                                break;
                        }

                    }

                    for (CalData jsonListData : upcFilteredList) {
                        switch (jsonListData.kind) {
                            case "MFH":
                            case "MFV":
                                sortList.add(jsonListData);
                                break;

                            default:
                                break;
                        }
                    }
                }

                calRecyclerViewAdapter = new CalRecyclerViewAdapter(getContext(), sortList);
                recyclerView.setAdapter(calRecyclerViewAdapter);
                calRecyclerViewAdapter.notifyDataSetChanged();

                spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        locationlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setText("Location");

                isSortSelected = false;
                RogFragment.isViewByLocation = true;
                CalRecyclerViewAdapter.mPrioritySelected = false;
                calRecyclerViewAdapter = new CalRecyclerViewAdapter(getContext(), upcFilteredList);
                recyclerView.setAdapter(calRecyclerViewAdapter);
                calRecyclerViewAdapter.notifyDataSetChanged();

                spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        viewsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        titlelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
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
                spinner_view.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                spinner_view.setVisibility(View.INVISIBLE);
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
        search_title.setText(R.string.searchTitle);
        search_title.setTextSize(14);
        search_title.setTextColor(Color.parseColor("#3648a8"));
        search_title.setVisibility(View.VISIBLE);

        search_result.setTextSize(10);
        search_result.setTextColor(Color.parseColor("#3648a8"));
        search_result.setVisibility(View.VISIBLE);
        sortby.setText(R.string.viewBy);
        sortby.setTextSize(10);
        sortby.setTextColor(Color.parseColor("#3648a8"));
        sortby.setVisibility(View.VISIBLE);

        // SOS view onclick
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
                sos.setSelected(!sos.isSelected());

                if (sos.isSelected()) {

                    if (sosFacingsList.size() == 0) {
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
                        if (!isRogSelected) {
                            rogSelection();
                        } else {
                            for (View sosrect : RogFragment.sosRects) {
                                sosrect.setVisibility(View.VISIBLE);
                            }
                            for (View sostext : RogFragment.sosText) {
                                sostext.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    sos_layout.setVisibility(View.INVISIBLE);
                    sosSelected = false;
                    for (View sosrect : RogFragment.sosRects) {
                        sosrect.setVisibility(View.INVISIBLE);
                    }
                    for (View sostext : RogFragment.sosText) {
                        sostext.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        // CA view onclick
        correctiveActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View imageButton) {
                spinner_view.setVisibility(View.INVISIBLE);
                correctiveActions.setSelected(!correctiveActions.isSelected());

                isCalSelected = correctiveActions.isSelected();
//                rogView.setClickable(false);
                updateViewsLayer(CalRecyclerViewAdapter.mValuesFiltered);

            }
        });

        //icon view onclick
        pogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pogButton) {
                spinner_view.setVisibility(View.INVISIBLE);
                pogView.setSelected(!pogView.isSelected());
                try {
                    if (pogView.isSelected()) {
                        calRecyclerViewAdapter.notifyDataSetChanged();

                        sos.setSelected(false);
                        sos.setEnabled(false);
                        sos_layout.setVisibility(View.INVISIBLE);
                        sosSelected = false;
                        pogView.setClickable(false);
                        rogView.setClickable(true);
                        rogView.setSelected(false);
                        splitView.setClickable(true);
                        splitView.setSelected(false);
                        correctiveActions.setSelected(false);
                        correctiveActions.setEnabled(false);
                        viewname.setText("Pog");

                        Fragment fragment = PogFragment.newInstance(pogImage);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.commitAllowingStateLoss();
                        transaction.replace(R.id.pogrogLayout, fragment, "pogfragment");
                        selectedFragment = "pog";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //rog view onclick
        rogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rogButton) {
                spinner_view.setVisibility(View.INVISIBLE);
                rogView.setSelected(!rogView.isSelected());
                try {
                    if (rogView.isSelected()) {
                        rogSelection();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //split view onclick
        splitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View splitButton) {
                spinner_view.setVisibility(View.INVISIBLE);
                splitView.setSelected(!splitView.isSelected());
                try {
                    if (splitView.isSelected()) {
                        isRogSelected = false;
                        CalRecyclerViewAdapter.isRogActive = false;
                        calRecyclerViewAdapter.notifyDataSetChanged();

                        sos.setSelected(false);
                        sos.setEnabled(true);
                        sos_layout.setVisibility(View.INVISIBLE);
                        sosSelected = false;
                        splitView.setClickable(false);
                        pogView.setClickable(true);
                        pogView.setSelected(false);
                        rogView.setClickable(true);
                        rogView.setSelected(false);
                        correctiveActions.setEnabled(true);
                        if (isCalSelected) {
                            correctiveActions.setSelected(true);
                        }
                        viewname.setText("Pog");

                        Fragment fragment = SplitFragment.newInstance(rogImage, pogImage);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.commitAllowingStateLoss();
                        transaction.replace(R.id.pogrogLayout, fragment, "splitfragment");
                        selectedFragment = "split";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        fitToHeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fitToHeight.setSelected(true);
                fitToWidth.setSelected(false);
                switch (selectedFragment) {
                    case "rog":
                        RogFragment.fitToHeight();
                        break;
                    case "pog":
                        PogFragment.fitToHeight();
                        break;
                    case "split":
                        SplitFragment.fitToHeight();
                        break;
                }

            }

        });
        fitToWidth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fitToHeight.setSelected(false);
                fitToWidth.setSelected(true);
                switch (selectedFragment) {
                    case "rog":
                        RogFragment.fitToWidth();
                        break;
                    case "pog":
                        PogFragment.fitToWidth();
                        break;
                    case "split":
                        SplitFragment.fitToWidth();
                        break;
                }

            }

        });

        // submit CA
        submitCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_view.setVisibility(View.INVISIBLE);

                Log.d(TAG, "onClick of submit button: ");
                try {
                    builder = new Dialog(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View uncorrectedview = inflater.inflate(R.layout.submit_incorrected_dialog, null);
                    View correctedview = inflater.inflate(R.layout.submit_corrected_dialog, null);
                    TextView unresolvedtext = uncorrectedview.findViewById(R.id.submittext);
                    TextView resolvedtext = correctedview.findViewById(R.id.submittext);
                    int size = 0;
                    if (isSortSelected) {
                        size = sortList.size() - savedList.size();
                    } else {
                        size = upcFilteredList.size() - savedList.size();
                    }

                    if (size > 0) {
                        unresolvedtext.setText(size + " issues remain unresolved,continue?");
                        cancel = uncorrectedview.findViewById(R.id.cancel);
                        ok = uncorrectedview.findViewById(R.id.ok);
                        builder.setContentView(uncorrectedview);
                        builder.show();
                    } else {
                        resolvedtext.setText("All issues resolved,continue?");
                        cancel = correctedview.findViewById(R.id.cancel);
                        ok = correctedview.findViewById(R.id.ok);
                        builder.setContentView(correctedview);
                        builder.show();
                    }
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
                                ProgressBarHandler.loadProgressScreen(getActivity());
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

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        secondParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
            }
        });

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
            ParseJson(jsonData);
            updateUIInfo();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private JSONObject createJsonData() throws JSONException {

        String corrected = "false";

        JSONObject object = new JSONObject(finalJsonResult);
        JSONObject result = object.getJSONObject("result");
        JSONObject correctiveActions = result.getJSONObject("correctiveActions");
        JSONArray regions = correctiveActions.getJSONArray("regions");
        if (regions.length() > 0) {
            for (int i = 0; i < regions.length(); i++) {
                JSONObject reg = regions.getJSONObject(i);
                if (reg.has("ca")) {
                    JSONArray ca = reg.getJSONArray("ca");
                    for (int j = 0; j < ca.length(); j++) {
                        JSONObject items = ca.getJSONObject(j);
                        JSONObject rect_ca = items.getJSONObject("rect");
                        for (CalData eachData : productsList) {
                            if (eachData.upc.equals(items.getString("upc")) && eachData.x_ca == Float.valueOf(rect_ca.getString("x")) && eachData.y_ca == Float.valueOf(rect_ca.getString("y"))) {

                                if (eachData.edited == 1 || eachData.edited == 2 || eachData.edited == 3) {
                                    corrected = "true";
                                }

                                if (null != eachData.editSelected) {
                                    switch (eachData.editSelected) {
                                        case "Corrected":
                                            items.put("action", eachData.editSelected);
                                            items.put("corrected", corrected);
                                            items.put("comment", eachData.edittextData);
                                            break;

                                        case "Not Corrected":
                                            String commentString = "";
                                            items.put("action", eachData.editSelected);
                                            items.put("corrected", corrected);
                                            if (eachData.onorderchecked) {
                                                commentString = "On order";
                                            }
                                            if (eachData.notaccessiblechecked) {
                                                commentString += ",Not accessible";
                                            }
                                            if (eachData.notimechecked) {
                                                commentString += ",No time";
                                            }
                                            if (eachData.mgrrefusedchecked) {
                                                commentString += ",Manager refused";
                                            }
                                            if (eachData.mgrunavailablechecked) {
                                                commentString += ",Manager not available";
                                            }

                                            break;

                                        case "Invalid Action":
                                            items.put("action", eachData.editSelected);
                                            items.put("corrected", corrected);
                                            items.put("comment", eachData.edittextData);
                                            break;

                                        default:
                                            items.put("action", null);
                                            items.put("corrected", false);
                                            break;
                                    }
                                }

                            }
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

    private void calculationOfTotalPercentSOS() {
        float competitorPercent = 0;
        float cpgPercent = 0;

        for (SosData sosJsonList : sosFacingsList) {
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

    private void rogSelection() {

        isRogSelected = true;
        CalRecyclerViewAdapter.isRogActive = true;
        calRecyclerViewAdapter.notifyDataSetChanged();

        rogView.setSelected(true);

        sos.setEnabled(true);
        rogView.setClickable(false);
        pogView.setClickable(true);
        pogView.setSelected(false);
        splitView.setClickable(true);
        splitView.setSelected(false);
        correctiveActions.setEnabled(true);
        if (isCalSelected) {
            correctiveActions.setSelected(true);
        }
        viewname.setText("Rog");

        Fragment fragment = RogFragment.newInstance(rogImage);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.commitAllowingStateLoss();
        transaction.replace(R.id.pogrogLayout, fragment);
        selectedFragment = "rog";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getDataTask != null) {
            getDataTask.cancel(false);
            getDataTask = null;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void InitializeViews(View view) {
        viewname = view.findViewById(R.id.viewname);
        searchresultlayout = view.findViewById(R.id.searchresultlayout);
        backimg = view.findViewById(R.id.backimg);
        searchname = view.findViewById(R.id.searchname);
        searchcount = view.findViewById(R.id.searchcount);

        titlelayout = view.findViewById(R.id.titlelayout);
        viewsLayout = view.findViewById(R.id.viewsLayout);
        searchLayout = view.findViewById(R.id.searchLayout);
        prioritylayout = view.findViewById(R.id.prioritylayout);
        locationlayout = view.findViewById(R.id.locationlayout);
        spinner_view = view.findViewById(R.id.spinner_view);
        sort_drop_down = view.findViewById(R.id.sort_drop_down);
        totcatper = view.findViewById(R.id.totcatper);
        tototherper = view.findViewById(R.id.tototherper);
        textcatname = view.findViewById(R.id.textcatname);
        textothername = view.findViewById(R.id.textothername);
        sos_layout = view.findViewById(R.id.sos_layout);
        sos = view.findViewById(R.id.sos);
        categoryname = view.findViewById(R.id.categoryname);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        progresstext = view.findViewById(R.id.progresstext);
        closesearch = view.findViewById(R.id.closesearch);
        searchresultview = view.findViewById(R.id.searchresultview);
        correctiveActions = view.findViewById(R.id.ca);
        pogView = view.findViewById(R.id.pogView);
        rogView = view.findViewById(R.id.rogView);
        splitView = view.findViewById(R.id.splitView);
        submitCA = view.findViewById(R.id.submitCA);
        fitToHeight = view.findViewById(R.id.fittoheight);
        fitToWidth = view.findViewById(R.id.fittowidth);
        pogrogLayout = view.findViewById(R.id.pogrogLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        search = view.findViewById(R.id.search_box);

        title = view.findViewById(R.id.title);
        search_title = view.findViewById(R.id.searchTitle);
        search_result = view.findViewById(R.id.searchResult);
        sortby = view.findViewById(R.id.sortBy);

        spinner = view.findViewById(R.id.sortspinner);
        sos_spinner = view.findViewById(R.id.sos_spinner);
        secondParent = view.findViewById(R.id.secondParent);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case THREAD_INFO:
                curCount++;
                totalCount = upcFilteredList.size();
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

    //////////////////////////////// Getting Data///////////////////////
    private class GetData extends AsyncTask<String, Void, String> {

        String responseMsg = null;
        String finalMsg = null;
        String sendMsg = null;
        int responseCode = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBarHandler.loadProgressScreen(getActivity());
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
                ParseJson(result);
                updateUIInfo();

            }
        }
    }

    private void updateUIInfo() {
        categoryname.setText(" " + category);
        categoryname.setTextSize(18);
        categoryname.setVisibility(View.VISIBLE);

        correctiveActions.setSelected(true);
        isCalSelected = true;
        rogView.setSelected(true);
        if (isSortSelected) {
            search_result.setText(" " + sortList.size() + "  issues found");
            if (savedList.size() > 0) {
                search_result.setText(savedList.size() + " of " + sortList.size() + "  issues fixed");
            }
        } else {
            search_result.setText(" " + upcFilteredList.size() + "  issues found");
            if (savedList.size() > 0) {
                search_result.setText(savedList.size() + " of " + upcFilteredList.size() + "  issues fixed");
            }
        }

        viewname.setClickable(false);
        viewname.setOnClickListener(null);
        viewname.setText("Rog");

        for (int j = 0; j < upcFilteredList.size(); j++) {
            String upc = upcFilteredList.get(j).upc;
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
            if (((networkInfo != null) && (networkInfo.isConnected()))) {
                executor.execute(new ThumbImageThread(j, getActivity(), upc, pimhost, new Handler(CalMainFragment.this)));
            } else {
                Toast.makeText(getActivity(), "Please verify network connectivity.If problems persist,please contact technical support", Toast.LENGTH_SHORT).show();
            }
        }

        if (!invalidData && getActivity() != null) {
            try {
                CalRecyclerViewAdapter.isRogActive = true;
                Fragment fragment = RogFragment.newInstance(rogImage);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.commitAllowingStateLoss();
                transaction.replace(R.id.pogrogLayout, fragment, "rogfragment");
                selectedFragment = "rog";
                isRogSelected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != searchList && getActivity() != null) {
            arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.search_dropdown, searchList);
            search.setThreshold(1);
            search.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
            search.setAdapter(arrayAdapter);
        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
            }
        });

        search.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    spinner_view.setVisibility(View.INVISIBLE);

                    String s = search.getText().toString();
                    search.dismissDropDown();
                    searchname.setText("Products Containing '" + s + "'");

                    List<CalData> filteredJsonList = new ArrayList<>();
                    if (s.length() > 0) {
                        for (int searchposition = 0; searchposition < arrayAdapter.getCount(); searchposition++) {
                            SearchData dataForSearch = arrayAdapter.getItem(searchposition);
                            for (CalData tempData : productsList) {
                                if (tempData.uniqueTagOfProduct.equalsIgnoreCase(dataForSearch.uniqueTagOfProduct)) {
                                    filteredJsonList.add(tempData);
                                    break;
                                }
                            }
                        }
                    }
                    calRecyclerViewAdapter.mValuesFiltered = filteredJsonList;

                    searchcount.setText(filteredJsonList.size() + " Products Found");

                    calRecyclerViewAdapter.notifyDataSetChanged();
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
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    spinner_view.setVisibility(View.INVISIBLE);

                    closesearch.setVisibility(View.VISIBLE);
                    searchresultlayout.setVisibility(View.VISIBLE);
                    searchresultview.setVisibility(View.INVISIBLE);

                    calRecyclerViewAdapter.mValuesFiltered = upcFilteredList;

                    calRecyclerViewAdapter.notifyDataSetChanged();
                    updateViewsLayer(upcFilteredList);

                    if (search.getText().toString().isEmpty()) {
                        closesearch.setVisibility(View.INVISIBLE);
                    }
                }
                return false;
            }
        });

        closesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_view.setVisibility(View.INVISIBLE);
                if (isSortSelected) {
                    calRecyclerViewAdapter.mValuesFiltered = sortList;
                } else {
                    calRecyclerViewAdapter.mValuesFiltered = upcFilteredList;
                }

                calRecyclerViewAdapter.notifyDataSetChanged();
                updateViewsLayer(upcFilteredList);
                search.setText("");
                closesearch.setVisibility(View.INVISIBLE);
                searchresultlayout.setVisibility(View.VISIBLE);
                searchresultview.setVisibility(View.GONE);

                InputMethodManager inputManager = (InputMethodManager) getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spinner_view.setVisibility(View.INVISIBLE);

                SearchData tempSearch = (SearchData) parent.getItemAtPosition(position);
                ArrayList<CalData> filteredList = new ArrayList<>();
                for (CalData tempCalData : upcFilteredList) {
                    if (tempCalData.uniqueTagOfProduct.equalsIgnoreCase(tempSearch.uniqueTagOfProduct)) {
                        filteredList.add(tempCalData);
                        break;
                    }
                }
                calRecyclerViewAdapter.mValuesFiltered = filteredList;

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
                searchcount.setText(CalRecyclerViewAdapter.mValuesFiltered.size() + " Products Found");


                calRecyclerViewAdapter.notifyDataSetChanged();
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
                spinner_view.setVisibility(View.INVISIBLE);

                if (isSortSelected) {
                    calRecyclerViewAdapter.mValuesFiltered = sortList;
                } else {
                    calRecyclerViewAdapter.mValuesFiltered = upcFilteredList;
                }

                calRecyclerViewAdapter.notifyDataSetChanged();
                updateViewsLayer(upcFilteredList);

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

    private class SendData extends AsyncTask<String, Void, Void> {
        String responseMsg = null;
        int responseCode = 0;
        HttpsURLConnection con;
        DataOutputStream wr;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL hostURL = new URL("https://" + host + "/ore/api/v1/cal_json?requestToken=" + tokenn);

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
                                ProgressBarHandler.unloadProgressScreen(getActivity());
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

    // Parsing the Json response for given token
    private void ParseJson(String finalMsg) {

        productsList = new ArrayList<>();
        upcFilteredList = new ArrayList<>();
        rectList = new ArrayList<>();
        uniqueSearchItems = new HashSet<>();
        try {
            // start of json parsing
            Log.d(TAG, "ParseJson: " + finalMsg);
            JSONObject object = new JSONObject(finalMsg);
            result = object.getJSONObject("result");
            JSONObject appData = result.getJSONObject("appData");
            String maxDimension = null;
            float maxOfWH = 0.f;
            if (appData.has("maxDimension")) {
                maxDimension = appData.getString("maxDimension");
            }

            try {
                sa = maxDimension.split("x");
                maxOfWH = Float.parseFloat(sa[1]);
                if (Float.parseFloat(sa[0]) >= Float.parseFloat(sa[1])) {
                    maxOfWH = Float.parseFloat(sa[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject correctiveActions = result.getJSONObject("correctiveActions");
            JSONObject canvas = correctiveActions.getJSONObject("canvas");
            String canvas_height = canvas.getString("height");
            String canvas_width = canvas.getString("width");
            JSONArray regions = correctiveActions.getJSONArray("regions");
            if (regions.length() > 0) {
                for (int i = 0; i < regions.length(); i++) {
                    JSONObject reg = regions.getJSONObject(i);

                    if (reg.has("ca")) {
                        JSONArray ca = reg.getJSONArray("ca");
                        for (int j = 0; j < ca.length(); j++) {
                            CalData item = new CalData();
                            SearchData searchItem = new SearchData();
                            JSONObject items = ca.getJSONObject(j);
                            item.kind = items.getString("kind");
                            if (items.has("action") && !items.isNull("action")) {
                                String actionString = items.getString("action");
                                item.editSelected = actionString;
                                switch (actionString) {
                                    case "Invalid Action":
                                        item.edited = 3;
                                        if (items.has("comment") && !items.isNull("comment")) {
                                            item.ifCommentsAdded = true;
                                            item.edittextData = items.getString("comment");
                                        }
                                        break;
                                    case "Not Corrected":
                                        item.edited = 2;
                                        if (items.has("comment") && !items.isNull("comment")) {
                                            String notCorrectedString = items.getString("comment");
                                            String[] notCorrectedList = notCorrectedString.split(",");
                                            for (String splitStr : notCorrectedList) {
                                                switch (splitStr) {
                                                    case "On order":
                                                        item.onorderchecked = true;
                                                        break;
                                                    case "Not accessible":
                                                        item.notaccessiblechecked = true;
                                                        break;
                                                    case "No time":
                                                        item.notimechecked = true;
                                                        break;
                                                    case "Manager refused":
                                                        item.mgrrefusedchecked = true;
                                                        break;
                                                    case "Manager not available":
                                                        item.mgrunavailablechecked = true;
                                                        break;
                                                }

                                            }
                                        }
                                        break;
                                    case "Corrected":
                                        item.edited = 1;
                                        if (items.has("comment") && !items.isNull("comment")) {
                                            item.edittextData = items.getString("comment");
                                            if (item.edittextData.length() > 0) {
                                                item.ifCommentsAdded = true;
                                            }
                                        }
                                        break;
                                }
                            }
                            if (items.has("popFacingIndex")) {
                                JSONObject popFacingIndex = items.getJSONObject("popFacingIndex");
                                String facing_popFacingIndex = popFacingIndex.getString("facing");
                                String lg_popFacingIndex = popFacingIndex.getString("lg");
                            } else Log.d(TAG, "popFacingIndex does not exist");
                            if (items.has("posFacingIndex")) {
                                JSONObject posFacingIndex = items.getJSONObject("posFacingIndex");
                                String facing_posFacingIndex = posFacingIndex.getString("facing");
                                String lg_posFacingIndex = posFacingIndex.getString("lg");
                            } else Log.d(TAG, "posFacingIndex does not exist");

                            String score = items.getString("score");
                            if (items.has("upc") && !items.isNull("upc")) {
                                item.upc = items.getString("upc");
                                searchItem.upc = items.getString("upc");
                            } else {
                                invalidData = true;
                                break;
                            }

                            if (items.has("upc_found")) {
                                JSONArray upc_found = items.getJSONArray("upc_found");
                                for (int k = 0; k < upc_found.length(); k++) {
                                    JSONArray upc_array = upc_found.getJSONArray(k);
                                    for (int l = 0; l < upc_array.length(); l++) {
                                        String[] arrayItems = new String[upc_array.length()];
                                        arrayItems[l] = upc_array.getString(l);
                                        Log.d(TAG, "UPC array items: " + arrayItems[l]);
                                    }
                                }
                            } else {
                                Log.d(TAG, "upc_found does not exist");
                            }

                            if (items.has("rect")) {
                                JSONObject rect_ca = items.getJSONObject("rect");
                                String height_ca = rect_ca.getString("height");
                                String width_ca = rect_ca.getString("width");
                                String x_ca = rect_ca.getString("x");
                                String y_ca = rect_ca.getString("y");
                                JSONObject productDetails = items.getJSONObject("productDetails");
                                item.description = productDetails.getString("productName");
                                searchItem.description = productDetails.getString("productName");
                                desc = item.description;
                                item.category = productDetails.getString("productCategory");
                                item.subCategory = productDetails.getString("productSubcategory");
                                item.brand = productDetails.getString("productBrand");

                                item.width = Float.parseFloat(sa[0]);
                                item.height = Float.parseFloat(sa[1]);

                                item.x_ca = Float.parseFloat(x_ca);
                                item.y_ca = Float.valueOf(y_ca);
                                item.width_ca = Float.valueOf(width_ca);
                                item.height_ca = Float.valueOf(height_ca);
                                item.rectF = new RectF(item.x_ca, item.y_ca, item.width_ca, item.height_ca);
                                item.canvas_height = Float.parseFloat(canvas_height);
                                item.canvas_width = Float.parseFloat(canvas_width);
                                item.uniqueTagOfProduct = item.upc + "|" + UtilityClass.convertFloatToString(item.x_ca) + "|" + UtilityClass.convertFloatToString(item.y_ca);
                            } else {
                                invalidData = true;
                                break;
                            }
                            productsList.add(item);
                            listDataSave.put(item.upc, "");
                        }
                    }
                    if (invalidData) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage("Incompatible recognition results(error code:RW260)");
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
                        break;
                    }
                }

            }
            JSONObject share = result.getJSONObject("share");
            JSONObject drawSOS = share.getJSONObject("drawSOS");

            JSONArray linearGroups = drawSOS.getJSONArray("linearGroups");
            countOfSos = linearGroups.length();
            for (int i = 0; i < linearGroups.length(); i++) {
                JSONObject lg = linearGroups.getJSONObject(i);

                if (lg.has("rect")) {
                    JSONObject rect = lg.getJSONObject("rect");
                    if (rect.isNull("height") || rect.isNull("width") || rect.isNull("x") || rect.isNull("y")) {
                        String rect_height = "";
                        String rect_width = "";
                        String rect_x = "";
                        String rect_y = "";
                    } else {
                        String rect_height = rect.getString("height");
                        String rect_width = rect.getString("width");
                        String rect_x = rect.getString("x");
                        String rect_y = rect.getString("y");
                    }
                } else Log.d(TAG, "LG rect does not exist");

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
                                String sos_upcPrefix = "";

                            } else {
                                sositem.sos_percent = Float.parseFloat(rectface.getString("sos"));
                                sositem.sos_competitor = rectface.getString("competitor");
                                String sos_upcPrefix = rectface.getString("upcPrefix");
                            }
                            sositem.canvas_height = Float.parseFloat(canvas_height);
                            sositem.canvas_width = Float.parseFloat(canvas_width);
                            if (!rectface.getString("competitor").equals("unknown")) {
                                sosFacingsList.add(sositem);
                            }
                        } else Log.d(TAG, "Facings rect does not exist");
                    }
                }
            }
            JSONObject metaData = result.getJSONObject("metaData");
            category = metaData.getString("category");
            String subCategory = metaData.getString("subCategory");
            String storeIdNo = metaData.getString("storeId");
            String bannerName = metaData.getString("bannerName");
            rogImage = Uri.parse(metaData.getString("rogImage"));
            pogImage = Uri.parse(metaData.getString("pogImage"));
            cpg = metaData.getString("cpg");
            String shelfIdNo = metaData.getString("shelfId");
            String prefix = null;
            String competitor = null;

            Boolean isPrefixNotFound = true;
            if (metaData.has("prefixes")) {
                ArrayList<String> prefixArray = new ArrayList<>();
                JSONArray prefixes = metaData.getJSONArray("prefixes");
                Log.d(TAG, "Prefixes found" + prefixes);
                for (int r = 0; r < prefixes.length(); r++) {
                    isPrefixNotFound = false;
                    JSONObject data = prefixes.getJSONObject(r);
                    Log.d(TAG, " prefix data: " + data.toString());
                    if (data.isNull("prefix") || data.isNull("brand") || data.isNull("competitor")) {
                        prefix = "";
                        String brand = "";
                        competitor = "";
                    } else {
                        prefix = data.getString("prefix");
                        String brand = data.getString("brand");
                        competitor = data.getString("competitor");
                        if (competitor.equals("false")) {
                            prefixArray.add(prefix);
                        }
                    }
                    Log.d(TAG, "Compitetor: " + competitor);

                }
                if (prefixArray.size() > 0) {
                    for (CalData productSingleData : productsList) {
                        for (String prefString : prefixArray) {
                            if (productSingleData.upc.startsWith(prefString)) {
                                upcFilteredList.add(productSingleData);
                                SearchData tempData = new SearchData();
                                tempData.upc = productSingleData.upc;
                                tempData.description = productSingleData.description;
                                tempData.uniqueTagOfProduct = productSingleData.uniqueTagOfProduct;
                                uniqueSearchItems.add(tempData);
                            }
                        }
                    }
                    searchList = new SearchData[productsList.size()];
                    searchList = uniqueSearchItems.toArray(new SearchData[uniqueSearchItems.size()]);

                    calRecyclerViewAdapter = new CalRecyclerViewAdapter(getContext(), upcFilteredList);
                } else {
                    isPrefixNotFound = true;
                }
            }
            if (isPrefixNotFound) {
                Log.d(TAG, "No Prefixes found");
                upcFilteredList = productsList;
                calRecyclerViewAdapter = new CalRecyclerViewAdapter(getContext(), productsList);
                for (CalData productSingleData : productsList) {
                    SearchData tempData = new SearchData();
                    tempData.upc = productSingleData.upc;
                    tempData.description = productSingleData.description;
                    tempData.uniqueTagOfProduct = productSingleData.uniqueTagOfProduct;
                    uniqueSearchItems.add(tempData);
                }
                searchList = new SearchData[productsList.size()];
                searchList = uniqueSearchItems.toArray(new SearchData[uniqueSearchItems.size()]);
            }
            // end of json parsing

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(calRecyclerViewAdapter);

            if (upcFilteredList.size() == 0) {
                submitCA.setEnabled(true);
                submitCA.setAlpha(1f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateText() {
        if (savedList.size() > 0) {
            if (isSortSelected) {
                search_result.setText(savedList.size() + " of " + sortList.size() + "  issues fixed");
                search_result.setPadding(7, 55, 0, 5);
                submitCA.setEnabled(true);
                submitCA.setAlpha(1f);
            } else {
                search_result.setText(savedList.size() + " of " + upcFilteredList.size() + "  issues fixed");
                search_result.setPadding(7, 55, 0, 5);
                submitCA.setEnabled(true);
                submitCA.setAlpha(1f);
            }

        }
    }

    public static void hideSortDropdown() {
        spinner_view.setVisibility(View.INVISIBLE);
    }

    public void updateBitmapInProductList() {
        for (int i = 0; i < upcFilteredList.size(); i++) {
            Bitmap b = null;
            Log.d(TAG, "Inside update, thumbhash size= " + ThumbImageThread.thumbHash.size());
            b = ThumbImageThread.thumbHash.get(upcFilteredList.get(i).upc);
            if (null != b) {
                upcFilteredList.get(i).thumb = b;
                Log.d(TAG, "Bitmap updated to prod list" + i);
            }
        }
        calRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void updateViewsLayer(List<CalData> listDisplayed) {
        if (isCalSelected) {
            List<String> uniqueTagList = new ArrayList<>();
            for (CalData singleCalData : listDisplayed) {
                uniqueTagList.add(singleCalData.uniqueTagOfProduct);
            }
            List<String> keyList = new ArrayList<>(RogFragment.viewObjects.keySet());
            for (String key : keyList) {
                if (uniqueTagList.contains(key) && (null != RogFragment.viewObjects.get(key))) {
                    for (View eachView : RogFragment.viewObjects.get(key)) {
                        if (eachView.getId() == viewId) {
                            eachView.setVisibility(View.VISIBLE);
                        } else if ((eachView.getId() == borderId) && eachView.getTag().equals(highlightedTag)) {
                            eachView.setVisibility(View.VISIBLE);
                        } else {
                            eachView.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    if (null != RogFragment.viewObjects.get(key)) {
                        for (View eachView : RogFragment.viewObjects.get(key)) {
                            eachView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        } else {

            ArrayList<String> keyList = new ArrayList<String>(RogFragment.viewObjects.keySet());
            for (String key : keyList) {
                if (null != RogFragment.viewObjects.get(key)) {
                    for (View eachView : RogFragment.viewObjects.get(key)) {
                        eachView.setVisibility(View.INVISIBLE);
                    }
                }

            }
        }
    }

}
