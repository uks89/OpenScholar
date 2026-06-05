package org.openscholar.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.domain.repository.KnowledgeGraphRepository
import org.openscholar.app.domain.repository.MemoryRepository
import org.openscholar.app.model.Document
import org.openscholar.app.model.Memory
import org.openscholar.app.model.MemoryType
import javax.inject.Inject

data class HomeUiState(
    val recentDocuments: List<Document> = emptyList(),
    val recentMemories: List<Memory> = emptyList(),
    val documentCount: Int = 0,
    val nodeCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val graphRepository: KnowledgeGraphRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            documentRepository.getAllDocuments().collect { documents ->
                _uiState.value = _uiState.value.copy(
                    recentDocuments = documents.take(5),
                    documentCount = documents.size,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            memoryRepository.getMemoriesByType(MemoryType.SHORT_TERM).collect { memories ->
                _uiState.value = _uiState.value.copy(
                    recentMemories = memories.take(5)
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadHomeData()
    }
}
