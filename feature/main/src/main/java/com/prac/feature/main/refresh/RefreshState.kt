package com.prac.feature.main.refresh

enum class RefreshState {
    Default,
    PullingDown,
    ReachedThreshold,
    Refreshing,
    CompleteRefreshing
}