# 인스타뀨램

##  🤲🏻 프로젝트 소개

인스타그램 서비스를 클론 코딩하는 프로젝트입니다.

## 👬 팀 소개

<table>
  <tr>
    <td>
        <a href="https://github.com/midasWorld">
            <img src="https://avatars.githubusercontent.com/u/93169519?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/HyoungUkJJang">
            <img src="https://avatars.githubusercontent.com/u/50834204?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/whyWhale">
            <img src="https://avatars.githubusercontent.com/u/67587446?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/pjh612">
            <img src="https://avatars.githubusercontent.com/u/62292492?v=4" width="100px" />
        </a>
    </td>
    <td>
        <a href="https://github.com/HYEBPARK">
            <img src="https://avatars.githubusercontent.com/u/35947674?v=4" width="100px" />
        </a>
    </td>
  </tr>
  <tr>
    <td><b>곽동운</b></td>
    <td><b>김형욱</b></td>
    <td><b>김병연</b></td>
    <td><b>박진형</b></td>
    <td><b>박혜빈</b></td>
  </tr>
  <tr>
    <td><b>Product Owner</b></td>
    <td><b>Scrum Master</b></td>
    <td><b>Developer</b></td>
    <td><b>Developer</b></td>
    <td><b>Developer</b></td>
  </tr>
</table>

## 🛠 기술 스택

### BackEnd
- `Java 17`
- `Gradle 7.4.2`
- `Spring Boot 2.7.0`
- `MySQL`
- `JPA`
- `JUnit5 / Mockito`
- `Swagger`

### FrontEnd
- `Thymeleaf`

## ERD

![image](https://user-images.githubusercontent.com/50834204/174832512-4f3741d7-8728-4135-85a0-22714ac2917a.png)

### .env 파일 설정 값
프로젝트 루트 경로에 위치하도록 합니다.

.env
```
MYSQL_USERNAME=   #로컬 환경의 MySQL 유저네임을 입력합니다.
MYSQL_PASSWORD=   #로컬 환경의 MySQL 패스워드를 입력합니다.
SHOW_SQL=         #true 혹은 false로 입력합니다.
ENCRYPTOR_KEY=    #암호화 키를 입력합니다.
```

### Husky
git hook을 프로젝트 내에서 공유할 수 있도록 합니다.

- node가 없다면
```
brew install node
```

- 설치 
```
npm install
```