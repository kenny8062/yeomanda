package com.example.yeomanda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yeomanda.Retrofit.ResponseDto.ChatRoomResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ProfileResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.WithoutDataResponseDto;
import com.example.yeomanda.Retrofit.RequestDto.LocationDto;
import com.example.yeomanda.Retrofit.ResponseDto.LocationResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;
import com.example.yeomanda.Retrofit.RequestDto.TeamInfoDto;
import com.example.yeomanda.chatPkg.ChatActivity;
import com.example.yeomanda.chatPkg.ChatListActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener{
    private GoogleMap mMap;
    private Context context=this;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    ListView listView;
    ArrayAdapter<String> adapter;

    Location mCurrentLocatiion;
    LatLng currentPosition;

    RetrofitClient retrofitClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private LocationResponseDto locationResponseDto=null;
    Button createBoardBtn;
    double lat,lon;
    int teamNumCount;
    String locationArr[];
    String myToken, myEmail;
    Boolean hasPlanned;
    View boardDialogView,profileDialogView;
    ArrayList<String> items=new ArrayList<>();
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    TextView customTravelDate,favoriteTeam,profileRetouch,cancelTravel,chatRoom;
    ArrayList<ArrayList<TeamInfoDto>> sameLocationTeams=new ArrayList<ArrayList<TeamInfoDto>>();

    ImageView menuBtn;

    private DrawerLayout drawerLayout;
    private View drawerView;



    ImageView personSubImage1, personMainImage, personSubImage2,nextTeamBtn, backTeamBtn, chatBtn, favoriteBtn;
    TextView personEmail,personSex,personName,personBirth,teamName;
    ProfileResponseDto profileResponseDto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();
        myToken =intent.getStringExtra("token");
        myEmail=intent.getStringExtra("email");
        hasPlanned=intent.getBooleanExtra("hasPlanned",false);

        init();
    }

    public void init(){
        retrofitClient = new RetrofitClient();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mLayout = findViewById(R.id.layout_main);


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createBoardBtn=findViewById(R.id.createBoardBtn);
        menuBtn=findViewById(R.id.menuIcon);
        favoriteTeam=findViewById(R.id.favoriteTeam);
        profileRetouch=findViewById(R.id.profileRetouch);
        cancelTravel = findViewById(R.id.cancelTravel);
        chatRoom = findViewById(R.id.chatRoom);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        createBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(),CreateBoard.class);
                intent.putExtra("lat",location.getLatitude());
                intent.putExtra("lon",location.getLongitude());
                startActivity(intent);

            }

        });
        favoriteTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent favoriteIntent=new Intent(getApplicationContext(),MyFavoriteList.class);
                favoriteIntent.putExtra("token", myToken);
                startActivity(favoriteIntent);
            }
        });
        cancelTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("게시판 등록을 취소하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        retrofitClient=new RetrofitClient();
                        WithoutDataResponseDto withoutDataResponseDto =retrofitClient.deleteBoard(myToken);
                        while(withoutDataResponseDto ==null){
                            Log.d("error", " withoutDataResponseDto is null");
                        }
                        if(withoutDataResponseDto.getSuccess()) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                        Toast.makeText(getApplicationContext(), withoutDataResponseDto.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        profileRetouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MyProfile.class);
                intent.putExtra("token",myToken);
                intent.putExtra("email",myEmail);
                startActivity(intent);
            }
        });
        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent=new Intent(getApplicationContext(), ChatListActivity.class);
                chatIntent.putExtra("token", myToken);
                chatIntent.putExtra("myEmail",myEmail);
                startActivity(chatIntent);
            }
        });
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                if(locationResponseDto==null) {
                    currentPosition=new LatLng(location.getLatitude(),location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 15);
                    mMap.moveCamera(cameraUpdate);

                    //근처 TeamInfo 가져오기
                    LocationDto locationDto = new LocationDto();
                    locationDto.setLatitude(Double.toString(location.getLatitude()));
                    locationDto.setLongitude(Double.toString(location.getLongitude()));
                    locationResponseDto = retrofitClient.sendLocation(locationDto);
                    while(locationResponseDto == null) {
                        Log.d("error", "locationResponse is null");
                    }
                    ArrayList<TeamInfoDto> teamArr = new ArrayList<>();
                    boolean isOverlap = false;
                    if (locationResponseDto.getData().size() != 0){
                        for(int i=0;i<locationResponseDto.getData().size();i++) {
                            for (int j = 0; j < sameLocationTeams.size(); j++) {
                                if (locationResponseDto.getData().get(i).getLocationGps().equals(sameLocationTeams.get(j).get(0).getLocationGps())) {
                                    sameLocationTeams.get(j).add(locationResponseDto.getData().get(i));
                                    isOverlap=true;
                                    break;
                                }
                            }
                            if(isOverlap) {
                                isOverlap=false;
                            }else{
                                teamArr.add(locationResponseDto.getData().get(i));
                                sameLocationTeams.add(teamArr);
                                teamArr=new ArrayList<>();
                            }
                        }

                        for(int i=0;i<sameLocationTeams.size();i++) {
                            locationArr=sameLocationTeams.get(i).get(0).getLocationGps().split(",");
                            lat=Double.parseDouble(locationArr[0]);
                            lon=Double.parseDouble(locationArr[1]);
                            LatLng teamsGPS = new LatLng(lat, lon);

                            mMap.addMarker(new MarkerOptions()
                                    .position(teamsGPS)
                                    .title(locationResponseDto.getData().get(i).getTeamNo().toString()))
                                    .setTag(sameLocationTeams.get(i));
                        }

                    }
                }

                mCurrentLocatiion = location;
            }


        }

    };


    //현재 위치 업데이트 메소드
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }



    //현재위치를 지오코더를 이용하여 GPS -> 주소로 변환
    public String getCurrentAddress(LatLng latlng) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    //위치가 안잡힐경우 디폴트 위치값 설정
    public void setDefaultLocation() {

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //마커 클릭시 이벤트(게시판 AlertDialog로 띄워주기)
    @Override
    public boolean onMarkerClick(Marker marker) {
        ArrayList<TeamInfoDto> teamInfoDto= (ArrayList<TeamInfoDto>) marker.getTag();

        items=new ArrayList<>();
        boardDialogView = getLayoutInflater().inflate(R.layout.custom_show_travelers, null);

        profileDialogView = getLayoutInflater().inflate(R.layout.activity_person_info,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(boardDialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        backTeamBtn=alertDialog.findViewById(R.id.backTeamBtn);
        nextTeamBtn=alertDialog.findViewById(R.id.nextTeamBtn);
        favoriteBtn=alertDialog.findViewById(R.id.favoriteBtn);
        chatBtn=alertDialog.findViewById(R.id.chatBtn);
        customTravelDate=alertDialog.findViewById(R.id.customTravelDate);
        teamName=alertDialog.findViewById(R.id.customTeamName);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofitClient=new RetrofitClient();
                WithoutDataResponseDto withoutDataResponseDto =retrofitClient.postFavoriteTeam(myToken,teamInfoDto.get(teamNumCount).getTeamNo());
                while(withoutDataResponseDto ==null){
                    Log.d("error", " withoutDataResponseDto is null");
                }
                Toast.makeText(getApplicationContext(), withoutDataResponseDto.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient retrofitClient = new RetrofitClient();
                ChatRoomResponseDto chatRoomResponseDto = retrofitClient.inToChatRoom(myToken, teamInfoDto.get(teamNumCount).getTeamNo().toString());
                while (chatRoomResponseDto == null) {
                    Log.e("error", "chatRoomResponseDto is null");
                }
                if (chatRoomResponseDto.getSuccess()) {

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("roomId", chatRoomResponseDto.getData().getRoomId());
                    intent.putExtra("token", myToken);
                    intent.putExtra("myEmail", myEmail);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"게시판을 등록해주세요",Toast.LENGTH_LONG).show();
                }
            }
        });
        listView=alertDialog.findViewById(R.id.personList);

        //사람 프로필 보기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),items.get(position),Toast.LENGTH_SHORT).show();

                profileDialogView = getLayoutInflater().inflate(R.layout.activity_person_info,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(profileDialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                personSubImage1 =alertDialog.findViewById(R.id.personSubImage1);
                personMainImage =alertDialog.findViewById(R.id.personMainImage);
                personSubImage2 =alertDialog.findViewById(R.id.personsubImage2);
                personEmail=alertDialog.findViewById(R.id.personEmail);
                personSex=alertDialog.findViewById(R.id.personSex);
                personName=alertDialog.findViewById(R.id.personName);
                personBirth=alertDialog.findViewById(R.id.personBirth);
                retrofitClient=new RetrofitClient();
                profileResponseDto=retrofitClient.showProfile(myToken,teamInfoDto.get(teamNumCount).getEmail().get(position));
                while(profileResponseDto==null){
                    System.out.println("ProfileResponseDto is null");
                }
                personEmail.setText(profileResponseDto.getData().getEmail());
                personSex.setText(profileResponseDto.getData().getSex());
                personBirth.setText(profileResponseDto.getData().getBirth());
                personName.setText(profileResponseDto.getData().getName());

                Glide.with(context)
                        .load(profileResponseDto.getData().getFiles().get(0))
                        .into(personMainImage);


                Glide.with(context)
                        .load(profileResponseDto.getData().getFiles().get(1))
                        .into(personSubImage1);

                Glide.with(context)
                        .load(profileResponseDto.getData().getFiles().get(2))
                        .into(personSubImage2);

                //이미지 크게 보기1
                personSubImage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SelectImageActivity.class);
                        intent.putExtra("uri",profileResponseDto.getData().getFiles().get(1));
                        startActivity(intent);

                    }
                });
                //이미지 크게 보기2
                personMainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SelectImageActivity.class);
                        intent.putExtra("uri",profileResponseDto.getData().getFiles().get(0));
                        startActivity(intent);

                    }
                });
                //이미지 크게 보기3
                personSubImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SelectImageActivity.class);
                        intent.putExtra("uri",profileResponseDto.getData().getFiles().get(2));
                        startActivity(intent);

                    }
                });
            }
        });

        teamNumCount =0;
        //같은 위치에 한 팀만 있을 경우
        if(teamInfoDto.size()==1){
            items.addAll(teamInfoDto.get(teamNumCount).getNameList());
            customTravelDate.setText(teamInfoDto.get(teamNumCount).getTravelDate());
            teamName.setText(teamInfoDto.get(teamNumCount).getTeamName());
            adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_listview_item,items);
            listView.setAdapter(adapter);
        }
        //같은 위치에 2개 이상의 팀이 있을 경우
        else{
            nextTeamBtn.setVisibility(View.VISIBLE);
            items.addAll(teamInfoDto.get(teamNumCount).getNameList());
            customTravelDate.setText(teamInfoDto.get(teamNumCount).getTravelDate());
            teamName.setText(teamInfoDto.get(teamNumCount).getTeamName());
            adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_listview_item,items);
            listView.setAdapter(adapter);
            nextTeamBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teamNumCount++;
                    backTeamBtn.setVisibility(View.VISIBLE);
                    items.clear();
                    items.addAll(teamInfoDto.get(teamNumCount).getNameList());
                    customTravelDate.setText(teamInfoDto.get(teamNumCount).getTravelDate());
                    teamName.setText(teamInfoDto.get(teamNumCount).getTeamName());
                    adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_listview_item,items);
                    listView.setAdapter(adapter);
                    //마지막 팀(다음버튼 없애기)
                    if(teamInfoDto.size()== teamNumCount +1) {
                        nextTeamBtn.setVisibility(View.INVISIBLE);
                    }
                }

            });

            backTeamBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teamNumCount--;
                    nextTeamBtn.setVisibility(View.VISIBLE);
                    items.clear();
                    //첫번째 팀
                    items.addAll(teamInfoDto.get(teamNumCount).getNameList());
                    customTravelDate.setText(teamInfoDto.get(teamNumCount).getTravelDate());
                    teamName.setText(teamInfoDto.get(teamNumCount).getTeamName());
                    adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_listview_item,items);
                    listView.setAdapter(adapter);
                    if (teamNumCount==0)
                        backTeamBtn.setVisibility(View.INVISIBLE);
                }
            });
        }


        return false;
    }



    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        locationResponseDto=null;
        sameLocationTeams=new ArrayList<>();
        init();
        Log.d(TAG, "onRestart()");
    }


}