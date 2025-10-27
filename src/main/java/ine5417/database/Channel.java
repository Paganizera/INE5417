package ine5417.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "channels")
public class Channel {
    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private String description;

    @Column(nullable = false, updatable = false)
    private String email;

}
