# STACK & SKILL
<br><br>


> ## Android
<br><br>
***
<br>

<br>

> ## socketio

version issue
- nodejs - 4.2.0
- android - 2.0.0
<br>

> ## Retrofit
@Get, @Post, @MultiPart 사용
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
- MultiPart는 회원가입시 사진과 회원정보를 한번에 보낼 때 @Post와 함께 사용 
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
    
