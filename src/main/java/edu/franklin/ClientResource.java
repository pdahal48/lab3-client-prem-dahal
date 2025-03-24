package edu.franklin;

import org.jboss.logging.Logger;

import io.quarkus.grpc.GrpcClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/grpc/students")
public class ClientResource {

    @Inject
    @GrpcClient("student-service")
    StudentServiceGrpc.StudentServiceBlockingStub studentService;
    private static final Logger LOGGER = Logger.getLogger(ClientResource.class);

    @POST
    public Response addStudent(StudentEntity studentEntity) {
        LOGGER.debug("Adding a new student: " + studentEntity.name);
        try {
            Student student = Student.newBuilder()
                .setName(studentEntity.name)
                .setPhone(studentEntity.phone)
                .setAge(studentEntity.age)
                .setZip(studentEntity.zip)
                .build();

            StudentID studentID = studentService.addStudent(student);
            return Response.ok(studentID.getValue()).build();
        } catch (Exception e) {
            LOGGER.error("Error creating student", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creating student: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") String id) {
        try {
            Student student = studentService.getStudent(StudentID.newBuilder().setValue(id).build());
            return Response.ok(student.toString()).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching student", e);
            return Response.status(Response.Status.NOT_FOUND)
                .entity("Student not found: " + e.getMessage()).build();
        }
    }

    public static class StudentEntity {
        public String id;
        public String name;
        public String phone;
        public String age;
        public String zip;
    }
}
