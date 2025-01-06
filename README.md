# Github App

- **Summary**
    
    이 프로젝트는 GitHub API 를 사용하여 레파지토리 목록을 불러와 화면에서 표시하고, 각 레포지토리의 세부 정보를 확인할 수 있도록 구현된 프로젝트입니다. 간결한 UI 를 목표로 설계했으며, API 통신, Room 을 통한 Star State 동기화, Exponential Backoff, Compose 등 Android 개발의 다양한 기술을 적용한 프로젝트입니다.
    
- **Built With**

    `Coroutine`  `Flow`  `Compose`  `Room`   `Paging3`  `Hilt`  `ProtoDataStore`  `Retrofit2` `OkHttp3`  `Glide`  `Markwon`
    
- **Architecture**

    android app architecture ( UI layer -> Domain layer (optional) -> Data layer ) 를 참고하여 설계했으며, MVI (Model - View - Intnet ) 패턴을 적용하여 상태(state) 를 관리합니다.

## Architecture

이 프로젝트는 android app architecture 를 기반으로 참고하여 설계했습니다.

![Architecture](https://github.com/user-attachments/assets/cc382dc2-f5ea-4505-b141-d5118503de53)

- **`UI layer` ( user interface, presentation )** : `UI layer` 는 사용자와 상호 작용할 수 있는 화면을 구성하는 UI 요소들과 앱 상태를 저장하고 configuration change 의 경우 데이터를 복원하는 ViewModel 로 구성되어 있습니다.

- **`Domain layer`** : `Domain layer` 는 여러 `Repository` 를 활용해야 하는 경우, ViewModel 의 재사용성을 높이기 위해 고려된 layer 이다. 필수적인 경우에만 필요한 `Usecase` 만 구현하여 선택적으로 사용합니다.

- **`Data layer`** : `Data layer` 는 데이터를 원격 API 또는 로컬 데이터베이스와 상호작용하며, 비즈니스 로직을 포함해 데이터를 가공하거나 제공하는 역할을 합니다.

- **`Network layer`** : `Network layer` 는 네트워크 통신과 관련된 작업을 처리하는 layer 입니다.

- **`Auth layer`** : `Auth layer` 는 네트워크 통신 중 인증 정보를 관리하며, API 요청 시 토큰을 포함하거나 갱신하는 등의 역할을 담당합니다.
  
- **`Local layer`** : `Local layer` 는 로컬 데이터베이스 또는 파일 시스템에 데이터를 저장하고 읽어오는 작업을 처리합니다.

### MVI
![MVI](https://github.com/user-attachments/assets/45832345-b661-4f91-819c-8ad0e333a549)

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
![Modularization](https://github.com/user-attachments/assets/c31a2abd-4dbe-44ea-a594-c27614b6e4a4)

- **`app`** : `app 모듈`은 앱의 구조를 정의하고 동작을 제어하는 코드를 포함합니다. 예를 들어, 앱의 진입점 역할을 하는 GithubApp, 내비게이션 설정 및 바텀 네비게이션을 처리하는 Scaffold, 그리고 MainActivity가 이에 해당합니다.
- **`feature`** : `feature 모듈`은 특정 화면이나 기능을 독립적으로 처리하도록 설계된 모듈입니다.
- **`core`** : `core 모듈`은 앱 전반에서 공유되는 코드와 공통 기능을 포함하는 모듈입니다. core 모듈은 feature 모듈과 app 모듈 등에서 필요한 기반을 제공합니다.

## ScreenShots

<p align = "center" >
<img src = "https://github.com/user-attachments/assets/314c67e9-36b2-42cb-9320-464db4034fbb" width="20%" height="30%">
<img src = "https://github.com/user-attachments/assets/3eb08639-b9c8-4869-97e8-557af292d373" width="20%" height="30%">
<img src = "https://github.com/user-attachments/assets/727443ba-1f66-4f0d-81ef-61e546424c2d" width="20%" height="30%">
<img src = "https://github.com/user-attachments/assets/0784dd9c-92bf-4cf5-89f9-b5048bf1a68f" width="20%" height="30%">
</p>



