package ua.dash;
import android.app.Activity;
import android.content.*;
import android.os.*;
import android.view.*;
import android.webkit.*;

public class MainActivity extends Activity {
  WebView w; BroadcastReceiver r; boolean plugged; int level = 100; boolean have;

  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    w = new WebView(this);
    w.setBackgroundColor(0xff000000);
    setContentView(w);
    WebSettings s = w.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    w.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView v, String u) { push(); }
    });
    w.loadUrl("file:///android_asset/index.html?app=1");
  }

  void push() {
    if (have) w.evaluateJavascript("window.nativeBat&&nativeBat(" + plugged + "," + level + ")", null);
  }

  void hide() {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
      | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
  }

  @Override protected void onResume() {
    super.onResume(); hide();
    r = new BroadcastReceiver() {
      @Override public void onReceive(Context c, Intent i) {
        plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
        int sc = i.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) * 100 / (sc > 0 ? sc : 100);
        have = true; push();
      }
    };
    registerReceiver(r, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
  }

  @Override protected void onPause() { super.onPause(); if (r != null) unregisterReceiver(r); r = null; }
  @Override public void onWindowFocusChanged(boolean f) { super.onWindowFocusChanged(f); if (f) hide(); }
  @Override public void onBackPressed() { w.evaluateJavascript("var s=document.getElementById('set');s.className=''", null); }
}
