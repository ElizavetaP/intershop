package ru.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Column("username")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(max = 100, message = "Пароль слишком длинный")
    @Column("password")
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Column("email")
    private String email;

    @Column("role")
    private Role role;

    @Column("enabled")
    private Boolean enabled;

    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * Создает нового пользователя
     * Дата создания, роль и статус enabled установятся автоматически
     * через БД DEFAULT значения при сохранении
     */
    public static User createNew(String username, String password, String email) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }


    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isAccountActive() {
        return Boolean.TRUE.equals(this.enabled);
    }
}
