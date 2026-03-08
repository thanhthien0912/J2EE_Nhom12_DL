package nhom12.example.nhom12.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

  private String frontendUrl = "http://localhost:5173";
  private String backendUrl = "http://localhost:8080";
  private List<String> corsAllowedOrigins =
      new ArrayList<>(List.of("http://localhost:5173", "http://localhost:8080"));
}
