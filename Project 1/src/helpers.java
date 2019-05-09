import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class helpers {

    public static String DecodeValue(String Value) throws UnsupportedEncodingException {
        if(Value == null){
            return Value;
        }
        System.out.println("Value Received: (" + Value + ")");
        String decoded = URLDecoder.decode(Value, StandardCharsets.UTF_8.toString());
        System.out.println("Decoded Value: (" + decoded + ")");

        //return decoded;
        return Value;
    }

    public static boolean isValidGrade(String Grade){
        if(Grade.length() > 2) return false;

        System.out.println("Grade Received: (" + Grade + ")" + " Length: " + Grade.length());

        Pattern p = Pattern.compile("^([ABCDEFIWZ\\+\\-]{1,2})$");
        Matcher m = p.matcher(Grade);
        //boolean b = m.matches();
        if(m.matches()){
            Pattern p2 = Pattern.compile("(^[EFIWZ].)");
            Matcher m2 = p2.matcher(Grade);

            return !m2.matches();
        }
        return false;
    }
    public static StringBuilder getXMLwrapper(StringBuilder XML, String ListObjectType){
        XML.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><"+ ListObjectType + ">").append("</" + ListObjectType + ">");
        return XML;
    }

    /*
    public static Response AddUpdateGradeBook(String Name, ConcurrentHashMap<String, Gradebook> GradebooksDB, boolean isPrimary) throws UnsupportedEncodingException{
        // String _grade = helpers.DecodeValue(Grade);
        String _name = helpers.DecodeValue(Name);

        String _otherServer = !isPrimary ? gradebookService.GetServerName() : secondaryService.GetServerName();
        ConcurrentHashMap<String, Gradebook> otherDB = isPrimary ? secondaryService.GetDB() : gradebookService.GetDB();

        String _thisServer = isPrimary ? gradebookService.GetServerName() : secondaryService.GetServerName();
        Gradebook _gradeBook = new Gradebook(_thisServer, _name);

        if(otherDB.containsKey(helpers.GetHashID(Name, _otherServer))) {
            return Response.status(400).entity("Gradebook exists on " + _otherServer + " Database").build();

        }else if(!GradebooksDB.containsKey(_gradeBook.getID())){
            // _gradeBook ;
            GradebooksDB.putIfAbsent(_gradeBook.getID(), _gradeBook);
        }else{
            return Response.status(400).entity("Gradebook Exists").build();
        }

        //return getGradeBookResponse(_gradeBook);
        return Response.status(200).entity(_gradeBook.getID()).build();
        //status(200).entity(getStudentXML(student)).build()
    }

*/
    public static Response CloneGradeBookToSecondary(String GradebookID) throws UnsupportedEncodingException{
        // String _grade = helpers.DecodeValue(Grade);
        //String _name = helpers.DecodeValue(Name);

        ConcurrentHashMap<String, Gradebook> primaryDB = gradebookService.GetDB();
        ConcurrentHashMap<String, Gradebook> secondaryDB = secondaryService.GetDB();

        Gradebook _existing;

        if(primaryDB.containsKey(GradebookID)){
            _existing = primaryDB.get(GradebookID);
        }else{
            return Response.status(400).entity("Gradebook does not exist").build();
        }

        Gradebook _clone = new Gradebook(_existing, secondaryService.GetServerName());

        if(secondaryDB.containsKey(_clone.getID())) {
            return Response.status(400).entity("Gradebook exists on " + secondaryService.GetServerName() + " server").build();

        }else{
            // _gradeBook ;
            _existing.setCopy(_clone.getID());
            _clone.setCopy(_existing.getID());
            secondaryDB.putIfAbsent(_clone.getID(), _clone);
        }
        //else{
          //  return Response.status(400).entity("Gradebook Exists").build();
        //}

        //return getGradeBookResponse(_gradeBook);
        return Response.status(200).entity(_clone.getID()).build();
        //status(200).entity(getStudentXML(student)).build()
    }

    public static Response AddUpdateStudentInGradeBook(String Name, String Grade, Gradebook GradeBook) throws UnsupportedEncodingException{
        ConcurrentHashMap<String, Student> _students = studentservice.GetDB();

        String _grade = helpers.DecodeValue(Grade);
        String _name = helpers.DecodeValue(Name);
        String _gbID = GradeBook.getID(); //helpers.GetHashID(_name, GradeBook); //

        String _studentID = helpers.GetHashID(_name, GradeBook.getID());

        if(Grade != null && !helpers.isValidGrade(_grade.toUpperCase())){
            return Response.status(400).entity("ERROR: Grade is Invalid").build();
        }

        Student _student;
        String _rtnString;

        if(_students.containsKey(_studentID)){
            _student = _students.get(_studentID);
            _student.setGrade(_grade);
            _rtnString = "Updated Student: " + _name + " grade to: " + _grade;

        }else{
            _student = new Student(_name, _grade, GradeBook);
            _students.putIfAbsent(_studentID, _student);
            _rtnString = "Added Student: " + _name + " to Gradebook: " + GradeBook.getName();
        }

        //return getStudentResponse(_student);
        return Response.status(200).entity(_rtnString).build();
    }

    public static String GetHashID(String GradebookName, String GradebookServer){
        return "ID" + Math.abs((GradebookName.toUpperCase() + GradebookServer.toUpperCase()).hashCode());
    }
}
