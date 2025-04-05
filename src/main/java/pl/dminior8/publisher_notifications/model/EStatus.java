package pl.dminior8.publisher_notifications.model;

public enum EStatus {
    DELIVERED,
    PENDING, // Oczekuje na pierwszą próbę dostarczenia
    IN_PROGRESS, // Wiadomość jest w trakcie próby dostarczenia
    RETRY_PENDING_1, // Pierwsza próba dostarczenia nie powiodła się, oczekuje na ponowną próbę
    RETRY_PENDING_2,
    FAILED
}
