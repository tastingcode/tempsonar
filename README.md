# tempsonar

Spring Boot, Gradle, Jenkins, SonarQube를 작은 단위로 학습하기 위한 토이프로젝트입니다.

목표는 거대한 구조를 바로 따라 만드는 것이 아니라, 아래 흐름을 직접 눈으로 확인하는 것입니다.

```text
Jenkins Job 실행
  -> Gradle test/build 실행
    -> Gradle Sonar task 실행
      -> SonarQube 서버에 분석 결과 업로드
```

## 구성

- Spring Boot API: `/hello`, `/add`
- Gradle: build, test, jacoco, sonar task
- Jenkins: `Jenkinsfile` 기반 pipeline
- SonarQube: Docker Compose로 실행
- PostgreSQL: SonarQube 저장소

## 1. 로컬 테스트

```bash
./gradlew clean test
```

## 2. Docker Compose 실행

```bash
docker compose up -d --build
```

접속 주소는 다음과 같습니다.

- Jenkins: http://localhost:8080
- SonarQube: http://localhost:9000

SonarQube 초기 계정은 보통 `admin / admin`입니다. 최초 로그인 후 비밀번호를 변경합니다.

Jenkins 초기 비밀번호는 다음 명령으로 확인합니다.

```bash
docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

## 3. SonarQube token 만들기

SonarQube에 로그인한 뒤 다음 순서로 token을 만듭니다.

```text
My Account
  -> Security
    -> Generate Tokens
```

token은 Jenkins Credentials에 `sonarqube-token` ID로 등록합니다.

## 4. Jenkins Pipeline 만들기

학습용으로는 Jenkins에서 Pipeline job을 하나 만들고, Git 저장소의 `Jenkinsfile`을 읽도록 설정하는 방식이 가장 자연스럽습니다.

```text
Definition=Pipeline script from SCM
SCM=Git
Repository URL=https://github.com/tastingcode/tempsonar
Branch Specifier=*/main
Script Path=Jenkinsfile
```

Jenkins가 Git 저장소를 checkout하면 현재 workspace에 소스가 준비됩니다. 따라서 Jenkinsfile은 별도 `SOURCE_DIR` 없이 현재 workspace에서 `./gradlew`를 실행합니다.

## 5. Jenkins에서 실행하기

Pipeline 실행 전에 Jenkins Credentials를 확인합니다.

```text
Kind=Secret text
ID=sonarqube-token
Secret=<SonarQube에서 만든 token>
```

Jenkinsfile은 Credentials에서 token을 가져온 뒤 Gradle에 명시적으로 넘깁니다.

```text
SONAR_HOST_URL=http://sonarqube:9000
SONAR_PROJECT_KEY=tempsonar
SONAR_TOKEN=credentials('sonarqube-token')
```

`ERROR: sonarqube-token`으로 실패하면 Jenkins가 해당 ID의 Credential을 찾지 못한 것입니다. Jenkins에서 `Manage Jenkins -> Credentials -> System -> Global credentials -> Add Credentials`로 이동한 뒤 위 값 그대로 등록합니다.

Jenkins Credentials 화면에서 `Description`이 아니라 `ID`가 `sonarqube-token`인지 확인합니다. ID를 비워두고 만들면 Jenkins가 UUID 형태의 ID를 자동 생성하므로, Jenkinsfile의 `credentialsId`와 매칭되지 않습니다.

실행이 끝나면 SonarQube의 `tempsonar` 프로젝트에서 분석 결과를 확인합니다.

## 학습 순서

1. Spring Boot 테스트가 Jenkins에서 실행되는지 확인합니다.
2. Jenkins artifact에 jar와 테스트 리포트가 남는지 확인합니다.
3. SonarQube token을 만들고 Gradle `sonar` task를 실행합니다.
4. SonarQube에서 bugs, vulnerabilities, code smells, coverage를 확인합니다.
5. Jenkinsfile에 간단한 parameter를 추가해 tps의 `SEPARATE_ID` 개념을 흉내냅니다.
6. Trigger Job을 하나 더 만들어서 분석 Job을 호출해봅니다.

자세한 단계별 학습 가이드는 `docs/learning-roadmap.md`를 참고하세요.
