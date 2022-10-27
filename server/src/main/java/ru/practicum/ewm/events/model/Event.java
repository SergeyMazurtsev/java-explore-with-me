package ru.practicum.ewm.events.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.requests.model.Request;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;
    @Column(name = "location_lat")
    private Double locationLat;
    @Column(name = "location_lon")
    private Double locationLon;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;
    @Column(name = "title")
    private String title;
    @OneToMany(mappedBy = "eventId")
    private Set<Request> requests = new HashSet<>();
    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations;
    @OneToMany(mappedBy = "event")
    private Set<Comment> comments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
