package app.xquare.xquareinfra.infrastructure.persistence.team.schema

import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceEntity
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
@Table(name = "team_members")
data class TeamMemberPersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserPersistenceEntity,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: TeamMemberPersistenceRole,
)
