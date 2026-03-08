package nhom12.example.nhom12.security;

import lombok.RequiredArgsConstructor;
import nhom12.example.nhom12.exception.ResourceNotFoundException;
import nhom12.example.nhom12.exception.UnauthorizedException;
import nhom12.example.nhom12.model.User;
import nhom12.example.nhom12.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

  private final UserRepository userRepository;

  public User resolve(Authentication authentication) {
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Authentication is required");
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof UserDetails userDetails) {
      return findByUsername(userDetails.getUsername());
    }

    if (principal instanceof OAuth2User oAuth2User) {
      String googleId = oAuth2User.getAttribute("sub");
      if (hasText(googleId)) {
        return userRepository
            .findByGoogleId(googleId)
            .orElseGet(() -> findByNameFallback(authentication.getName()));
      }

      String email = oAuth2User.getAttribute("email");
      if (hasText(email)) {
        return userRepository
            .findByEmail(email)
            .orElseGet(() -> findByNameFallback(authentication.getName()));
      }
    }

    return findByNameFallback(authentication.getName());
  }

  public String resolveUserId(Authentication authentication) {
    return resolve(authentication).getId();
  }

  private User findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
  }

  private User findByNameFallback(String principalName) {
    if (!hasText(principalName) || "anonymousUser".equals(principalName)) {
      throw new UnauthorizedException("Authentication is required");
    }

    return userRepository
        .findByUsername(principalName)
        .or(() -> userRepository.findByEmail(principalName))
        .or(() -> userRepository.findByGoogleId(principalName))
        .orElseThrow(() -> new ResourceNotFoundException("User", "principal", principalName));
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
