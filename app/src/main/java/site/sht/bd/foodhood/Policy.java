package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.PolicyType;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

public class Policy extends AppCompatActivity {

    WebView webView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        final int policyType = getIntent().getIntExtra("policy_type", PolicyType.TERMS_OF_USE);

        webView = findViewById(R.id.webView);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        //webView.loadUrl("https://www.google.com");

        FirebaseFirestore.getInstance().collection("public")
                .document("policy").get()
                .addOnSuccessListener(Policy.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String termsOfUseLink = documentSnapshot.getString("t");
                            Log.i("tos", termsOfUseLink);
                            String privacyPolicyLink = documentSnapshot.getString("p");
                            Log.i("privacy_policy", privacyPolicyLink);
                            if(policyType==PolicyType.TERMS_OF_USE){
                                webView.loadUrl(termsOfUseLink);
                            }else{
                                webView.loadUrl(privacyPolicyLink);
                            }
                            webView.setWebViewClient(new MyWebViewClient(termsOfUseLink, privacyPolicyLink));
                        }
                    }
                })
                .addOnFailureListener(Policy.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error", e.getMessage());
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient{

        String termsOfUseLink;
        String privacyPolicyLink;

        MyWebViewClient(String termsOfUseLink, String privacyPolicyLink){
            super();
            this.termsOfUseLink = termsOfUseLink;
            this.privacyPolicyLink = privacyPolicyLink;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.equals(termsOfUseLink) || url.equals(privacyPolicyLink)){
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @TargetApi(21)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            Log.i("url", url);
            if(url.equals(termsOfUseLink) || url.equals(privacyPolicyLink)){
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}
