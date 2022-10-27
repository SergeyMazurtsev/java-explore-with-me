package ru.practicum.ewm.comments.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.events.model.Event;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "commentor_id")
    private User commentor;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @Column(name = "comment")
    private String comment;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "rating")
    private Integer rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return id != null && Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
