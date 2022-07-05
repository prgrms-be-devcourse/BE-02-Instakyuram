# 인스타뀨램

## 🤲🏻 프로젝트 소개

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
- `Flyway`

### FrontEnd

- `Thymeleaf`

### 문서/협업

- `Jira`
- `Notion`
- `Slack`
- `Git`
- `Mermaid js`

## 🖼 ERD

![image](https://user-images.githubusercontent.com/50834204/174832512-4f3741d7-8728-4135-85a0-22714ac2917a.png)

## 💻 개발환경

### 로컬 개발 환경 구축 (Infra)

어떠한 환경에서도 개발 환경을 구축할 수 있도록 하였습니다.
<br/>
**도커 설치** 후에 아래의 소개 페이지로 가시면 간단한 명령어와 함께 로컬 환경을 구축할 수 있습니다.
<br/>

**🐳 도커 설치 방법**

[도커 맥 설치 페이지](https://docs.docker.com/desktop/mac/install/) / [도커 윈도우 설치 페이지](https://docs.docker.com/desktop/windows/install/)
<br/>
각각 환경에 맞게 위의 페이지에서 설치 후에 사용 가능 합니다.

**🧑🏻‍💻 명령어 사용 방법**

```shell
# 1. 먼저 infra 폴더로 이동해주세요.
# 2. setup을 실행해주세요. (최초에 한번만 실행하면 됩니다!)
./setup

# 3. 이제 사용하는 목적에 따라 각각의 실행 스크립트를 실행해주세요.
# Mysql
./mysql

# Redis
./redis

# Sonarqube
./sonarqube
```

**🫥 Container 폴더 설명**

```shell
# 백그라운드 실행 방법
# 각 **_container 의 `start` 스크립트 실행
# ex) Mysql
./mysql_container/start

# 종료 방법
# 각 **_container 의 `stop` 스크립트 실행
# ex) Mysql
./mysql_container/stop

# 삭제 방법
# 각 **_container의 `remove` 스크립트 실행
# ex) Mysql
./mysql_container/remove
```

**🌐 웹 링크**

```
* Sonarqube
 http://localhost:9001
  * user: admin
  * password: admin

* mysql
 http://localhost:8082
  * user: root
  * password: password

* Redis
 http://localhost:8081
```

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

**node가 없다면**

```
brew install node
```

**설치**

```
npm install
```

### Git submodule

Submodule Repository - [https://github.com/instakyuram/instakyuram-config](https://github.com/instakyuram/instakyuram-config) private 저장소로써, 권한이 없다면 접근 불가능 합니다.

서버의 민감 정보를 메인 프로젝트에 포함하고 그대로 Git에 노출한다면 악용될 가능성이 있습니다. 이러한 문제들을 해결 하기 위해
Private 한 저장소에 서버의 민감 정보(설정파일, 비밀 키 등)를 저장하고 프로젝트에 Submodule로서 관리해 안전하게 민감 정보를 관리할 수 있도록 했습니다.

Submodule에 대한 더 자세한 정보 및 사용법은 [Instakyuram 기술문서](https://www.notion.so/backend-devcourse/793183425a3b4736948bcdd9fe6e62cb)의
[Git Submodule](https://www.notion.so/backend-devcourse/GitHub-Submodule-145bd32e810e42748369b196848e0f82)을 참고 하세요.
