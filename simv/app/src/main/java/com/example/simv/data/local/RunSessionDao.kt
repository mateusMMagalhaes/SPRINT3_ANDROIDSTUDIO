package com.example.simv.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operações CRUD na tabela "run_sessions".
 * Todas as operações de escrita são suspend functions e devem ser chamadas
 * dentro de uma coroutine. A leitura usa [Flow] para atualizações reativas.
 */
@Dao
interface RunSessionDao {

    /**
     * Insere uma nova sessão e retorna o ID gerado automaticamente.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: RunSessionEntity): Long

    /**
     * Atualiza uma sessão existente (identificada pelo [RunSessionEntity.id]).
     */
    @Update
    suspend fun update(session: RunSessionEntity)

    /**
     * Remove uma sessão do banco.
     */
    @Delete
    suspend fun delete(session: RunSessionEntity)

    /**
     * Retorna todas as sessões ordenadas da mais recente para a mais antiga.
     * O [Flow] emite uma nova lista sempre que os dados mudam.
     */
    @Query("SELECT * FROM run_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<RunSessionEntity>>

    /**
     * Busca uma sessão específica pelo seu ID.
     */
    @Query("SELECT * FROM run_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Int): RunSessionEntity?
}
