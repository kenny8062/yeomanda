# STACK & SKILL
<br><br>


> ## Android
- Java언어 사용
<br><br>
***
<br>



> ## Retrofit Request Method
@Get, @Post 사용
- Get은 Read, 정보 조회용도
  ``` java
  @GET("/menuBar/getProfile")
    Call<ProfileResponseDto> getMyProfile(@Header("Authorization") String userToken
    );
  ```
- Post는 BODY에 전송할 데이터를 담아서 서버에 생성
  ```java
  @POST("/user/login")
    Call<LoginResponseDto> login(
            @Body LoginDto loginDto
            );
  ```
- 추가로 MultiPart는 회원가입시 사진(파일)과 회원정보를 한번에 보낼 때 @Post와 함께 사용 
    ```java
    @Multipart
    @POST("/user/signup")
    Call<JoinResponseDto> uploadJoin(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("name") RequestBody name,
            @Part("sex") RequestBody sex,
            @Part("birth") RequestBody birth,
            @Part MultipartBody.Part[] totalSelfImage);
    ```
    
<br><br>
> ## Retrofit execute vs enqueue
- execute
동기처리시 사용
단, 메인쓰레드에서 네트워크 연결을 제한하므로 새로운 Thread를 생성 하여 사용 가능

- enqueue
비동기처리시 사용(자동으로 백그라운드 쓰레드로 동작)

<br><br>
> Retrofit ISSUE

execute를 사용하여 받아온 데이터로 UI 수정을 해야 하는데 UI 수정은 MainThread에서 밖에 못한다.
그러므로 execute를 사용한 새로운 Thread에서 결과값을 받아오면 MainThread로 보내게 진행했다.
이 때 MainThread가 데이터를 받기 전에 다음 동작을 하기 때문에 while문으로 response값이 Null이 아닐 때 까지 강제 대기시켰다(Response가 MainThread로 전송이 되면 실행 되게끔).
하지만 이것은 사실상 대기라기보다는 while문을 계속 돌고 있는것이기 때문에 자원 낭비라고 생각한다.
그래서 Kotlin 언어로 enqueue를 사용하여 프로젝트를 다시 만들었다.
- Kotlin Link
https://github.com/kenny8062/YeoManDa_Kotlin


<br><br>
***
<br>

> ## Firebase 클라우드 메시징
- In-App 채팅시 상대에게 알림을 보내기 위해 사용
- Firebase 토큰은 기기마다 혹은 주기적으로 변경되기 때문에 로그인시 서버로 Token값 갱신
``` java
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FcmTokenTag", "FCM 등록 토큰을 가져오지 못했습니다.",
                            task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        fcmToken = task.getResult();
                    }
                });
```

<br><br>
***
<br>

> ## 이메일인증
- javax.mail Api 사용
- Math.random() 함수를 이용하여 랜덤한 인증코드 생성
- https://github.com/kenny8062/yeomanda/blob/main/yeomanda/front/Yeomanda/app/src/main/java/com/example/yeomanda/EmailAuthentication/GmailSender.java

<br><br>
***
<br>

> ## socketio
- 채팅을 위한 서버와 클라이언트의 양방향 실시간 통신
- mSocket.on(“EVENT_NAME”,mListener) 서버에서 보낸 이벤트 듣기
- mSocket.emit("chatRoom", roomInfo); 데이터 담아서 서버로 이벤트 발생 시키기
- mSocket.emit("chatRoom"); 데이터 없이 서버로 이벤트 발생 시키기
``` java
        //Socket연결
        try {
            mSocket= IO.socket("URL");
            mSocket.connect();
            Log.d("connect","ok");
        } catch (URISyntaxException e) {
            Log.e("ChatSocketError",e.getReason());
            e.printStackTrace();
        }
        //mSocket.on 으로 서버에서 보낸 이벤트 듣기
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        
        // 서버에서 Socket.EVENT_CONNECT 이벤트를 Android로 보낼 시 동작
        //mSocket.emit으로 서버로 이벤트 전송
        Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("chatRoom", roomInfo);
        }
    };
        
```

>version issue
- nodejs - 4.2.0
- android - 2.0.0
<br>
