이 프로젝트는 GitHub API 를 사용하여 레파지토리 목록을 불러와 화면에서 표시하고, 각 레포지토리의 세부 정보를 확인할 수 있도록 구현된 프로젝트입니다. 간결한 UI 를 목표로 설계했으며, API 통신, Room 을 통한 Star State 동기화, Exponential Backoff, Compose Migration을 통해 Android 개발의 다양한 기술을 적용한 예시 프로젝트입니다.

### Built With

---

- Coroutine + Flow 를 통한 비동기 처리
- Jetpack
    - Compose : 안드로이드 UI 를 선언적으로 작성할 수 있게 도와주는 최신 툴킷
    - ViewModel : 화면 회전과 같은 configuration change 에서 데이터가 보존되며 UI 관련 데이터를 관리
    - Room : 데이터베이스에 보다 간편하게 접근하고 관리
    - RemoteMediator : 네트워크에서 추가 데이터를 로드하고 로컬 데이터베이스에 저장 후 로컬 데이터베이스에서 데이터를 로드하고 표시
    - Hilt : for dependency injection
    - ProtoDataStore : protocol buffer 를 통해 타입 안전성이 보장된 데이터 저장소
- Retrofit2 & OkHtpp3 을 통한 네트워크 통신
- Glide : 네트워크로부터 이미지 로드

### Architecture

---

![image](https://github.com/user-attachments/assets/66dddd57-2e94-4832-a9a3-886dfaf4cd91)

- **Data**: Repository Interface, RepositoryImpl
- **Network**: DataSource Interface, DataSourceImpl, Service
- **Local**: DataSource Interface, DataSourceImpl, DB, DataStore

### ScreenShots

---

<p align = "center" >
<img src = "https://github.com/user-attachments/assets/26e8bd1e-fdc4-469a-b16a-53c6df67568a" width = "30%" height = "30%">

<img src = "https://github.com/user-attachments/assets/e1ff7c88-7283-49df-a3a2-a8e507dc6a03" width = "30%" height = "30%">

<img src = "https://github.com/user-attachments/assets/9de4f612-8f8b-41d0-8c5f-5b7d64f22cc2" width = "30%" height = "30%">
</p>
