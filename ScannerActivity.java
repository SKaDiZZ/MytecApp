package com.murtic.adis.techyz;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import com.google.zxing.Result;

import java.net.URI;
import java.net.URISyntaxException;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "Postavite kod u centar kvadrata:";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 15;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.top + PAINT.getTextSize() - 100;
                tradeMarkLeft = framingRect.left + 200;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here

        final AlertDialog.Builder buildAlert = new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Dialog_Alert);
        buildAlert.setTitle("Result");
        buildAlert.setMessage(rawResult.getText() + "\n" + rawResult.getBarcodeFormat().toString());
        buildAlert.setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                mScannerView.startCamera();
            }
        });


        buildAlert.setNeutralButton("Kopiraj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                // Creates a new text clip to put on the clipboard
                ClipData clip = ClipData.newPlainText("scanned_text",rawResult.getText());

                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);

                dialogInterface.cancel();
                mScannerView.startCamera();

            }
        });


        buildAlert.setPositiveButton("Posjeti URL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent goToScanned = new Intent(ScannerActivity.this, MainActivity.class);
                goToScanned.putExtra("scanned", rawResult.getText());
                startActivity(goToScanned);
            }
        });

        final AlertDialog alertDialog = buildAlert.create();
        alertDialog.show();

        // Provjeri da li je skenirani tekst zapravo URL ako jeste uljuci Posjeti URL dugmic
        // Ako nije url iskljucit ce dugmic
        Button posjetiURL = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        try{
            new URI(rawResult.getText());
            posjetiURL.setEnabled(true);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            posjetiURL.setEnabled(false);
        }

    }

}
