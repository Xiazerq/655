import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

@Path("/secondary/")
public class secondaryService {

    private static ConcurrentHashMap<String, Gradebook> secondaryDB = new ConcurrentHashMap<String, Gradebook>();
    private String _invalidGB = "Invalid Gradebook ID";
    private static String _serverName = "secondary";

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

    @POST
    @Path("{gradeBookID: ID\\d+}")
    public Response postGradeBookGradebyID(@PathParam("gradeBookID") String gradebookID) throws UnsupportedEncodingException {
        return cloneGradeBook(gradebookID);
    }

    @PUT
    @Path("{gradeBookID: ID\\d+}")
    public Response putGradeBookGradebyID(@PathParam("gradeBookID") String gradebookID) throws UnsupportedEncodingException{
        return cloneGradeBook(gradebookID);
    }
    /*
    @POST
    @Path("{name}")
    public Response getGradeBookGrade(@PathParam("name") String name) throws UnsupportedEncodingException {
        return addUpdateGradeBook(name);
    }

    @PUT
    @Path("{name}")
    public Response updateGradeBookGrade(@PathParam("name") String name) throws UnsupportedEncodingException{
        return addUpdateGradeBook(name);
    }
    private Response addUpdateGradeBook(String Name) throws UnsupportedEncodingException{
        // String _grade = helpers.DecodeValue(Grade);
        System.out.println("Made it to Secondary Server \n\n\n\n");
        return helpers.AddUpdateGradeBook(Name, secondaryDB, false);
        /*
        String _name = helpers.DecodeValue(Name);

        Gradebook _gradeBook;

        if(!secondaryDB.containsKey(_name)){
            _gradeBook = new Gradebook("primary", _name);
            secondaryDB.putIfAbsent(_name, _gradeBook);
        }else{
            return Response.status(400).build();
        }

        return Response.status(200).build();

         * /
    }
    */
    private Response cloneGradeBook(String GradebookID) throws UnsupportedEncodingException{
        return helpers.CloneGradeBookToSecondary(GradebookID);
    }

    @DELETE
    @Path("{gradeBookID: ID\\d+}")
    public Response deleteGradeBook(@PathParam("gradeBookID") String GradebookID) throws UnsupportedEncodingException {
        Gradebook _this = GetGradebookByID(GradebookID);

        if(_this == null){
            return Response.status(400).entity(_invalidGB).build();
        }

        gradebookService.getGradebookByID(GradebookID).nullifyCopy();
        secondaryDB.remove(GradebookID);

        return Response.status(400).entity(_this.getName() + " was successfully deleted from the Secondary Server").build();
    }

    private String getGradeBookListXML(){
        return getGradeBookListXML(false);
    }

    private String getGradeBookListXML(boolean includeStudents){
        StringBuilder _gradeBookList = new StringBuilder();

        //_gradeBookList.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><student-list>");

        //for (int x = 0; x < studentDB.mappingCount(); x++) {
        for(String key : secondaryDB.keySet()){
            _gradeBookList.append(secondaryDB.get(key).getXML(includeStudents));
        }

        //_gradeBookList.append("</student-list>");

        return helpers.getXMLwrapper(_gradeBookList, "gradebook-list").toString();
    }

    public static ConcurrentHashMap<String, Gradebook> GetDB(){
        return secondaryDB;
    }

    public static Gradebook GetGradebookByID(String GradebookID){

        if(!secondaryDB.containsKey(GradebookID)) {
            return null;
        }else{
            return secondaryDB.get(GradebookID);
        }
    }

    public static String GetServerName(){
        return _serverName;
    }
}