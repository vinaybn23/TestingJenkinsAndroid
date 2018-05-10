package com.ricoh.ripl.cal;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ricoh.ripl.calandroid.fragments.AplMainFragment;
import com.ricoh.ripl.calandroid.fragments.CalMainFragment;
import com.ricoh.ripl.calandroid.fragments.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private String token;
    private String upc_prefix;
    private String appid;
    private String appsecret;
    private String devid;
    private String devsecret;
    private String host;
    private String pimhost;
    private String pastjob;
    private NetworkInfo networkInfo;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button button4, button5, button6, button7, button8, button9, button10, button11, button12, button13, button14;
    CalMainFragment fragmentManagerActivity;
//    AplMainFragment fragmentManagerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing the shared prefs
        pref = getApplicationContext().getSharedPreferences("RI_CAL_PREF", 0); // 0 - for private mode
        editor = pref.edit();

        token = pref.getString("token", ""); // getting String
        upc_prefix = pref.getString("upc_prefix", ""); // getting String
        appid = pref.getString("appid", ""); // getting String
        appsecret = pref.getString("appsecret", ""); // getting String
        devid = pref.getString("devid", ""); // getting String
        devsecret = pref.getString("devsecret", ""); // getting String
        host = pref.getString("host", ""); // getting String
        pimhost = pref.getString("pimhost", ""); // getting String
        pastjob = pref.getString("pastjob", ""); // getting String

        // Setting click listeners
        // Home
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CAL launch fragment disable
//                Toast.makeText(MainActivity.this,"Clicked Home",Toast.LENGTH_LONG).show();
                unloadFragment();

            }
        });

        // CAL
        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CAL launching fragment enable
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
                if (((networkInfo != null) && (networkInfo.isConnected()))) {
                    loadFragment();
                } else {
                    Toast.makeText(MainActivity.this, "Please verify network connectivity.If problems persist,please contact technical support", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Token
        button6 = (Button) findViewById(R.id.button6);
        button6.setText("TOKEN:" + token);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("TOKEN");
            }
        });

        // UPC Prefix
        button7 = (Button) findViewById(R.id.button7);
        button7.setText("UPC Prefixes (dash-delimited):" + upc_prefix);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("UPC");
            }
        });

        // App secret
        button8 = (Button) findViewById(R.id.button8);
        button8.setText("appSecret:" + appsecret);

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("APPSEC");
            }
        });

        //app id
        button9 = (Button) findViewById(R.id.button9);
        button9.setText("appId:" + appid);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("APPID");
            }
        });

        //
        button10 = (Button) findViewById(R.id.button10);
        button10.setText("PIM Host:" + pimhost);
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("PIMHOST");
            }
        });

        //
        button11 = (Button) findViewById(R.id.button11);
        button11.setText("Host:" + host);
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("HOST");
            }
        });

        // devsecret
        button12 = (Button) findViewById(R.id.button12);
        button12.setText("devSecret:" + devsecret);
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("DEVSEC");
            }
        });

        // dev id
        button13 = (Button) findViewById(R.id.button13);
        button13.setText("devId:" + devid);
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("DEVID");
            }
        });

        button14 = (Button) findViewById(R.id.button14);
        button14.setText("Past Job:" + pastjob);
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog("PASTJOB");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.button5).setVisibility(View.VISIBLE);
    }

    private void loadFragment() {
        findViewById(R.id.caldisplay).setVisibility(View.VISIBLE);
        findViewById(R.id.button5).setVisibility(View.INVISIBLE);
        fragmentManagerActivity = new CalMainFragment();
//        fragmentManagerActivity = new AplMainFragment();
        Bundle args = new Bundle();

        // default
//        args.putString("token", pref.getString("token", ""));
//        args.putString("host", pref.getString("host", ""));
//        args.putString("appid", pref.getString("appid", ""));
//        args.putString("appsecret", pref.getString("appsecret", ""));
//        args.putString("devid", pref.getString("devid", ""));
//        args.putString("devsecret", pref.getString("devsecret", ""));
//        args.putString("pimhost", pref.getString("pimhost", ""));
//        args.putString("pastjob", pref.getString("pastjob", ""));

        // cal token
//        args.putString("token", pref.getString("token", "MWFUXSVY")); // correctly working
//        args.putString("host", pref.getString("host", "rirecog-int3.ocutag.com"));
//        args.putString("appid", pref.getString("appid", "5a4aefadd2a8569915dcffeb18f6ef71b36db41d9c54"));
//        args.putString("appsecret", pref.getString("appsecret", "7dd01c5cd252321942c0c0c9b74acf6081dfc3f459d2df14cf60474a0f3563"));
//        args.putString("devid", pref.getString("devid", "4bf1848c4b20b264e6cede343b0d94ee7c942b421675"));
//        args.putString("devsecret", pref.getString("devsecret", "37ac079c3a7058543968f8cd3706cbbcb63aa357d006c271ef5255ee12e937"));
//        args.putString("pimhost", pref.getString("pimhost", "ripim-int3.ocutag.com"));
//        args.putString("pastjob", pref.getString("pastjob", ""));

//        args.putString("token", pref.getString("token", "1LALRGVP")); // if json incorrect
//        args.putString("host", pref.getString("host", "rirecog-test1.ocutag.com"));
//        args.putString("appid", pref.getString("appid", "f766f1c40e915b8b2fde8fa8b5e59eb015cbb34c18fb"));
//        args.putString("appsecret", pref.getString("appsecret", "b96575bb9967a0dca28a25555a86d0bc58d3ae9fcbeedb00432db735bab38d"));
//        args.putString("devid", pref.getString("devid", "b5a87107c3358a4e3dc62aa96205ff17fc1e54ed739c"));
//        args.putString("devsecret", pref.getString("devsecret", "1ac1611ae42b7e1529cb5bc4c13e4a984f649cd0c538a056945e3663bf4a44"));
//        args.putString("pimhost", pref.getString("pimhost", "ripim-test1.ocutag.com"));
//        args.putString("pastjob", pref.getString("pastjob", ""));
        // cal token
//        args.putString("token", pref.getString("token", "XWBH28OE")); // no sos
////        args.putString("token", pref.getString("token", "JTCEVDNI")); // pog image dont load
//        args.putString("host", pref.getString("host", "ri-prod-orers.ocutag.com"));
//        args.putString("appid", pref.getString("appid", "18523d8e81c6fa0725a0176457f5168c8620645d2b2e"));
//        args.putString("appsecret", pref.getString("appsecret", "932f61e7ff4d0b4278bff63302ef41dbe7e4d5e271dd7d8bcf9f2963782302"));
//        args.putString("devid", pref.getString("devid", "395bbe5e437cebcbc961ce8ef781f1ddda0d87630044"));
//        args.putString("devsecret", pref.getString("devsecret", "e091fd22ac4f5b0de49536eaaa799b6e1554427108f15f881486c27ff1c72e"));
//        args.putString("pimhost", pref.getString("pimhost", "ri-prod-pim.ocutag.com"));
//        args.putString("pastjob", pref.getString("pastjob", ""));


        args.putString("token", pref.getString("token", "GTMGGVGU")); // sos not drawing in case of prefix not matched
//        args.putString("token", pref.getString("token", "D2GFKEOG"));
        args.putString("host", pref.getString("host", "rirecog-int3.ocutag.com"));
        args.putString("appid", pref.getString("appid", "5a4aefadd2a8569915dcffeb18f6ef71b36db41d9c54"));
        args.putString("appsecret", pref.getString("appsecret", "7dd01c5cd252321942c0c0c9b74acf6081dfc3f459d2df14cf60474a0f3563"));
        args.putString("devid", pref.getString("devid", "4bf1848c4b20b264e6cede343b0d94ee7c942b421675"));
        args.putString("devsecret", pref.getString("devsecret", "37ac079c3a7058543968f8cd3706cbbcb63aa357d006c271ef5255ee12e937"));
        args.putString("pimhost", pref.getString("pimhost", "ripim-int3.ocutag.com"));
        args.putString("pastjob", pref.getString("pastjob", ""));

        //apl token
//        args.putString("token", pref.getString("token", "JPND8NQ7")); //wide image
//        args.putString("host", pref.getString("host", "rirecog-int3.ocutag.com"));
//        args.putString("appid", pref.getString("appid", "bf1c40daeb872c29b829b57d3ef25dd9688fa34d5e13"));
//        args.putString("appsecret", pref.getString("appsecret", "8fbc1cd8791f9f164d0398f13ed4990ac78e107899e1ac1f0da6eb9f753728"));
//        args.putString("devid", pref.getString("devid", "1c2a5edea479388262916bb4d8c995d2aa2bf741e595"));
//        args.putString("devsecret", pref.getString("devsecret", "0f35d2792acab82ff423e76a0e09851e6759d200669d56aaccef3481b9d624"));
//        args.putString("pimhost", pref.getString("pimhost", "ripim-int3.ocutag.com"));
//        args.putString("pastjob", pref.getString("pastjob", ""));

        //apl token
//        args.putString("token", pref.getString("token", "ZCCOBCEE"));
////        args.putString("token", pref.getString("token", "8FMNSEOF")); // empty apl data
//        args.putString("host", pref.getString("host", "rirecog-int3.ocutag.com"));
//        args.putString("appid", pref.getString("appid", "3167b4e07c986298d0119c93744a465d4eb3aab0baef"));
//        args.putString("appsecret", pref.getString("appsecret", "d20f1ba8a6855cc12754c6040ec48bd5e74b07b3a6299d78b7dec0c2121ef2"));
//        args.putString("devid", pref.getString("devid", "b7ef702ba733a62b03ec79627b57cd06fe0950146297"));
//        args.putString("devsecret", pref.getString("devsecret", "c887fb4c5ea5e0df4e734c85dc0f81c4f95421d7e5a9d16ee9a8d4d5e7c0ff"));
//        args.putString("pimhost", pref.getString("pimhost", "ripim-int3.ocutag.com"));
//        args.putString("pastjob", pref.getString("pastjob", ""));



        fragmentManagerActivity.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        findViewById(R.id.frameLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.caldisplay).setVisibility(View.VISIBLE);
        if (!(fragmentManagerActivity.isVisible())) {
            fragmentTransaction.replace(R.id.caldisplay, fragmentManagerActivity);
//            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    private void unloadFragment() {
        findViewById(R.id.button5).setVisibility(View.VISIBLE);
        if (fragmentManagerActivity != null) {
            try {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.remove(fragmentManagerActivity);
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit(); // save the changes
                findViewById(R.id.caldisplay).setVisibility(View.GONE);
                findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createAlertDialog(String title) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Enter Value");
        // Create TextView
        final EditText input = new EditText(this);
        alert.setView(input);

        if (title.equalsIgnoreCase("TOKEN")) {
            alert.setTitle("TOKEN");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //input.setText("");
                    // Do something with value!

                    editor.putString("token", input.getText().toString());
                    editor.commit();
                    token = pref.getString("token", ""); // getting String
                    button6.setText("TOKEN:" + token);

                }
            });
        } else if (title.equalsIgnoreCase("UPC")) {
            alert.setTitle("UPC PREFIX");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!

                    editor.putString("upc_prefix", input.getText().toString().toLowerCase());
                    editor.commit();
                    upc_prefix = pref.getString("upc_prefix", ""); // getting String
                    button7.setText("UPC Prefixes (dash-delimited):" + upc_prefix);

                }
            });
        } else if (title.equalsIgnoreCase("APPID")) {
            alert.setTitle("APPID");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!

                    editor.putString("appid", input.getText().toString());
                    editor.commit();
                    appid = pref.getString("appid", ""); // getting String
                    button9.setText("appId:" + appid);
                }
            });
        } else if (title.equalsIgnoreCase("APPSEC")) {
            alert.setTitle("APPSEC");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!
                    editor.putString("appsecret", input.getText().toString().toLowerCase());
                    editor.commit();
                    appsecret = pref.getString("appsecret", ""); // getting String
                    button8.setText("appSecret:" + appsecret);

                }
            });
        } else if (title.equalsIgnoreCase("DEVID")) {
            alert.setTitle("DEVID");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!
                    editor.putString("devid", input.getText().toString().toLowerCase());
                    editor.commit();
                    devid = pref.getString("devid", ""); // getting String
                    button13.setText("devId:" + devid);

                }
            });
        } else if (title.equalsIgnoreCase("DEVSEC")) {
            alert.setTitle("DEVSEC");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!
                    editor.putString("devsecret", input.getText().toString().toLowerCase());
                    editor.commit();
                    devsecret = pref.getString("devsecret", ""); // getting String
                    button12.setText("devSecret:" + devsecret);
                }
            });
        } else if (title.equalsIgnoreCase("HOST")) {
            alert.setTitle("HOST");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!
                    editor.putString("host", input.getText().toString());
                    editor.commit();
                    host = pref.getString("host", ""); // getting String
                    button11.setText("Host:" + host);

                }
            });
        } else if (title.equalsIgnoreCase("PIMHOST")) {
            alert.setTitle("PIMHOST");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!
                    editor.putString("pimhost", input.getText().toString());
                    editor.commit();
                    pimhost = pref.getString("pimhost", ""); // getting String
                    button10.setText("PIM Host:" + pimhost);

                }
            });
        } else if (title.equalsIgnoreCase("PASTJOB")) {
            alert.setTitle("PASTJOB");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do something with value!
                    editor.putString("pastjob", input.getText().toString());
                    editor.commit();
                    pastjob = pref.getString("pastjob", ""); // getting String
                    button14.setText("Past Job:" + pastjob);

                }
            });
        }

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}