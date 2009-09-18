package com.roiding.rterm;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.view.KeyEvent;

import android.os.ResultReceiver;
import android.view.inputmethod.InputMethodManager;

import com.roiding.rterm.bean.Host;
import com.roiding.rterm.util.Constants;
import com.roiding.rterm.util.TerminalManager;

public class TerminalActivity extends Activity {

	private static final String TAG = "TermAct";
	protected static final int DIALOG_INPUT_HELP = 0;
	private ViewFlipper vflipper;
	private ScrollView scroll;
	private static long currentViewId = -1;

	private RefreshHandler mHandler;

	private InputMethodManager mInputMethodManager;
	private IMEResultReceiver mInputResultReceiver;

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			close((Exception) msg.obj);
		}

		public void dispatch(Exception ex) {
			this.removeMessages(0);
			Message.obtain(this, -1, ex).sendToTarget();
		}
	};

	class IMEResultReceiver extends ResultReceiver {
		public IMEResultReceiver() {
			super(null);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			Log.d("IMEResultReceiver", "onReceiveResult");	
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (!pref.getBoolean(Constants.SETTINGS_SHOW_STATUSBAR, false))
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_terminal);
		mHandler = new RefreshHandler();

		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mInputResultReceiver = new IMEResultReceiver(); 

	}

	@Override
	public void onStart() {
		super.onStart();

		Host host = (Host) getIntent().getExtras().getSerializable("host");

		currentViewId = host.getId();

		scroll = (ScrollView) findViewById(R.id.terminal_scroll);
		vflipper = (ViewFlipper) findViewById(R.id.terminal_flipper);
		final GestureDetector gestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {

						TerminalView view = TerminalManager.getInstance()
								.getView(currentViewId);

						float scrollX = (vflipper.getScrollX() + distanceX);

						Log.d("dd", "onScroll:" + scrollX);

						scrollX = Math.max(scrollX, 0);
						scrollX = Math.min(scrollX, view.SCREEN_WIDTH
								- TerminalView.SCREEN_WIDTH_DEFAULT);

						vflipper.scrollTo((int) scrollX, 0);
						return false;
					}

				});

		vflipper.setScrollBarStyle(ViewFlipper.SCROLLBARS_INSIDE_INSET);
		vflipper.setLongClickable(true);
		vflipper.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}

		});

		TerminalView view = TerminalManager.getInstance()
				.getView(currentViewId);

		if (view == null) {
			view = new TerminalView(this, null, this);
			view.terminalActivity = this;
			view.startConnection(host);
			TerminalManager.getInstance().putView(view);
			checkService();
		}

		view.terminalActivity = this;

		showView(currentViewId);
	}

	private int scrollY;

	public void scroll(int x, int y) {
		if (scrollY != (int) y
				&& (y >= TerminalView.SCREEN_HEIGHT_DEFAULT || y <= scroll
						.getScrollY())) {
			scroll.smoothScrollTo(x, y);
			scrollY = (int) y;
		}
	}

	public void refreshView() {
		showView(currentViewId);
	}

	public void showView(long id) {
		TerminalView view = TerminalManager.getInstance().getView(id);
		if (view != null) {
			view.terminalActivity = this;

			vflipper.removeAllViews();
			vflipper.addView(view, view.SCREEN_WIDTH, view.SCREEN_HEIGHT);

			currentViewId = id;

			vflipper.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.slide_in_left));
			vflipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.slide_out_right));

			vflipper.showNext();

			view.requestFocus();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

//		MenuItem cninput = menu.add(R.string.terminal_disconnect);
//		cninput.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			public boolean onMenuItemClick(MenuItem item) {
//				AlertDialog.Builder alert = new AlertDialog.Builder(
//						TerminalActivity.this);
//
//				alert.setTitle("Title");
//
//				// Set an EditText view to get user input
//				final EditText input = new EditText(TerminalActivity.this);
//				alert.setView(input);
//
//				alert.setPositiveButton("Ok",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								String value = input.getText().toString();
//
//								TerminalView view = TerminalManager
//										.getInstance().getView(currentViewId);
//
//								if (view != null) {
//									try {
//										view.connection.send(value
//												.getBytes(view.host
//														.getEncoding()));
//
//									} catch (Exception e) {
//									}
//
//								}
//
//							}
//						});
//
//				alert.setNegativeButton("Cancel",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								// Canceled.
//							}
//						});
//
//				alert.show();
//				return true;
//			}
//		});

		MenuItem disconnect = menu.add(R.string.terminal_disconnect).setIcon(
				R.drawable.offline);
		disconnect.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				close(null);
				return true;
			}
		});

                final TerminalView current_view = TerminalManager.getInstance().getView(currentViewId);

                if (current_view != null) {


  		MenuItem ime = menu.add("Edit").setIcon(R.drawable.showime);
		ime.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				mInputMethodManager.showSoftInput(current_view, InputMethodManager.SHOW_FORCED, mInputResultReceiver);
				return true;
			}
		});

		}

		TerminalView[] views = TerminalManager.getInstance().getViews();
		for (final TerminalView view : views) {
			MenuItem item = menu.add(view.host.getName()).setIcon(
					R.drawable.online);
			if (views.length > 1)
				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						showView(view.host.getId());
						return true;
					}
				});
		}

		return true;
	}

	public void disconnect(Exception e) {
		mHandler.dispatch(e);
	}

	public void close(Exception e) {

		TerminalView currentView = TerminalManager.getInstance().getView(
				currentViewId);
		try {
			currentView.connection.disconnect();
		} catch (Exception _e) {
		}

		if (e != null) {
			String msg = e.getLocalizedMessage();

			Host currentHost = currentView.host;

			if (UnknownHostException.class.isInstance(e)) {
				msg = String
						.format(getText(R.string.terminal_error_unknownhost)
								.toString(), currentHost.getName());
			} else if (ConnectException.class.isInstance(e)) {
				msg = String.format(getText(R.string.terminal_error_connect)
						.toString(), currentHost.getName());
			}

			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}

		TerminalManager.getInstance().removeView(currentViewId);
		checkService();
		currentViewId = -1;
		finish();
	}

	private void checkService() {
		if (TerminalManager.getInstance().getViews().length == 0) {
			Log.i(TAG, "stopService...");
			stopService(new Intent(this, TerminalService.class));
		} else {
			Log.i(TAG, "startService...");
			startService(new Intent(this, TerminalService.class));
		}
	}

	@Override
	public void  onWindowFocusChanged  (boolean hasFocus) {
		Log.d("onWindowFocusChanged", "onWindowFocusChanged" + hasFocus);
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume () {
		Log.d("onResume", "onResume");
		super.onResume ();
	}
/*
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {

		Log.d("onKeyDown", "onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("onKeyDown", "BACK button");
			return false;
		}
		return false;
	}
*/
	@Override
	public void onStop() {
		super.onStop();
		vflipper.removeAllViews();
		if (currentViewId == -1) {
			Toast.makeText(this, R.string.terminal_connectclose,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.terminal_connectsave,
					Toast.LENGTH_SHORT).show();
		}
	}
}
