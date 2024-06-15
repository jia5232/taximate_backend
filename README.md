# Taximate_Backend
대학생을 위한 택시 쉐어 플랫폼 "택시메이트"의 백엔드 리포지토리입니다.

## 프로젝트 소개
등하교시 버스나 지하철의 높은 인구 밀도로 
불편을 겪는 대학생들이 택시를 공유하여 </br>1/n 가격으로 저렴하게 이용할 수 있도록 도와주는 모바일 앱 서비스입니다.

[(https://github.com/jia5232/KIRI_Backend/assets/83686088/e8d9cbe5-ee22-47d4-9945-b0b0b9aeea49)](https://github.com/jia5232/Taximate_Backend/assets/83686088/821d900d-73f8-4665-8c91-041baf1a0973)


## 프로젝트 개발 기간
* 2024.01 ~ 2024.06

## 개발 인원
* Jia Kwon (개인 프로젝트)

## 백엔드 개발 환경
- Java 17
- Springboot 3.2.1
- JPA (Hibernate 6.4.1)
- Mysql 8.3.0

## 도메인 다이어그램

<img src="https://github.com/jia5232/KIRI_Backend/assets/83686088/a789887f-6c8a-4f98-a3c7-83d62a047945" width="700">

## 인증 로직 시퀀스 다이어그램
* 로그인
<img src="https://github.com/jia5232/KIRI_Backend/assets/83686088/3114dbc9-2cbb-440b-9440-683d96ab1a4a" width="400">
</br>
</br>
* 토큰 재발급
</br>
<img src="https://github.com/jia5232/KIRI_Backend/assets/83686088/f36a8236-b5ac-48f2-9a77-f881817e4fcb" width="400">



## 주요 기능 소개

<img width="250" alt="로그인" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/690d14b2-8544-4573-9448-011204555719">
<br/>
<img width="200" alt="회원가입1" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/0af47cca-ce8e-4ea2-8daa-ab133eae407a">
<img width="200" alt="회원가입2" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/c74634bb-5158-462e-893c-07185099d085">
<img width="200" alt="회원가입3" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/2f4ee28f-8b6c-4eea-b113-c755457da69a">
<img width="200" alt="회원가입4" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/af65b80f-5ac4-4ba9-8977-41503d07e61e">
<img width="200" alt="회원가입5" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/00839968-c17e-45a9-ab2f-7fab89ff5e55">
<img width="200" alt="회원가입6" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/c61c4c03-67dd-41c8-9669-b051ae787e0e">
</br>
* 대학교 이메일을 통해 인증 후 회원가입 및 로그인을 진행하여 서비스를 이용할 수 있습니다.
</br>
* Spring Security와 Filter, jwt를 활용해 토큰 발급 및 재발급 로직을 구현했습니다.
</br>
</br>
</br>
<img width="250" alt="메인페이지" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/39a842ff-5449-41c1-80c7-d70a8059eaf6">
<img width="250" alt="글 작성1" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/fd9b131b-e80b-4a1d-a745-fc9dd48ca393">
<img width="250" alt="글 상세" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/f44ec50d-d073-4d24-8c38-75c26bbb02bb">
</br>
* 출발지 혹은 도착지를 설정하고 출발 시간, 소요 금액, 탑승 인원 등을 기재해 새로운 모임을 개설하거나, 기존에 이미 개설된 모임에 참여할 수 있습니다.
</br>
</br>
* 회원의 대학교에 따라 메인 페이지에 보이는 서비스명이 달라집니다. 
</br>(ex, 국민끼리, 성신끼리, 고려끼리)
</br>
</br>
</br>

<img width="250" alt="채팅방" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/969ba4bc-46bd-4066-91b8-76a03099a735">
<img width="250" alt="채팅 리스트" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/a66564f4-5c22-466b-8fd4-31765ee2fdbd">
</br>
* 모임에 참여하면 채팅을 통해 약속장소를 정하고, 정산을 진행하여 택시를 저렴하게 이용할 수 있습니다.
</br>
* WebSocket을 기반으로 동작하는 STOMP(텍스트 기반 메시지 프로토콜)를 활용해 채팅을 구현했습니다.
</br>
</br>
</br>

<img width="250" alt="마이페이지1" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/6c2d5e5d-29ef-4b3b-a9a7-0deaed996ce7">
<img width="250" alt="마이페이지2" src="https://github.com/jia5232/Taximate_Backend/assets/83686088/2c2fcd43-4a3c-4d37-ac0c-12af2e4e0449">
</br>
* 마이페이지에서 회원 정보, 내가 작성한 글, 앱 정보, 문의하기, 로그아웃 등의 기능을 이용할 수 있습니다.
</br>
* 이외에도 서비스 로직상 발생 가능한 예외 상황들을 처리하고 사용자에게 적절한 알림을 보내도록 구현했습니다.
</br>
* 스프링부트의 exception handler를 활용하여 컨트롤러 계층의 에러를 메서드로 처리해주었습니다.
