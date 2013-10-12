package com.kukung.ac;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Mp3ListAdapter extends BaseAdapter{
	Context context;
	int layout;
	List<Mp3Item> mp3ItemList;
	
	public Mp3ListAdapter(Context context, int layout, List<Mp3Item> mp3ItemList) {
		this.context = context;
		this.layout = layout;
		if (mp3ItemList == null) {
			this.mp3ItemList = new ArrayList<Mp3Item>();
		} else {
			this.mp3ItemList = mp3ItemList;
		}
	}
	
	@Override
	public int getCount() {
		return mp3ItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mp3ItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(layout, parent, false);
		}
		
		Mp3Item mp3Item = (Mp3Item)getItem(position);
		TextView tocTitle = (TextView)convertView.findViewById(R.id.mp3ItemTitle);
		tocTitle.setText(mp3Item.getTitle());
		
		return convertView;
	}

	public List<Mp3Item> getMp3ItemList() {
		return mp3ItemList;
	}

	public void setMp3ItemList(List<Mp3Item> mp3ItemList) {
		this.mp3ItemList = mp3ItemList;
	}

}
