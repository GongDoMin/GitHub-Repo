이 프로젝트는 GitHub API 를 사용하여 레파지토리 목록을 불러와 화면에서 표시하고, 각 레포지토리의 세부 정보를 확인할 수 있도록 구현된 프로젝트입니다. 간결한 UI 를 목표로 설계했으며, API 통신, Room 을 통한 Star State 동기화, Exponential Backoff, Compose Migration을 통해 Android 개발의 다양한 기술을 적용한 예시 프로젝트입니다.

## Built With

`Coroutine` `Flow` `Compose` `Room` `Paging3` `Hilt` `ProtoDataStore` `Retrofit2` `OkHttp3` `Glide` `Markwon`

## Architecture

이 프로젝트는 `MVVM` 와 `MVI`를 기반으로 설계되었습니다.

![Architecture](https://github.com/user-attachments/assets/8af47a9f-5ec2-463d-8659-e6d110ea169a)


- **`UI layer`** : `UI layer` 는 사용자와 상호 작용할 수 있는 화면을 구성하는 UI 요소들과 앱 상태를 저장하고 configuration change 의 경우 데이터를 복원하는 ViewModel 로 구성되어 있습니다.

- **`Data layer`** : `Data layer` 는 로컬 데이터베이스에서 데이터를 쿼리하고 네트워크에서 데이터를 요청하는 등 비즈니스 로직을 포함합니다.

### MVI Architecture
![MVI](https://github.com/user-attachments/assets/59484a94-e767-4ceb-85f4-8280ace6e199)

- **`View`**
    - 사용자와 상호작용하며 Action을 발행합니다.
    - View는 uiState를 관찰하고 이를 UI로 렌더링합니다.

- **`Action`**
    - 사용자의 입력 또는 이벤트를 의미합니다.
    - View에서 발생하며, ViewModel로 전달됩니다.

- **`Mutation`**
    - ViewModel에서 처리된 Action을 바탕으로 상태를 변경할 때 사용됩니다.
    - Mutation은 상태를 어떻게 변경할지에 대한 정보만 담고 있으며, 이를 통해 StateModel에서 새로운 UI 상태를 생성합니다.

- **`StateModel`**
    - UI 상태를 관리하며, Mutation을 통해 상태를 업데이트합니다.
    - Reducer를 사용하여 기존 상태를 기반으로 새로운 uiState를 생성합니다.
    - 새로운 상태는 View로 전달되어 UI를 갱신합니다.

- **`Reducer`**
    - 이전 상태와 Mutation을 조합하여 새로운 uiState를 생성하는 함수입니다.
    - 상태 변경 로직은 Reducer에 집중되어 있습니다.

## Modularization
![Modularization](https://github.com/user-attachments/assets/19d78532-7b72-4cf5-8684-28912e70f11a)

- **`app`** : `app 모듈`은 앱 구조를 구성하고 동작을 제어하는 코드를 포함합니다. 예를 들어, GithubApp 과 같이 앱의 진입점이 되거나, 내비게이션 설정 및 바텀 네비게이션을 처리하는 scaffold 그리고 MainActivity 가 이에 해당합니다. 
- **`feature`** : `feature 모듈`은 특정 기능을 처리하도록 설계된 모듈입니다. 네비게이션이나 특정 상황에서 feature 모듈 간 연결이 필요한 경우 의존성을 허용하고 있습니다. 현재 feature:home 은 feature:main, feature:detail 에 대해 참조하고 있습니다.
- **`core`** : `core 모듈`은 앱 전반에서 공유되어야 할 코드를 포함하고 있는 모듈입니다. core 모듈은 feature 모듈과 app 모듈과 같은 상위 모듈에서 필요한 기반을 제공합니다.


## ScreenShots

<p align = "center" >
<img src = "https://github.com/user-attachments/assets/314c67e9-36b2-42cb-9320-464db4034fbb" width="20%" height="30%">
<img src = "https://github.com/user-attachments/assets/3eb08639-b9c8-4869-97e8-557af292d373" width="20%" height="30%">
<img src = "https://github.com/user-attachments/assets/727443ba-1f66-4f0d-81ef-61e546424c2d" width="20%" height="30%">
<img src = "https://github.com/user-attachments/assets/0784dd9c-92bf-4cf5-89f9-b5048bf1a68f" width="20%" height="30%">
</p>



