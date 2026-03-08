package nhom12.example.nhom12.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nhom12.example.nhom12.dto.response.ApiResponse;
import nhom12.example.nhom12.dto.response.ProductResponse;
import nhom12.example.nhom12.security.CurrentUserResolver;
import nhom12.example.nhom12.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

  private final WishlistService wishlistService;
  private final CurrentUserResolver currentUserResolver;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlist(
      Authentication authentication) {
    String userId = currentUserResolver.resolveUserId(authentication);
    return ResponseEntity.ok(
        ApiResponse.success(
            wishlistService.getWishlist(userId), "Wishlist retrieved successfully"));
  }

  @PostMapping("/{productId}")
  public ResponseEntity<ApiResponse<List<ProductResponse>>> toggleProduct(
      Authentication authentication, @PathVariable String productId) {
    String userId = currentUserResolver.resolveUserId(authentication);
    return ResponseEntity.ok(
        ApiResponse.success(
            wishlistService.toggleProduct(userId, productId), "Wishlist updated successfully"));
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<List<ProductResponse>>> clearWishlist(
      Authentication authentication) {
    String userId = currentUserResolver.resolveUserId(authentication);
    return ResponseEntity.ok(
        ApiResponse.success(
            wishlistService.clearWishlist(userId), "Wishlist cleared successfully"));
  }
}
