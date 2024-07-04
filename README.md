### SWYP 4기 7팀 B.E. REST API
<img src="https://github.com/SWYP-LUCKY-SEVEN/back-end/assets/58322578/dc530728-a45d-4a32-8abc-6587f6db0ae8" width="200" height="200"/>

## 쇼터디 - 딱 맞는 온라인 스터디메이트 찾기

학습자의 목표달성을 돕는 맞춤형 스터디 매칭 및 운영 서비스

## 📝 프로젝트 소개

뚜렷한 목적을 가진 성인 학습자가 증가하는 추세인 현재.

꾸준한 학습의 동기가 되어줄 스터디그룹의 수요가 꾸준히 증가하고 있습니다.

단기간 목표를 공유하며 동기부여를 받을 수 있고, 학습자의 스터디 목적 달성을 위해 스터디의 매칭부터 진행까지 돕는 서비스를 제공하고자 합니다.

## ⭐ 배포 링크 - [Showtudy](https://shortudy.vercel.app/)
### 💡 주요 기능
![Untitled](https://github.com/SWYP-LUCKY-SEVEN/back-end/assets/58322578/e8bbf0d4-d406-4d53-8e76-5099e72d8641)

## 🔨 프로젝트 구조
### 백엔드 구성
<img src="https://github.com/SWYP-LUCKY-SEVEN/back-end/assets/58322578/3e079f15-cf25-4672-9f06-dfe3942f273c" width="700" height="404"/>

### RDB Design
![최종2](https://github.com/SWYP-LUCKY-SEVEN/back-end/assets/58322578/52d169e6-2895-4cd8-af68-6ecf1e30b3c6)

## REST API 제공 기능 - [명세서](https://dori2005.notion.site/REST-API-d5ed0c44bbe746e9bcd62102b9d489ae?pvs=4)
- **회원 관련**
    - 회원 가입 (Kakao OAuth)
    - 회원 정보 수정
    - 마이페이지 조회
        - 찜 한 스터디 확인
        - 참여 중인 스터디 확인
        - 참가 신청한 스터디 확인
    - 회원 탈퇴
- **스터디 관련**
    - 스터디 생성
    - 스터디 빠른 매칭
    - 스터디 검색
        - 최근 검색어 관리
        - 인기 검색어 관리
    - 스터디 즉시 참가
    - 스터디 참가 신청
    - 스터디 참가 수락/거절
    - 스터디 내보내기
    - 스터디 상세 정보 조회
    - 스터디 목표 관련
        - 공용/개인 목표 생성
        - 공용/개인 목표 수정
        - 공용/개인 목표 삭제
    - 스터디 삭제
    - 팀원 평가

## 🔧 Stack
### FRONT-END
- **Language** : TypeScript
- **Library & Framework** : React.js, Next.js, React-Query, Zustand
- **Deploy** : Vercel
### BACK-END 
- **Server** : AWS EC2
- **Deploy** : Jenkins, NGINX
#### REST API SERVER
- **Language** : JAVA
- **Library & Framework** : Sprint Boot, SpringSecurity, Swagger, JWT
- **ORM** : JPA, QueryDSL
- **DB** : MySQL (AWS RDS)
#### CHAT SERVER
- **Language** : TypeScript
- **Library & Framework** : Node.js, Express.js, Socket.IO, JWT
- **ORM** : Mongoose
- **DB** : MongoDB(Atlas), Redis(Redis Cloud)

## Developer
|BACK-END|BACK-END|FULL-STACK|FRONT-END|FRONT-END|
|:-:|:-:|:-:|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/58322578?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/58907538?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/81890292?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/58941022?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/129826514?v=4" width="150" height="150"/>|
|[곽도훈](https://github.com/dori2005)|[박재은](https://github.com/park-jaeeun)|[김민재](https://github.com/minjamie)|[신현수](https://github.com/scato3)|[이수현](https://github.com/shuding0307)|
