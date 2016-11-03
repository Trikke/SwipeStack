/*
 * Copyright (C) 2016 Frederik Schweiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.trikke.swipestacksample;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import be.trikke.swipestack.SwipeStack;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener, View.OnClickListener {

	private Button mButtonLeft, mButtonRight;
	private FloatingActionButton mFab;

	private ArrayList<String> mData;
	private SwipeStack mSwipeStack;
	private SwipeStackAdapter mAdapter;

	private int fabCardNr = 0;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
		mButtonLeft = (Button) findViewById(R.id.buttonSwipeLeft);
		mButtonRight = (Button) findViewById(R.id.buttonSwipeRight);
		mFab = (FloatingActionButton) findViewById(R.id.fabAdd);

		mButtonLeft.setOnClickListener(this);
		mButtonRight.setOnClickListener(this);
		mFab.setOnClickListener(this);

		mData = new ArrayList<>();
		mAdapter = new SwipeStackAdapter(mData);
		mSwipeStack.setAdapter(mAdapter);
		mSwipeStack.setListener(this);
		mSwipeStack.setSwipeProgressListener(new SwipeStack.SwipeProgressListener() {
			@Override public void onSwipeStart(int position) {
				Log.d("p", "started on " + position);
			}

			@Override public void onSwipeProgress(int position, float progress) {
			}

			@Override public void onSwipeEnd(int position) {
				Log.d("p", "ended on " + position);
			}
		});

		fillWithTestData();
	}

	private void fillWithTestData() {
		for (int x = 0; x < 50; x++) {
			mData.add(getString(R.string.dummy_text) + " " + (x + 1));
		}
	}

	@Override public void onClick(View v) {
		if (v.equals(mButtonLeft)) {
			mSwipeStack.swipeTopViewToLeft();
		} else if (v.equals(mButtonRight)) {
			mSwipeStack.swipeTopViewToRight();
		} else if (v.equals(mFab)) {
			fabCardNr++;
			mData.add(getString(R.string.dummy_fab) + " " + fabCardNr);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menuReset:
				mSwipeStack.resetStack();
				Snackbar.make(mFab, R.string.stack_reset, Snackbar.LENGTH_SHORT).show();
				return true;
			case R.id.menuGitHub:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/trikke/SwipeStack"));
				startActivity(browserIntent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showSwipeDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Make this swipe?");

		// set dialog message
		alertDialogBuilder.setMessage("Click yes to swipe! No to dismiss swipe")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mSwipeStack.continueOnSwipe();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mSwipeStack.resetSwipe();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override public boolean onViewSwipedToRight(int position) {
		String swipedElement = mAdapter.getItem(position);
		Log.w("s", position + " > " + getString(R.string.view_swiped_right, swipedElement));

		showSwipeDialog();
		return true;
	}

	@Override public void onViewTapped(int currentPosition) {
		Log.w("tap", "tapped");
	}

	@Override public boolean onViewSwipedToLeft(int position) {
		String swipedElement = mAdapter.getItem(position);
		Log.w("s", position + " > " + getString(R.string.view_swiped_left, swipedElement));
		//showSwipeDialog();
		Log.w("p", "-> " + mSwipeStack.getCurrentPosition() + " m " + mAdapter.getCount());
		return false;
	}

	@Override public void onStackEmpty() {
		Toast.makeText(this, R.string.stack_empty, Toast.LENGTH_SHORT).show();
	}

	public class SwipeStackAdapter extends BaseAdapter {

		private List<String> mData;

		public SwipeStackAdapter(List<String> data) {
			this.mData = data;
		}

		@Override public int getCount() {
			return mData.size();
		}

		@Override public String getItem(int position) {
			return mData.get(position);
		}

		@Override public long getItemId(int position) {
			return position;
		}

		@Override public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.card, parent, false);
			}

			TextView textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
			textViewCard.setText(mData.get(position));

			return convertView;
		}
	}
}
