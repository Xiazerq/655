import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

public class Gradebook {

    private String _id;
    private String _name;
    private ConcurrentHashMap<String, Student> _students;
    private String _copyID;

    public Gradebook() {
        _students = new ConcurrentHashMap<String, Student>();
    }

    /**
     * Constructor for Gradebook
     * @param GradebookServer: Name of the server the GB is located
     * @param _name: Name of the Gradebook
     */
    public Gradebook(String GradebookServer, String _name) {
        this.setName(_name);
        this.setID(GradebookServer);
    }

    public Gradebook(Gradebook Clone, String GradebookServer){
        this.setName(Clone.getName());
        this.setID(GradebookServer);
    }

    public Gradebook(String _name) {
        this.setName(_name);
    }

    public String getName() {
        return _name;
    }

    public void setName(String Name) {
        this._name = Name;
    }

    public String getID() {
        return _id;
    }

    public void setID(String GradebookServer) {
        this._id = helpers.GetHashID(this._name, GradebookServer);//Math.abs((this._name + Gradebook).hashCode()) + "";
    }

    public String getCopyID() {
        return _copyID;
    }

    public void setCopy(String CopyID) {
        this._copyID = CopyID;
    }

    public void nullifyCopy() {
        this._copyID = null;
    }

    public String getXML(boolean IncludeStudents){
        return "<Gradebook>\n" +
                "\t\t<id>\n" +
                this.getID() +
                "\t\t</id>\n" +
                "\t\t<name>\n" +
                this.getName() +
                "\t\t</name>\n" +
                (IncludeStudents ? this.getStudentsXML() : "") +
                "\t</Gradebook>";
    }

    public String getStudentsXML(){

        StringBuilder _rtn = new StringBuilder();

        /*
        _rtn.append("\t\t<id>\n" +
                this.getID() +
                "\t\t</id>\n" +
                "\t\t<name>\n" +
                this.getName() +
                "\t\t</name>\n" +
                "\t\t<students>\n");

         */
        _rtn.append("\t\t<students>\n");

        ConcurrentHashMap<String, Student> _sDB = studentservice.GetDB();
        for(String _key : _sDB.keySet()){
            Student _stu = _sDB.get(_key);
            if(_stu.getGradeBookID().equals(this._id) || _stu.getGradeBookID().equals(this._copyID)){
                _rtn.append(_stu.getXML());
            }
        }

        return _rtn.append("\t</students>").toString();
    }

}
