package com.railway.InRailway.service;

import com.railway.InRailway.dto.ProfileDtos;
import com.railway.InRailway.exception.ApiException;
import com.railway.InRailway.model.User;
import com.railway.InRailway.model.Notification;
import com.railway.InRailway.model.SavedRoute;
import com.railway.InRailway.model.UserSession;
import com.railway.InRailway.repository.ProfileRepository;
import com.railway.InRailway.repository.NotificationRepository;
import com.railway.InRailway.repository.SavedRouteRepository;
import com.railway.InRailway.repository.UserSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private final ProfileRepository profiles;
    private final NotificationRepository notifications;
    private final SavedRouteRepository savedRoutes;
    private final UserSessionRepository sessions;

    public ProfileService(ProfileRepository profiles, NotificationRepository notifications, SavedRouteRepository savedRoutes, UserSessionRepository sessions) {
        this.profiles = profiles;
        this.notifications = notifications;
        this.savedRoutes = savedRoutes;
        this.sessions = sessions;
    }

    @Transactional(readOnly = true)
    public ProfileDtos.Response profile(String username) {
        return response(user(username));
    }

    @Transactional
    public ProfileDtos.Response update(String username, ProfileDtos.UpdateRequest request) {
        User user = user(username);
        if (request.username() != null && !request.username().isBlank()
                && !request.username().equals(user.getUsername())
                && profiles.findByUsername(request.username()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (request.email() != null && !request.email().isBlank()
                && !request.email().equalsIgnoreCase(user.getEmail())
                && profiles.findByEmail(request.email()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (request.username() != null && !request.username().isBlank()) user.setUsername(request.username());
        if (request.email() != null && !request.email().isBlank()) user.setEmail(request.email());
        if (request.fullName() != null) user.setFullName(request.fullName().isBlank() ? null : request.fullName().trim());
        if (request.password() != null && !request.password().isBlank()) user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(request.password()));
        return response(profiles.save(user));
    }

    @Transactional
    public ProfileDtos.Response avatar(String username, String avatarUrl) {
        User user = user(username);
        user.setAvatarUrl(avatarUrl);
        return response(profiles.save(user));
    }

    public List<ProfileDtos.NotificationResponse> notifications(String username) {
        return notifications.findByUserUsernameOrderByCreatedAtDesc(username).stream().map(this::notificationResponse).toList();
    }

    @Transactional public ProfileDtos.NotificationResponse createNotification(String username, ProfileDtos.NotificationRequest request) {
        Notification notification = new Notification(); notification.setUser(user(username)); notification.setTitle(request.title()); notification.setMessage(request.message()); notification.setType(request.type());
        return notificationResponse(notifications.save(notification));
    }
    @Transactional public void markNotificationsRead(String username) { notifications.findByUserUsernameOrderByCreatedAtDesc(username).forEach(n -> n.setReadStatus(true)); }
    @Transactional public void deleteNotifications(String username) { notifications.deleteAll(notifications.findByUserUsernameOrderByCreatedAtDesc(username)); }
    public long unreadCount(String username) { return notifications.countByUserUsernameAndReadStatusFalse(username); }
    public List<ProfileDtos.SavedRouteResponse> savedRoutes(String username) { return savedRoutes.findByUserUsernameOrderByCreatedAtDesc(username).stream().map(this::savedRouteResponse).toList(); }
    @Transactional public ProfileDtos.SavedRouteResponse saveRoute(String username, ProfileDtos.SavedRouteRequest request) { SavedRoute route = new SavedRoute(); route.setUser(user(username)); route.setSource(request.source()); route.setDestination(request.destination()); return savedRouteResponse(savedRoutes.save(route)); }
    @Transactional public void deleteRoute(String username, Long id) { savedRoutes.findById(id).filter(r -> r.getUser().getUsername().equals(username)).ifPresent(savedRoutes::delete); }

    public ProfileDtos.SecurityResponse security(String username) {
        User user = user(username);
        return new ProfileDtos.SecurityResponse(user.getUsername(), user.getEmail(),
                user.getPassword() != null && !user.getPassword().isBlank(),
                user.getLastLogin(), user.getUpdatedAt());
    }

    public List<ProfileDtos.Session> sessions(String username) {
        return sessions.findByUserUsernameOrderByLastActivityDesc(username).stream()
                .map(session -> new ProfileDtos.Session(session.getId(), session.getDevice(), session.getBrowser(), session.getIpAddress(), session.getLoginTime(), session.getLastActivity(), true)).toList();
    }
    @Transactional public void deleteSession(String username, Long id) {
        sessions.findById(id).filter(session -> session.getUser().getUsername().equals(username)).ifPresent(sessions::delete);
    }
    @Transactional public ProfileDtos.Response removeAvatar(String username) {
        User user = user(username); user.setAvatarUrl(null); return response(profiles.save(user));
    }

    private User user(String username) {
        return profiles.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private ProfileDtos.Response response(User user) {
        return new ProfileDtos.Response(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRole().name(),
            user.getAvatarUrl(), user.getCreatedAt(), user.getLastLogin(), user.getUpdatedAt(), user.getAccountStatus());
    }
    private ProfileDtos.NotificationResponse notificationResponse(Notification n) { return new ProfileDtos.NotificationResponse(n.getId(), n.getTitle(), n.getMessage(), n.getType(), n.isReadStatus(), n.getCreatedAt()); }
    private ProfileDtos.SavedRouteResponse savedRouteResponse(SavedRoute r) { return new ProfileDtos.SavedRouteResponse(r.getId(), r.getSource(), r.getDestination(), r.getCreatedAt()); }
}