package com.example.yeomanda.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yeomanda.R;

import java.util.ArrayList;

public class TeamInfoListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<TeamInfoListViewItem> listViewItemList = new ArrayList<TeamInfoListViewItem>() ;

    // ListViewAdapter의 생성자
    public TeamInfoListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView imageView =  convertView.findViewById(R.id.favoriteTeamPersonImage) ;
        TextView nameTextView =  convertView.findViewById(R.id.favoriteTeamPersonName) ;
        TextView sexTextView = convertView.findViewById(R.id.favoriteTeamPersonSex);
        TextView birthTextView =  convertView.findViewById(R.id.favoriteTeamPersonBirth) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        TeamInfoListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context)
                .load(listViewItem.getFavoriteTeamPersonImage())
                .into(imageView);
        nameTextView.setText(listViewItem.getFavoriteTeamPersonName());
        sexTextView.setText(listViewItem.getFavoriteTeamPersonSex());
        birthTextView.setText(listViewItem.getFavoriteTeamPersonBirth());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String image, String name, String sex,String birth) {
        TeamInfoListViewItem item = new TeamInfoListViewItem();

        item.setFavoriteTeamPersonImage(image);
        item.setFavoriteTeamPersonName(name);
        item.setFavoriteTeamPersonSex(sex);
        item.setFavoriteTeamPersonBirth(birth);
        listViewItemList.add(item);
    }

}
