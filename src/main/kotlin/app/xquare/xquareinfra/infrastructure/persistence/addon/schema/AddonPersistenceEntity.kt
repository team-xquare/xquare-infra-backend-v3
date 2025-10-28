package app.xquare.xquareinfra.infrastructure.persistence.addon.schema

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
@Table(name = "addons")
data class AddonPersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: TeamPersistenceEntity,
    @Column(nullable = false)
    val type: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tier: AddonPersistenceTier,
    @Column(nullable = false)
    val storageGi: Int,
)
