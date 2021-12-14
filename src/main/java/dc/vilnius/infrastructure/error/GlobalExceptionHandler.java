package dc.vilnius.infrastructure.error;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import java.util.UUID;
import jakarta.inject.Singleton;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
@Requires(classes = {PersistenceException.class, ExceptionHandler.class})
class GlobalExceptionHandler implements ExceptionHandler<PersistenceException, HttpResponse<JsonError>> {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Override
  public HttpResponse<JsonError> handle(HttpRequest request, PersistenceException e) {
    UUID uuid = UUID.randomUUID();
    logger.error("{}: An error occurred while saving entities {}", uuid, e.getMessage());
    JsonError error = new JsonError("Failed to save. Error id: " + uuid)
        .link(Link.SELF, Link.of(request.getUri()));

    return HttpResponse.<JsonError>badRequest().body(error);
  }

}
