package org.example.resources;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class HelloWorldResource {

  @GET
  @Path("hello-world")
  public Response helloWorld() {
    log.debug("Testing 123...");
    return Response.ok("Hello World").build();
  }

  @POST
  @Path("fileDetails")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadFile(
      @Parameter(schema = @Schema(type = "string", name = "file", format = "binary")) @FormDataParam("file") InputStream inputStream,
      @Parameter(hidden = true) @FormDataParam("file") FormDataContentDisposition formDataContentDisposition) {
    return Response.ok(formDataContentDisposition).build();
  }

}
