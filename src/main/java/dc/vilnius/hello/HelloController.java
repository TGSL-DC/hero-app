package dc.vilnius.hello;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/hello")
@Secured(SecurityRule.IS_ANONYMOUS)
public class HelloController {

  @Get(produces = MediaType.TEXT_PLAIN)
  public String index() {
    return "Hello World";
  }
}