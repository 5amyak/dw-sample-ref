package org.example.resources;

import client.CatFactClient;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Hello World")
@RequiredArgsConstructor
@Slf4j
public class DriverResource {

  private final CatFactClient catFactClient;

  @GET
  @Path("hello")
  public Response helloWorld() {
    log.debug("Testing 123...");
    return Response.ok("World !!!").build();
  }

  @GET
  @Path("cat-fact")
  public Response catFact() {
    return Response.ok(catFactClient.getCatFact()).build();
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
