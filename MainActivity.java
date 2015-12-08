    package com.murtic.adis.techyz;

    import android.app.AlertDialog;
    import android.app.SearchManager;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.res.ColorStateList;
    import android.content.res.Configuration;
    import android.graphics.Bitmap;
    import android.graphics.Color;
    import android.net.MailTo;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Handler;
    import android.preference.PreferenceManager;
    import android.provider.Settings;
    import android.support.design.widget.CoordinatorLayout;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.NavigationView;
    import android.support.design.widget.Snackbar;
    import android.support.v4.view.GravityCompat;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v4.widget.SwipeRefreshLayout;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.SearchView;
    import android.support.v7.widget.Toolbar;
    import android.text.Html;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.animation.Animation;
    import android.view.animation.AnimationUtils;
    import android.webkit.DownloadListener;
    import android.webkit.WebChromeClient;
    import android.webkit.WebSettings;
    import android.webkit.WebView;
    import android.webkit.WebViewClient;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.zxing.common.StringUtils;
    import com.parse.ParseException;
    import com.parse.ParseInstallation;
    import com.parse.SaveCallback;
    import com.squareup.picasso.Picasso;
    import org.json.JSONObject;




    public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    // SITE_URL
    private static String PUSH_URL = null;

    // ID trenutno selektovanog itema u Navigation Draweru
    private int navItemID;

    // TAG koji koristimo da sacuvamo naItemID u memoriji
    private static final String NAV_ITEM_ID = "navItemID";

    // Duzina odgode kod zatvaranja Navigation Drawera
    private static final long DRAWER_CLOSE_DELAY_MS = 250;

    // Handler koji upravlja odgodom kod zatvaranja Navigation Drawera
    private final Handler drawerActionHandler = new Handler();

    // Izlazak iz aplikacije sa porukom
    long lastPress;

    // Postavke
    boolean webCache;
    boolean loadImages;


    // postavke coordinator layout-a i floating buttona
    private CoordinatorLayout rootlayout;
    private FloatingActionButton fab;

    // Komponente u interfejsu
    Toolbar toolbar;
    NavigationView main_drawer;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    SwipeRefreshLayout swipeRefreshLayout;
    WebView webView;
    TextView tekst;
    ProgressBar progressBar;
    TextView textView;
    ImageView splashImg;
    Animation rotateout;
    Animation rotatein;
    TextView text22;

    // poruka na klik  koju koristimo na floating action button-u
    public void Poruka (View v) {
        Snackbar.make(rootlayout, "Pogledajte akciju Android televizora.", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).setAction("više ->", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                webView.loadUrl("http://imtec.ba/androidtv/");

            }

        }).show();


    }



    // Pokrenimo glavnu aktivnost
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pokretanje Floating Action Button-a
        rootlayout =(CoordinatorLayout) findViewById(R.id.rootlayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_800)));

        // Pokreni WebView komponentu
        webView = (WebView) findViewById(R.id.weby);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        // Pokreni Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Pokreni Glavni Navigation Drawer
        main_drawer = (NavigationView) findViewById(R.id.main_drawer);

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                } else {
                    e.printStackTrace();

                }
            }
        });

        // Ako u memoriji ne postoji sacuvan navItemID ucitaj prvu stranicu
        // u suprotnom ucitaj navItemID iz memorije i otvori stranicu vezanu za njega
        if (savedInstanceState == null) {
            navItemID = R.id.nav_item_1;
        } else {
            navItemID = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        // Provjeri i ucitaj postavke za aplikaciju
        SharedPreferences postavke = PreferenceManager.getDefaultSharedPreferences(this);
        webCache = postavke.getBoolean("Web_cache", false);
        loadImages = postavke.getBoolean("load_images", true);

        // Postavi title u toolbaru
        toolbar.setTitle("Imtec");

       // Postavi boju slova u Toolbar-u
        toolbar.setTitleTextColor(Color.DKGRAY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Osluskuj ima li kakvih klikova po Navigation Drawer itemima
        main_drawer.setNavigationItemSelectedListener(this);

        // Postavi Drawer Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Oznaci trenutno selektovani item u Navigation Draweru
        main_drawer.getMenu().findItem(navItemID).setChecked(true);

        // Postavi ikonicu u toolbar kojom ukljucujemo i iskljucujemo Navigation Drawer
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();


        // Postavi Drawer Header Image (koristimo Picasso za pametno ucitavanje slika)
        ImageView drawerHeaderImage = (ImageView) findViewById(R.id.imteclogo);
        Picasso.with(this).load(R.drawable.imteclogo).into(drawerHeaderImage);

        //mogućnost skidanja (Downloada) dokumenata
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        // Mogućnost zumiranja dokumenata unutar Webview komponente
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setSupportMultipleWindows(true);

        // WebView Postavke (neke mogu ubrzati ili usporiti ucitavanje stranica)
        // Ukljuci automatsko ucitavanje slika
        webView.getSettings().setLoadsImagesAutomatically(loadImages);


        // Iskljuci geolokaciju
        webView.getSettings().setGeolocationEnabled(false);

        // Iskljuci postavljanje pocetnog fokusa
        webView.getSettings().setNeedInitialFocus(false);

        // Iskljuci sacuvljavanje podataka u web formama
        webView.getSettings().setSaveFormData(false);

        // Dozvoli pristup fajlovima unutar WebView komponente
        webView.getSettings().setAllowFileAccess(true);

        // Ukljuci javascript
        webView.getSettings().setJavaScriptEnabled(true);

        //PDF Viewer
        String pdf = "http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf";
        webView.loadUrl("https://docs.google.com/viewer?url=http://my.domain.com/yourPdfUrlHere.pdf" + pdf);
        // Iskljuci App Cache za brze otvaranje stranica
        webView.getSettings().setAppCacheEnabled(webCache);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeit);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Pokreni Web View Client
        webView.setWebViewClient(new WebViewClient());

        // slika u pozadini loga
        text22 = (TextView)findViewById(R.id.text22);

        splashImg = (ImageView) findViewById(R.id.splashImg);

        // urađena animacija loga na početnoj stranici aplikacije

        rotateout = AnimationUtils.loadAnimation(this,R.anim.rotateout);
        splashImg.setAnimation(rotateout);
        rotatein = AnimationUtils.loadAnimation(this,R.anim.rotatein);
        splashImg.setAnimation(rotatein);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        webView.setWebViewClient(new WebViewClient() {

            // pokazivanje postavki kada aplikacija nema pristup internetu
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == -2) {
                    view.loadData("", "", null);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#388E3C'><b>Imtec Aplikacija</b></font>"));
                builder.setMessage(Html.fromHtml("<font color='#000000'>Nemate internet konekcije. Konektujte se i pokušajte ponovo !</font>"));
                builder.setPositiveButton(Html.fromHtml("<font color='#388E3C'><b>Wifi Postavke</b></font>"), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);

                    }
                });


                builder.setNegativeButton(Html.fromHtml("<font color='#7F02AE'><b>Pokušajte ponovo</b></font>"), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        webView.loadUrl("http://imtec.ba/app/new/index.html");
                    }
                });


                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                text22.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                splashImg.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // Mogucnost poziva unutar Webview-a
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                    // Mogucnost telefonskog poziva unutar Webview-a
                } else if (url.startsWith("mailto:")) {
                    MailTo mailTo = MailTo.parse(url);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo.getTo()});
                    intent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
                    intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
                    intent.putExtra(Intent.EXTRA_CC, mailTo.getCc());
                    intent.setType("message/rfc822");
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                text22.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                splashImg.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);


            }


        });



        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                progressBar.setProgress(newProgress);

            }
        });




        if (getIntent().getExtras() != null) {

            try {

                Bundle b = getIntent().getExtras();

                if(b.containsKey("com.parse.Data")) {

                    JSONObject jsonObject = new JSONObject(b.getString("com.parse.Data"));

                    String data = jsonObject.getString("alert");
                    PUSH_URL = jsonObject.getString("url");

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                    intent.putExtra("SITE_URL", PUSH_URL);
                    startActivity(intent);

                    tekst.setText(data);
                }

                if (b.containsKey("scanned")) {

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                    PUSH_URL = b.getString("scanned");

                    intent.putExtra("SITE_URL", PUSH_URL);
                    startActivity(intent);

                    tekst.setText(PUSH_URL);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            webView.loadUrl(PUSH_URL);

        }

        if (PUSH_URL == null) {

            navigate(navItemID);

        }


    }


    // Nova instanca Navigation Drawera trazi novu konfiguraciju
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        drawerToggle.onConfigurationChanged(newConfig);

    }

    // Sta se dogadja kada kliknemo na item u Navigation Draweru
    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {

        // Osvjezi selektovani item u Navigation Draweru
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }

        navItemID = menuItem.getItemId();

        // Dodaj malo vremena nakon zatvaranja Navigation Drawera da korisnik moze vidjeti sta se dogadja
        drawerLayout.closeDrawer(GravityCompat.START);
        drawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(navItemID);
            }
        }, DRAWER_CLOSE_DELAY_MS);

        return true;


    }

    // Kada korisnik klikne na neki od itema pozivamo funkciju navigate i prosljedjujemo joj itemId
    // zavisno od vrijednosti itemId ova mala pametna funkcija otvara zeljeni link ili aktivnost
    private void navigate(final int itemId) {

        if (itemId == R.id.nav_item_1) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/index.html");

        }

        if (itemId == R.id.nav_item_2) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://shop.imtec.ba/?mobile_theme_ok");

        }



        if (itemId == R.id.nav_item_3) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/akcije.html");

        }

        if (itemId == R.id.nav_item_30) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/video.html");

        }

        if (itemId == R.id.nav_item_4) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/trgovine.html");

        }

        if (itemId == R.id.nav_item_5) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/placanje.html");
        }


        if (itemId == R.id.nav_item_6) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/servis.html");
        }

        if (itemId == R.id.garancija) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/garancija.html");
        }


        if (itemId == R.id.narudzba) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/kupovina.html");
        }


        if (itemId == R.id.nav_item_7) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("http://imtec.ba/app/new/kontakt.html");
        }

        if (itemId == R.id.nav_item_8) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("https://shop.imtec.ba/order");
        }

        if (itemId == R.id.nav_item_9) {

            drawerLayout.closeDrawer(GravityCompat.START);
            webView.loadUrl("https://shop.imtec.ba/authentication");
        }

        if (itemId == R.id.nav_item_10) {

            Intent launchScanner = new Intent(MainActivity.this, ScannerActivity.class);
            startActivity(launchScanner);
        }

       if (itemId == R.id.postavke) {

           Intent launchPrefs = new Intent(MainActivity.this, Preferences.class);
           startActivity(launchPrefs);

       }


    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeColors(Color.DKGRAY);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(false);
                webView.reload();
            }
        }, 1000);
    }


    public class AppConfig {

        public static final String PARSE_APPLICATION_ID = "mFrgSZ6IOUiwKY0IIi9s1YnWNLyZ9gTCjkvcEjMs";
        public static final String PARSE_CLIENT_KEY = "dvqQCBPA8Itq69bqfp2VyW98gQZaJMwwe5mpKY2O";
        public static final int NOTIFICATION_ID = 100;
    }

    // Sta se desava kada pritisnemo BACK dugmic
    // Napravimo ga pametnim :)
    @Override
    public void onBackPressed() {




        // Ako je navigation drawer otvoren zatvori ga
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);

        }

        // Ako je navigation drawer zatvoren i ako u memoriji ima jos jedna stranica idi jednu stranicu nazad
        else if (!drawerLayout.isDrawerOpen(GravityCompat.START) && webView.canGoBack()) {

            webView.goBack();

        } else  // Izlazak iz aplikacije sa porukom
            {
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastPress > 1000){
                Toast.makeText(getBaseContext(), "Pritisni ponovo za izlaz.", Toast.LENGTH_LONG).show();
                lastPress = currentTime;
            }else

            // Ako nista od navedenog nije istina napusti aplikaciju
            super.onBackPressed();


        }


    }



    // Sacuvajmo trenutno otvorenu poziciju u meniju kako bismo u slucaju napustanja
    // pauze ili iznenadnog poziva mogli nastaviti na istom mjestu
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // sacuvajmo navItemID trenutno otvorenog linka u Navigation Draweru
        outState.putInt(NAV_ITEM_ID, navItemID);

    }

    // Pokreni meni u toolbaru
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchbox).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        handleIntent(getIntent());

        return true;
    }

    // Sta se desava kada kliknemo item u toolbar meniju
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.refresh:

               webView.reload();

                return true;

            case R.id.home:
                webView.loadUrl("http://imtec.ba/app/new/index.html");
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        setIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);

            webView.loadUrl("http://shop.imtec.ba/search?search_query=" + query + "&controller=search&orderby=position&orderway=desc");

        }

    }




    }