package com.growatt.chargingpile.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutActivity extends BaseActivity {

	@BindView(R.id.relative_agreement)
	RelativeLayout rlAgreement;

	@BindView(R.id.headerView)
	LinearLayout headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		ButterKnife.bind(this);
		initHeaderView();
	}

	private void initHeaderView() {
		setHeaderTitle(headerView, getString(R.string.m53关于), R.color.title_1, true);
		setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@OnClick(R.id.relative_agreement)
	public void onClickListener(View view){
		switch (view.getId()){
			case R.id.relative_agreement:
				startActivity(new Intent(this,AgreementActivity.class));
				break;
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
