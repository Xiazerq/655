import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/gradebook/")
public class gradebookService {

    private static ConcurrentHashMap<String, Gradebook> gradeBookDB = new ConcurrentHashMap<String, Gradebook>();
    private String _invalidGB = "Invalid Gradebook ID";
    private static String _serverName = "primary";

    @GET
    @Produces("application/xml")
    //@Produces("application/text")
    public String getGradeBooks(){
       // return "Hello Gradebook!";
        return getGradeBookListXML(false);
    }

    @GET
    @Produces("application/xml")
    @Path("{gradeBookID: ID\\d+}")
    //@Produces("application/text")
    public String getGradeBooks(@PathParam("gradeBookID") String name){
        // return "Hello Gradebook!";
        return getGradeBookListXML(true);
    }

    /*
    @GET
    @Path("{name}")
    @Produces("application/xml")
    public Response getGradeBook(@PathParam("name") String name) throws UnsupportedEncodingException {

        String decoded = name;//decodeValue(name);

        if(!gradeBookDB.containsKey(decoded)){
            return Response.status(404).build();
            //_gradeBook = new GradeBook(decoded);
            //gradeBookDB.putIfAbsent(name, _gradeBook);
        }

        //GradeBook gradeBook = gradeBookDB.get(name);

        return getGradeBookResponse(gradeBookDB.get(name));
    }
*/

    @GET
    @Produces("application/xml")
    @Path("{gradeBookID: ID\\d+}/student")
    public String postStudent(@PathParam("gradeBookID") String GradeBookID) throws UnsupportedEncodingException{
        Gradebook _this = getGradebookByID(GradeBookID);

        if(_this == null){
            return helpers.getXMLwrapper(new StringBuilder(_invalidGB), "gradebook").toString();
        }

        return helpers.getXMLwrapper(new StringBuilder(_this.getStudentsXML()), "gradebook").toString();

       // return addUpdateStudent(GradeBookID);
    }

    @POST
    @Path("{gradeBookID: ID\\d+}/student/{studentName}")
    public Response postStudent(@PathParam("gradeBookID") String GradeBookID, @PathParam("studentName") String StudentName) throws UnsupportedEncodingException{
        return addUpdateStudent(GradeBookID, StudentName, null);
    }

    @PUT
    @Path("{gradeBookID: ID\\d+}/student/{studentName}")
    public Response putStudent(@PathParam("gradeBookID") String GradeBookID, @PathParam("studentName") String StudentName) throws UnsupportedEncodingException{
        return addUpdateStudent(GradeBookID, StudentName, null);
    }

    @POST
    @Path("{gradeBookID: ID\\d+}/student/{studentName}/grade/{grade}")
    public Response postStudentGrade(@PathParam("gradeBookID") String GradeBookID, @PathParam("studentName") String StudentName, @PathParam("grade") String Grade) throws UnsupportedEncodingException{
        return addUpdateStudent(GradeBookID, StudentName, Grade);
    }

    @PUT
    @Path("{gradeBookID: ID\\d+}/student/{studentName}/grade/{grade}")
    public Response putStudentGrade(@PathParam("gradeBookID") String GradeBookID, @PathParam("studentName") String StudentName, @PathParam("grade") String Grade) throws UnsupportedEncodingException{
            return addUpdateStudent(GradeBookID, StudentName, Grade);
    }

    private Response addUpdateStudent(String GradebookID, String Name,String Grade) throws UnsupportedEncodingException {
        Gradebook _this = getGradebookByID(GradebookID);

        if(_this == null){
            return Response.status(400).entity(_invalidGB).build();
        }
        /*
        if(!gradeBookDB.containsKey(GradebookID)) {
        }else{
            _this = gradeBookDB.get(GradebookID);
        }
        */

        return helpers.AddUpdateStudentInGradeBook(Name, Grade, _this);
    }

    @DELETE
    @Path("{gradeBookID: ID\\d+}")
    public Response deleteGradeBook(@PathParam("gradeBookID") String GradebookID) throws UnsupportedEncodingException {
        Gradebook _this = getGradebookByID(GradebookID);

        if(_this == null){
            return Response.status(400).entity(_invalidGB).build();
        }

        gradeBookDB.remove(GradebookID);

        return Response.status(400).entity(_this.getName() + " was successfully deleted from the Primary Server").build();
    }

    @POST
    @Path("{name}")
    public Response getGradeBookGrade(@PathParam("name") String name) throws UnsupportedEncodingException{
        return addUpdateGradeBook(name);
    }

    @PUT
    @Path("{name}")
    public Response updateGradeBookGrade(@PathParam("name") String name) throws UnsupportedEncodingException{
        return addUpdateGradeBook(name);
    }

    private Response addUpdateGradeBook(String Name) throws UnsupportedEncodingException{
       // String _grade = helpers.DecodeValue(Grade);
        /* * /
        if(otherDB.containsKey(helpers.GetHashID(Name, _otherServer))) {
            return Response.status(400).entity("Gradebook exists on " + _otherServer + " Database").build();

        }else if(!GradebooksDB.containsKey(_gradeBook.getID())){
            // _gradeBook ;
            GradebooksDB.putIfAbsent(_gradeBook.getID(), _gradeBook);
        }

        //return helpers.AddUpdateGradeBook(Name, gradeBookDB, true);

        /**/
        String _name = helpers.DecodeValue(Name);

        Gradebook _gradeBook;

        if(!gradeBookDB.containsKey(helpers.GetHashID(_name, _serverName))){
            _gradeBook = new Gradebook(_serverName, _name);
            gradeBookDB.putIfAbsent(_gradeBook.getID(), _gradeBook);
        }else{
            return Response.status(400).entity("Gradebook Already Exists").build();
        }

        return Response.status(200).entity(_gradeBook.getID()).build();
        //status(200).entity(getStudentXML(student)).build()

         /**/
    }

    private Response getGradeBookListResponse(){
        return Response.status(200).entity(getGradeBookListXML(false)).build();
    }

    private String getGradeBookListXML(boolean includeStudents){
        StringBuilder _gradeBookList = new StringBuilder();

        //_gradeBookList.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><student-list>");

        //for (int x = 0; x < studentDB.mappingCount(); x++) {
        for(String key : gradeBookDB.keySet()){
            _gradeBookList.append(gradeBookDB.get(key).getXML(includeStudents));
        }

        //_gradeBookList.append("</student-list>");

        return helpers.getXMLwrapper(_gradeBookList, "gradebook-list").toString();
    }

    public static ConcurrentHashMap<String, Gradebook> GetDB(){
        return gradeBookDB;
    }

    public static Gradebook getGradebookByID(String GradebookID){

        if(!gradeBookDB.containsKey(GradebookID)) {
            return null;
        }else{
            return gradeBookDB.get(GradebookID);
        }
    }

    public static String GetServerName(){
        return _serverName;
    }
}
