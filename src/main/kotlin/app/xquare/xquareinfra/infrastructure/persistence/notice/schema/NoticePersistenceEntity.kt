package app.xquare.xquareinfra.infrastructure.persistence.notice.schema

import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "notices")
data class NoticePersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: UserPersistenceEntity,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,
    @Column(name = "file_url", length = 1000)
    val fileUrl: String? = null,
)
