package com.example.yeomanda.ListView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yeomanda.R;

import java.util.ArrayList;

public class ChatMessageAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ChatMessageItem> listViewItemList = new ArrayList<>();

    // ListViewAdapter의 생성자
    public ChatMessageAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chatting_message_list_item, parent, false);
        }

        LinearLayout linearLayout=convertView.findViewById(R.id.chatMessageLayout);
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView nameTextView = convertView.findViewById(R.id.userName);
        TextView contentTextView = convertView.findViewById(R.id.chatmessage);
        TextView timeTextView = convertView.findViewById(R.id.msgTime);
        // LoginResponseDataDto Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ChatMessageItem listViewItem = listViewItemList.get(position);

        if (listViewItem.getIsMyChat()){
            linearLayout.setGravity(Gravity.RIGHT);
            nameTextView.setText("");
        }else{
            linearLayout.setGravity(Gravity.LEFT);
            nameTextView.setText(listViewItem.getUserName());
        }
        // 아이템 내 각 위젯에 데이터 반영
        contentTextView.setText(listViewItem.getMessage());
        timeTextView.setText(listViewItem.getMsgTime());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String name, String content,String time,Boolean isMyChat) {
        ChatMessageItem item = new ChatMessageItem();
        item.setUserName(name);
        item.setMessage(content);
        item.setMsgTime(time);
        item.setIsMyChat(isMyChat);
        /*
        item.setUserName(chatMessageItem.getUserName());
        item.setMessage(chatMessageItem.getMessage());
        item.setMsgTime(chatMessageItem.getMsgTime());

 */
        listViewItemList.add(item);
    }


}
