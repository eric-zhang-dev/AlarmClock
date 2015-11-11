package com.baby.sp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baby.sp.R;

public class SelectBellActivity extends Activity implements OnClickListener{

	private Button bt_bell;
	private Button bt_music;
	private ListView lv_bell_list;
	
	private Context context;
	
	private List<Map<String, Object>> bells;
	private List<Map<String, Object>> musics;
	
	private MusicAdapter bellAdapter;
	private MusicAdapter musicAdapter;
	
	private Map<String, Boolean> RBStates;
	
	private MediaPlayer player;
	
	private String name;
	private String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//设置无标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bell_list);
		init();
		initList();
		bellAdapter = new MusicAdapter(bells);
		musicAdapter = new MusicAdapter(musics);
		bt_bell.performClick();
	}

	private void initList() {
		// 系统铃声查询
		Cursor bellcCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
					null, null, null, null);
		if(bellcCursor != null){
			while(bellcCursor.moveToNext()){
				Map<String, Object> map = new HashMap<String, Object>();
				//名字
				String bellName = bellcCursor.getString(
						bellcCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
				//路径
				String bellPath = bellcCursor.getString(
						bellcCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
				map.put("name", bellName);
				map.put("path", bellPath);
				bells.add(map);
			}
			bellcCursor.close();
		}
		//用户铃声查询
		Cursor musiccCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, null);
		if(musiccCursor != null){
			while(musiccCursor.moveToNext()){
				Map<String, Object> map = new HashMap<String, Object>();
				//名字
				String bellName = musiccCursor.getString(
						musiccCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
				//路径
				String bellPath = musiccCursor.getString(
						musiccCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
				map.put("name", bellName);
				map.put("path", bellPath);
				musics.add(map);
			}
			musiccCursor.close();
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		context = this;
		path = getIntent().getStringExtra("bellPath");
		name = getIntent().getStringExtra("bellName");
		bt_bell = (Button) findViewById(R.id.bt_bell);
		bt_music = (Button) findViewById(R.id.bt_music);
		lv_bell_list = (ListView) findViewById(R.id.lv_bell_list);
		lv_bell_list.setDivider(new ColorDrawable(Color.WHITE));  
		lv_bell_list.setDividerHeight(1); 
		RBStates = new HashMap<String, Boolean>();
		bells = new ArrayList<Map<String,Object>>();
		musics = new ArrayList<Map<String,Object>>();
		bt_bell.setOnClickListener(this);
		bt_music.setOnClickListener(this);
		((TextView)findViewById(R.id.tv_cancel)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_ok)).setOnClickListener(this);
		
	}

	//播放选择的铃声
	private void playMusic(String path){
		resetPlayer();
		player = MediaPlayer.create(context, Uri.parse(path));
		player.setLooping(false);
		player.start();
	}
	
	
	private void resetPlayer(){
		if(player != null){
			player.stop();
			player.release();
			player = null;
		}
		
	}
	
	//自定义适配器
	class MusicAdapter extends BaseAdapter{

		private List<Map<String, Object>> lists;
		
		MusicAdapter(List<Map<String, Object>> lists){
			this.lists = lists;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(lists != null && lists.size() > 0 ){
				return lists.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			Holder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.bell_item, null);
				holder = new Holder();
				convertView.setTag(holder);
				holder.tv_music = (TextView) convertView.findViewById(R.id.tv_music);
				holder.rb_check = (RadioButton) convertView.findViewById(R.id.rb_check);
				holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
			}else{
				holder = (Holder) convertView.getTag();
			}
			
			holder.tv_music.setText(lists.get(position).get("name").toString());
			holder.ll_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for (String key : RBStates.keySet()) {	
						RBStates.put(key, false);
					}
					RBStates.put(String.valueOf(position), true);
					notifyDataSetChanged();
					playMusic(lists.get(position).get("path").toString());
					name = lists.get(position).get("name").toString();
					path = lists.get(position).get("path").toString();
				}
			});
			
			boolean res = false;
			if(RBStates.get(String.valueOf(position)) == null || RBStates.get(String.valueOf(position)) == false){
				res = false;
			}else{
				res = true;
			}

			holder.rb_check.setChecked(res);			
			return convertView;
		}
		
		class Holder {
			LinearLayout ll_item;
			TextView tv_music;
			RadioButton rb_check;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_cancel:
			finish();
			break;
		case R.id.tv_ok:
			Intent intent = new Intent();
			intent.putExtra("name", name);
			intent.putExtra("path", path);
			setResult(100,intent);
			finish();
			break;
		case R.id.bt_bell:
			resetPlayer();
			bt_bell.setBackgroundResource(R.drawable.button_clicked_back);
			bt_music.setBackgroundResource(R.drawable.button_back);
			if(bellAdapter != null && RBStates != null){
				RBStates.clear();
				bellAdapter.notifyDataSetChanged();
				lv_bell_list.setAdapter(bellAdapter);
			}
			break;
		case R.id.bt_music:
			resetPlayer();
			bt_bell.setBackgroundResource(R.drawable.button_back);
			bt_music.setBackgroundResource(R.drawable.button_clicked_back);
			if(bellAdapter != null && RBStates != null){
				RBStates.clear();
				musicAdapter.notifyDataSetChanged();
				lv_bell_list.setAdapter(musicAdapter);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		resetPlayer();
	}
	
}
