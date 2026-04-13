# 학습 로드맵

## Step 1. Jenkins는 명령 실행기다

먼저 Jenkins에서 아래 명령이 실행되는지만 확인합니다.

```bash
./gradlew clean test
```

이 단계의 목표는 Jenkins가 내 프로젝트 코드를 가져와서 테스트를 실행하는 모습을 보는 것입니다.

## Step 2. Jenkins Pipeline을 이해한다

`Jenkinsfile`의 stage를 하나씩 봅니다.

```text
Prepare
Test
Build
SonarQube Analysis
```

각 stage는 Jenkins 화면에서 별도의 칸으로 보입니다. 실패한 stage도 바로 확인할 수 있습니다.

## Step 3. SonarQube는 결과 저장소다

Jenkins가 `./gradlew sonar`를 실행하면 Gradle Sonar plugin이 SonarQube 서버로 분석 결과를 보냅니다.

이때 필요한 값은 세 가지입니다.

```text
sonar.host.url
sonar.token
sonar.projectKey
```

## Step 4. tps의 SEPARATE_ID를 흉내낸다

tps에서는 `SEPARATE_ID`가 SonarQube branch 이름처럼 사용됩니다.

토이프로젝트에서는 나중에 Jenkinsfile에 아래 파라미터를 추가해봅니다.

```groovy
string(name: 'SEPARATE_ID', defaultValue: 'main', description: '분석 branch 이름')
```

그리고 sonar 실행 옵션에 아래 값을 추가합니다.

```bash
-Dsonar.branch.name=${SEPARATE_ID}
```

단, SonarQube Community Edition은 branch analysis를 제한할 수 있습니다. 이 경우 `sonar.projectKey`를 바꾸는 방식으로 실행 단위를 나눠보면 됩니다.

## Step 5. Trigger Job을 만든다

Trigger Job은 실제 분석을 하지 않고, 분석 Job을 호출하는 Job입니다.

개념은 아래와 같습니다.

```text
Trigger Job
  -> build job: "tempsonar-analysis"
    -> SONAR_PROJECT_KEY, SONAR_TOKEN 같은 parameter 전달
```

tps의 티켓 로직 전체를 구현할 필요는 없습니다. 처음에는 `RUN_ID` 같은 문자열 하나만 만들어서 downstream job에 넘기면 충분합니다.

## Step 6. TPS API 흉내내기

나중에 Spring Boot API에서 Jenkins REST API를 호출해봅니다.

처음 목표는 이것 하나면 됩니다.

```text
POST /api/analysis/run
  -> Jenkins buildWithParameters 호출
```

이 단계에 오면 tps의 `PipelineProcessor`, `JenkinsService`, `AnalysisService`가 왜 분리되어 있는지 훨씬 잘 보입니다.
