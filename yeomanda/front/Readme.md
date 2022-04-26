# 개발중 오류 및 메모

Retrofit2 사용중 오류

@POST("markup/userDetail")
Call<ProfileResponseDto> showProfile(
        @Body String email
); 으로 보낼 경우에는 서버로 리퀘스트가 가지를 않는다.

하지만 String email 객체 하나만 가지고 있는 EmailDto 클래스를 만들어서

@POST("markup/userDetail")
Call<ProfileResponseDto> showProfile(
        @Body EmailDto email
);

로 보낼 경우에는 리퀘스트가 잘 간다.

SocketIO 오류
처음 소켓연결이 안되어서 구글링을 하여
버전 문제라는 것을 알았다. 그래서 1.0.0 version -> 2.0.0 version으로 바꿔서 해결을 했다.

UI 부분
listview item height은 minheght 이용
listview item간의 margin은 dividerHeight이용
listview scroll기능을 추가하려면 xml에서 android:isScrollContainer="true" 사용

custom image는 한개의 이미지 파일로 스케일링으로 적용할 경우 cpu를 많이 먹게됨 그러므로 스케일링이 필요 없는게 좋음
보통 안드로이드 스튜디오는 mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi 사이즈인 5개의 이미지 파일이 있는게 좋음
안드로이드 앱 번들이라는 패키징 방식이 생겨서 앱을 다운받으면 5개의 사이즈의 파일이 아닌 폰에 맞는 사이즈의 파일 하나만 다운이 받아지므로 용량 걱정은 필요 없어짐

제플린을 이용하면 자동으로 5개의 사이즈로 저장이 가능
적용법 : 5개 사이즈의 이미지를 저장하여 weight,height을 wrap_content로 적용

padding과 margin 의 차이
padding은 버튼 내의 글자와 버튼 테두리 사이의 공백을 조절
margin은 레이아웃 배치(버튼-버튼,버튼-TextView 등)의 공백을 조절

ScrollView 사용시 하위 뷰가 match_parent가 안될 경우 android:fillViewport="true" 를 추가 해주면 된다.
