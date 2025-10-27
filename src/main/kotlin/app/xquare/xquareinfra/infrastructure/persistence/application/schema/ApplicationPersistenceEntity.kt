package app.xquare.xquareinfra.infrastructure.persistence.application.schema

import app.xquare.xquareinfra.infrastructure.persistence.team.schema.TeamPersistenceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "applications")
data class ApplicationPersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: TeamPersistenceEntity,
    @Column(unique = true, nullable = false)
    val name: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ApplicationPersistenceStatus,
    /*
    status 가 UNPUBLISHED 일 때의 configuration 저장
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    val configuration: String,
)
