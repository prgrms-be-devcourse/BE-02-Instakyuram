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

### Git submodule
Submodule Repository - [https://github.com/instakyuram/instakyuram-config](https://github.com/instakyuram/instakyuram-config)
private 저장소로써, 권한이 없다면 접근 불가능 합니다.

- 처음 프로젝트 Clone 받았을 때
  - Submodule initializing을 해줘야 합니다.
  - 항상 서브 모듈은 main 브랜치에 업데이트 되어야 합니다.
```bash
# 서브모듈 이니셜라이징
git submodule init

# 서브 모듈 업데이트
git submodule update

# 모든 서브모듈에서 main으로 checkout 합니다.
git submodule foreach git checkout main
```

- 이 후 변경 사항을 가져올 때
```bash
git submodule update --remote

#또는
 
git submodule update --remote --merge
```

- Submoudle에 대한 변경 사항을 commit/push 할 때 주의점
  - Submodule에 먼저 commit/push를 하고 MainModule에서 commit/push해야 변경사항을 추적 가능합니다.

- 안전하게 Push할 수 있는 명령어
```bash
#submodule이 모두 push된 상태인지 확인하고, 확인이 되면 main project를 push
git push --recurse-submodules=check
# submodule을 모두 push하고, 성공하면 main project를 push
git push --recurse-submodules=on-demand 
```

- 위 기능을 기본 push 명령어로 설정하는 법
```bash
# push시에 항상 check
git config push.recurseSubmodules check
# push 시에 항상 서브 모듈을 push
git config push.recurseSubmodules on-demand
```